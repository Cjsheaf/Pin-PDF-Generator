package PinPDFGen;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Christopher Sheaf
 */
public class Main {
    public static List<String> loadText(File textFile) {
        List<String> lineCorpus = new ArrayList<String>();
        Scanner scanner;
        
        try {
            scanner = new Scanner(textFile);
            scanner.useDelimiter("\n");
        
            while(scanner.hasNext()) {
                lineCorpus.add(scanner.next());
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        
        return lineCorpus;
    }
    
    public static List<PinTextLine> parseText(List<String> lineCorpus) {
        List<PinTextLine> parsedCorpus = new ArrayList<PinTextLine>(lineCorpus.size());
        
        for(int i = 0; i < lineCorpus.size(); i++) {
            parsedCorpus.add(new PinTextLine(lineCorpus.get(i)));
        }
        
        Collections.sort(parsedCorpus);
        return parsedCorpus;
    }
    
    /**
     * args[1] will be the text file to read from.
     * args[2] will be the directory containing all of the appropriate image files.
     * args[3] is the name of the .pdf file to generate.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Need three arguments. The first is the path of the text file to read from. The second is the path to the directory containing the appropriate image files. The third is the name of the .pdf file to generate.");
            throw new IllegalArgumentException("Not enough arguments!");
        }
        
        File textFile = new File(args[0]);
        if (textFile.exists() == false) {
            throw new IllegalArgumentException("Could not open the given text file!");
        }
        
        List<String> lineCorpus = loadText(textFile);
        List<PinTextLine> parsedCorpus = parseText(lineCorpus);
        
        DocumentGeneratorPDF documentGen = new DocumentGeneratorPDF(parsedCorpus, args[2]);
        documentGen.generateDocument(args[2]);
    }
}