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
 * Write a description of class ImageFetcher here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ImageFetcher {
    public static void fetchPictures(List<PinTextLine> parsedCorpus, String storageDirectory) {
        System.out.println("Fetching Images...");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < parsedCorpus.size(); i++) {
            executor.execute(new Runnable() {
                private PinTextLine pinInfo;
                private String storageDirectory;
                
                public Runnable init(PinTextLine pinInfo, String storageDirectory) {
                    this.pinInfo = pinInfo;
                    this.storageDirectory = storageDirectory;
                    return this;
                }
                
                public void run() {
                    try {
                        Process imageCapProcess = new ProcessBuilder(
                            storageDirectory + "\\phantomjs.exe",
                            "imageCapture.js",
                            "http://gallery.pinpics.com/cgi-bin/pin.cgi?pin=" + pinInfo.pinID,
                            "PinPics\\" + pinInfo.pinID + ".png"
                        ).start();
                        imageCapProcess.waitFor();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }.init(parsedCorpus.get(i), storageDirectory));
        }
        
        executor.shutdown();
        while (executor.isTerminated() == false) {
            //Infinite loop until the process is complete.
        }
        System.out.println("Image Fetching Complete.");
    }
    
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
            parsedCorpus.add(new PinTextLine(lineCorpus.get(i))); //PinTextLine automatically parses each line into the appropriate fields inside its instance
        }
        
        Collections.sort(parsedCorpus);
        return parsedCorpus;
    }    
    
    public static void main(String[] args) { //args[1] will be the text file to read from. args[2] will be the directory containing all of the appropriate image files
        File textFile = new File(args[0]);
        if (textFile.exists() == false) {
            throw new IllegalArgumentException("Could not open the given text file!");
        }
        
        List<String> lineCorpus = loadText(textFile);
        List<PinTextLine> parsedCorpus = parseText(lineCorpus);
        fetchPictures(parsedCorpus, "C:\\PhantomJS");
    }
}