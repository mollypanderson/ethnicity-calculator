package org.ethnicitycalculator;

import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;

public class EthnicityResultsPrinterService {

    public String getEthnicityResults(InputStream fileInputStream, String filepath) throws SAXParseException, IOException {
        return EthnicityCalculator.getEthnicityResults(fileInputStream, filepath);
    }
}
