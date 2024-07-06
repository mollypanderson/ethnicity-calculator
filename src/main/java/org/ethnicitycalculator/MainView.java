package org.ethnicitycalculator;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import org.ethnicitycalculator.util.DefaultIgnoredBirthplaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout {
    Logger logger = LoggerFactory.getLogger(MainView.class);

    public MainView() throws SAXParseException, IOException {
        DefaultIgnoredBirthplaces birthplaces = new DefaultIgnoredBirthplaces();
        ArrayList<String> customIgnoredBirthplaces = new ArrayList<>();
        ArrayList<String> resultsList = new ArrayList<>();
        H2 title = new H2("Ethnicity Calculator");

        Binder<List<String>> resultsListBinder = new Binder<>();
        resultsListBinder.setBean(resultsList);

        Paragraph hint = new Paragraph("File must be in either gedcom (.ged) or gedcomx (.gedx) format. ");

        // editable results area
        VerticalLayout resultsArea = new VerticalLayout();

        Binder<List<String>> resultsBinder = new Binder<>();
        resultsBinder.setBean(resultsList);

        Grid<String> resultsGrid = new Grid<>();
        resultsGrid.addColumn(String::toString);
        resultsGrid.setItems(resultsList);
        resultsGrid.addColumn(createActionRenderer()).setFrozenToEnd(true)
                .setAutoWidth(true).setFlexGrow(0);
        resultsGrid.setSelectionMode(Grid.SelectionMode.MULTI);


        //edit button
        Button editButton = new Button("Edit ignored birthplaces", e -> {
          //  resultsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        });
        Button saveButton = new Button("Save");
        editButton.setVisible(false);

        // upload file
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".ged", ".gedx");

        Button calculateButton = new Button("Calculate (new)");
        calculateButton.setEnabled(false);

        AtomicReference<String> fileName = new AtomicReference<>();
        final InputStream[] fileData = new InputStream[1];
        upload.addSucceededListener(event ->  {
            fileData[0] = buffer.getInputStream();
            fileName.set(event.getFileName());
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();
            calculateButton.setEnabled(true);
            // do something to process file?
        });

        Paragraph totalPercent = new Paragraph();
        AtomicInteger clicks = new AtomicInteger();
        StringBuilder ethResults = new StringBuilder();
        calculateButton.addClickListener(e -> {
            if (resultsListBinder.validate().isOk()) {
                ethResults.setLength(0);
                resultsList.clear();
                calculateButton.setText("Recalculate " + clicks.getAndIncrement());
                // get results upon button click
                System.out.println("ignored places added: " + customIgnoredBirthplaces);
                logger.info("HELLO CUSTOM TEST LOG");
                customIgnoredBirthplaces.replaceAll(String::toLowerCase);
                try {
                    ethResults.append(new EthnicityResultsPrinterService().getEthnicityResults(fileData[0], fileName.toString(), customIgnoredBirthplaces));
                    buffer.getInputStream().close();
                } catch (SAXParseException | IOException ex) {
                    System.out.println("error!!!!");
                    throw new RuntimeException(ex);
                }
                // update result list from new calculated results
                resultsList.addAll(Arrays.stream(ethResults.toString().split("\\n")).collect(Collectors.toCollection(ArrayList::new)));
                totalPercent.setText(resultsList.get(resultsList.size() - 1));
                resultsList.remove(resultsList.size() - 1);

             //   listBox.add("hi" + clicks);
                editButton.setVisible(true);
                resultsGrid.getDataProvider().refreshAll();
            }
        });

        //start with hidden cancel and save buttons
        Button cancelButton = new Button("Cancel");
        cancelButton.setVisible(false);

        saveButton.setVisible(false);

        editButton.addClickListener(e -> {
            saveButton.setVisible(true);
            saveButton.setEnabled(false);
            cancelButton.setVisible(true);
            editButton.setVisible(false);
            }
        );

//        listBox.addValueChangeListener(e -> {
////            String selectedCountriesText = e.getValue().stream()
////                    .map(Country::getName).collect(Collectors.joining(", "));
////
////            selectedCountries.setValue(selectedCountriesText);
//        });

        Binder<List<String>> customIgnoredBirthplacesListBinder = new Binder<>();
        customIgnoredBirthplacesListBinder.setBean(customIgnoredBirthplaces);

        customIgnoredBirthplaces.addAll(birthplaces.getAll());
        // create a birthplaces to ignore addable and deletable list with default set list
        Grid<String> customIgnoredBirthplacesGrid = new Grid<>();
        customIgnoredBirthplacesGrid.addColumn(String::toString);
        customIgnoredBirthplacesGrid.setItems(customIgnoredBirthplaces);
        customIgnoredBirthplacesGrid.addColumn(createActionRenderer()).setFrozenToEnd(true)
                .setAutoWidth(true).setFlexGrow(0);
        TextField newLocationItemTextField = new TextField();
        newLocationItemTextField.setMinLength(1);

        Button addButton = new Button("Add", e -> {
            if (customIgnoredBirthplacesListBinder.validate().isOk()) {
                customIgnoredBirthplaces.add(newLocationItemTextField.getValue());
                newLocationItemTextField.clear();
                customIgnoredBirthplacesGrid.getDataProvider().refreshAll();
            }
        });

        cancelButton.addClickListener(e -> {
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
        });

        List<String> birthplacesToIgnore = new ArrayList<>();
        resultsGrid.addSelectionListener(selection -> {
             birthplacesToIgnore.clear();
             birthplacesToIgnore.addAll(selection.getAllSelectedItems().stream().toList());
        });

        saveButton.addClickListener(e -> {
            // update buttons
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);

            // update custom ignored list area
            customIgnoredBirthplaces.addAll(birthplacesToIgnore);
            customIgnoredBirthplacesGrid.getDataProvider().refreshAll();
            System.out.println("adding places to birthplaces to ignore list: " + birthplacesToIgnore);

            //refresh main results list panel
            resultsList.removeIf(country -> !birthplacesToIgnore.contains(country)); // should be filtered to remove new ignored places
            resultsGrid.getDataProvider().refreshAll();
        });

        resultsArea.add(resultsGrid);

        VerticalLayout birthplacesCrudArea = new VerticalLayout();
        birthplacesCrudArea.add(customIgnoredBirthplacesGrid);
        birthplacesCrudArea.add(newLocationItemTextField);
        birthplacesCrudArea.add(addButton);

        // put the birthplaces crud area inside expandablePanel
        Details expandablePanel = new Details("Birthplaces to ignore in calculation", birthplacesCrudArea);
        expandablePanel.setOpened(true);

        // Birthplaces to ignore in calculation:
        // These locations will be considered non-foreign - the tool will skip past items in
        // this list and continue to traverse the tree until it finds a "foreign" birthplace.
        // Example: You have a 9th ancestor born in England but the 5th ancestor's birthplace is "MA".
        // If you add MA to the ignored locations list it will continue traversing past the MA result and identify "England"
        // as the ethnicity for that branch.

        Button button = new Button("Calculate");

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        setDefaultHorizontalComponentAlignment(
                Alignment.CENTER
        );

        // CSS
        addClassName("upload-box");
        addClassName("editCancelSave");
        addClassName("calculate");
        addClassName("expandable-panel");
        upload.setClassName("upload-box");
        editButton.setClassName("editCancelSave");
        saveButton.setClassName("editCancelSave");
        cancelButton.setClassName("editCancelSave");
        button.setClassName("calculate");
        expandablePanel.setClassName("expandable-panel");

        // Display components
        add(title, hint, upload, calculateButton, editButton, saveButton, cancelButton, resultsArea, totalPercent, expandablePanel);
    }

    private static Renderer<String> createActionRenderer() {
        return LitRenderer.<String> of(
                "<vaadin-button theme=\"tertiary-inline\">Edit</vaadin-button>");
    }
}
