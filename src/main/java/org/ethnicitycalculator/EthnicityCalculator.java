package org.ethnicitycalculator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ethnicitycalculator.service.FamilyTreeService;
import org.ethnicitycalculator.util.GedcomFileProcessor;
import org.gedcomx.Gedcomx;
import org.gedcomx.conclusion.Relationship;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EthnicityCalculator {

    public static String getEthnicityResults(InputStream fileInputStream, String filename, List<String> ignoredBirthplaces) throws IOException, SAXParseException {
        PrintStream fileStream = new PrintStream("src/main/resources/logs.txt");
        System.setOut(fileStream);

        StringBuilder liveResults = new StringBuilder();
        String fileExtension = FilenameUtils.getExtension(filename);
        String filepath = "src/main/resources/targetFile." + fileExtension;

        File targetFile = new File(filepath);
        FileUtils.copyInputStreamToFile(fileInputStream, targetFile);
        fileInputStream.close();

        if (!(fileExtension.equals("ged") || fileExtension.equals("gedx"))) {
            System.out.println("Invalid file extension. Must be either .ged or .gedx. ");
            return null;
        }
        if (fileExtension.equals("ged")) {
            GedcomFileProcessor.convertToGedcomx(filepath);
        }
        File file = new File("src/main/resources/mygedx.gedx");

        Gedcomx familyTree = GedcomFileProcessor.getGedcomTree(file);
        FamilyTreeService familyTreeService = new FamilyTreeService(familyTree, ignoredBirthplaces);

        List<Relationship> parents = familyTreeService.getRootPersonParents();
        if (parents.size() > 2) {
            System.out.println("Detected " + parents.size() + " parents. Calculation will proceed, " +
                    " but the ethnicity breakdown will add up to over 100 because it is unknown" +
                    " which two parents are biological.");
        }

        Map<String, Double> results = familyTreeService.findImmigrantAncestors(parents);
        ArrayList<String> endOfLineItems = familyTreeService.getEndOfLineResults();

        Map<String, Double> sortedResults = results.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println(targetFile.getAbsolutePath());
        Files.deleteIfExists(Path.of(filepath));
        return prettyPrintResults(sortedResults, endOfLineItems);
    }

    public static String prettyPrintResults(Map<String, Double> mathResults, ArrayList<String> endOfLineItems) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Double> entry : mathResults.entrySet()){
            sb.append("\n");
            sb.append(String.format("%,.2f", entry.getValue())).append("% ").append(entry.getKey());
            if (endOfLineItems.contains(entry.getKey())) {
                sb.append(" (End of line)");
            }
        }
        double sum = 0;
        for(Map.Entry<String, Double> entry : mathResults.entrySet()){
            sum = sum + entry.getValue();
        }
        String total = "\ntotal: " + String.format("%,.2f", sum) + "%";
        sb.append(total);

        return sb.toString();
    }
}
