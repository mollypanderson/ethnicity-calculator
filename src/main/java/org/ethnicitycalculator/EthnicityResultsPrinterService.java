package org.ethnicitycalculator;

import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EthnicityResultsPrinterService {

    public String getEthnicityResults(InputStream fileInputStream, String filepath, List<String> ignoredBirthplaces) throws SAXParseException, IOException {
        return EthnicityCalculator.getEthnicityResults(fileInputStream, filepath, ignoredBirthplaces);
    }
}
