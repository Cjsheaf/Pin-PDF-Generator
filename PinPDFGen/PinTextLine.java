package PinPDFGen;

import java.util.Scanner;

/**
 * A class which takes in a line of structured text and converts it into correct member fields.
 * 
 * @author Christopher Sheaf
 */
public class PinTextLine implements Comparable<PinTextLine> {
    public String leadingTerm;
    public String pinID;
    public String description;
    
    public PinTextLine(String rawLineData) {
        Scanner scanner = new Scanner(rawLineData);
        scanner.useDelimiter("\t");
        
        leadingTerm = scanner.next();
        pinID = scanner.next();
        description = scanner.next();
    }
    
    public int compareTo(PinTextLine otherLine) {
        return new Integer(Integer.parseInt(this.pinID)).compareTo(
            new Integer(Integer.parseInt(otherLine.pinID))
        );
    }
    
    @Override
    public String toString() {
        return leadingTerm + " " + pinID + " " + description;
    }
}