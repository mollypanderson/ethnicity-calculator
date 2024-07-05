package org.ethnicitycalculator;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() throws SAXParseException, IOException {
        DefaultIgnoredBirthplaces birthplaces = new DefaultIgnoredBirthplaces();
        ArrayList<String> customIgnoredBirthplaces = new ArrayList<>();
        H2 title = new H2("Ethnicity Calculator");

        Paragraph hint = new Paragraph("File must be in either gedcom (.ged) or gedcomx (.gedx) format. ");

        // upload file
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".ged", ".gedx");
        final InputStream[] fileData = new InputStream[1];
        AtomicReference<String> fileName = new AtomicReference<>();
        upload.addSucceededListener(event ->  {
            fileData[0] = buffer.getInputStream();
            fileName.set(event.getFileName());
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();
        });

        //live results
//        TextArea liveResults = new TextArea();
//        liveResults.setMaxHeight("150px");
//        liveResults.setLabel("Processing file...");

        // editable results area
        MultiSelectListBox<String> listBox = new MultiSelectListBox<>();
        listBox.setAriaLabel("Results");
        listBox.setWidthFull();
        listBox.setReadOnly(true);

        //edit button
        Button editButton = new Button("Exclude locations from calculation", e -> {
            listBox.setReadOnly(false);
        });
        editButton.setVisible(false);

        Binder<List<String>> binder = new Binder<>();
        binder.setBean(customIgnoredBirthplaces);

        customIgnoredBirthplaces.addAll(birthplaces.getAll());
        // create a birthplaces to ignore addable and deletable list with default set list
        Grid<String> grid = new Grid<>();
        grid.addColumn(String::toString);
        grid.setItems(customIgnoredBirthplaces);
        grid.addColumn(createActionRenderer()).setFrozenToEnd(true)
                .setAutoWidth(true).setFlexGrow(0);
        TextField newLocationItemTextField = new TextField();
        newLocationItemTextField.setMinLength(1);

        Button addButton = new Button("Add", e -> {
            if (binder.validate().isOk()) {
                customIgnoredBirthplaces.add(newLocationItemTextField.getValue());
                newLocationItemTextField.clear();
                grid.getDataProvider().refreshAll();
            }
        });

        VerticalLayout birthplacesCrudArea = new VerticalLayout();
        birthplacesCrudArea.add(grid);
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

        Button button = new Button("Calculate", e -> {
            try {
                // get results upon button click
                String ethResults = new EthnicityResultsPrinterService().getEthnicityResults(fileData[0], fileName.get(), customIgnoredBirthplaces);
                ArrayList<String> resultList = Arrays.stream(ethResults.split("\\n")).collect(Collectors.toCollection(ArrayList::new));
                resultList.remove(resultList.size() - 1);
                listBox.setItems(resultList);
                editButton.setVisible(true);
                customIgnoredBirthplaces.clear();
            } catch (SAXParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        setDefaultHorizontalComponentAlignment(
                Alignment.CENTER
        );

        // CSS
        addClassName("upload-box");
        addClassName("edit");
        addClassName("calculate");
        addClassName("expandable-panel");
        upload.setClassName("upload-box");
        editButton.setClassName("edit");
        button.setClassName("calculate");
        expandablePanel.setClassName("expandable-panel");

        // Display components
        add(title, hint, upload, button, editButton, listBox, expandablePanel);
    }

    private static Renderer<String> createActionRenderer() {
        return LitRenderer.<String> of(
                "<vaadin-button theme=\"tertiary-inline\">Edit</vaadin-button>");
    }
}
