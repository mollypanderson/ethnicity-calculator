package org.ethnicitycalculator;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.xml.sax.SAXParseException;
import com.vaadin.flow.component.upload.UploadI18N;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() throws SAXParseException, IOException {
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
        TextArea liveResults = new TextArea();
        liveResults.setMaxHeight("150px");
        liveResults.setLabel("Processing file...");

        // button and results generator
        TextArea percentResults = new TextArea();
        percentResults.setLabel("Results");
        percentResults.setWidthFull();
        percentResults.setReadOnly(true);
        Button button = new Button("Calculate", e -> {
            try {
                // get results upon button click
                String ethResults = new EthnicityResultsPrinterService().getEthnicityResults(fileData[0], fileName.get());
                percentResults.setValue(ethResults);
                add(new Paragraph(new EthnicityResultsPrinterService().getEthnicityResults(fileData[0], fileName.get())));
            } catch (SAXParseException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        addClassName("centered-content");
        setDefaultHorizontalComponentAlignment(
                Alignment.CENTER
        );

        add(title, hint, upload, button, percentResults);
    }
}
