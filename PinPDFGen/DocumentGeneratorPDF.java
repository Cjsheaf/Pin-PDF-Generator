package PinPDFGen;

import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Point;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;

/**
 * Write a description of class DocumentGeneratorPDF here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DocumentGeneratorPDF {
    private List<PinTextLine> pinList;
    private int currentImage; //Used by getNextImageIndex()
    private String imageDirectory;
    
    public DocumentGeneratorPDF(List<PinTextLine> pinList, String imageDirectory) {
        this.pinList = pinList;
        currentImage = 0;
        this.imageDirectory = imageDirectory;
    }
    
    private void addContentToDocument(PDDocument document) throws IOException {
        int maxNumberOfPages = (pinList.size() + 1) / 2;
        int currentPage = 0;
        int currentPinIndex = 0;
        
        PDPage page = null;
        PDPageContentStream contentStream = null;
        File currentImageFile = null;
        BufferedImage currentImage = null;
        PDXObjectImage pdxImage = null;
        
        for (int i = 0; i < maxNumberOfPages; i++) {
            page = new PDPage();
            
            contentStream = new PDPageContentStream(document, page);
            
            System.out.println("Generating page: " + i);
            
            try {
                currentPinIndex = getNextImageIndex();
                currentImageFile = new File(imageDirectory + "\\" + pinList.get(currentPinIndex).pinID + ".png");
                if (currentImageFile.exists() == true) {
                    currentImage = ImageIO.read(currentImageFile);
                    
                    contentStream.beginText();
                    contentStream.moveTextPositionByAmount(50, 750);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.drawString(pinList.get(currentPinIndex).toString());
                    contentStream.endText();
                    
                    pdxImage = new PDJpeg(document, currentImage);
                    
                    contentStream.drawXObject(pdxImage, 50, 410, pdfPageWidth - 100, 330);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (RuntimeException exception) {
                break; //There are no more images, end the loop.
            }
            
            //==========Second Half Of Page==========//
            
            try {
                currentPinIndex = getNextImageIndex();
                currentImageFile = new File(imageDirectory + "\\" + pinList.get(currentPinIndex).pinID + ".png");
                if (currentImageFile.exists() == true) {
                    currentImage = ImageIO.read(currentImageFile);
                    
                    contentStream.beginText();
                    contentStream.moveTextPositionByAmount(50, 390);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.drawString(pinList.get(currentPinIndex).toString());
                    contentStream.endText();
                    
                    pdxImage = new PDJpeg(document, currentImage);
                    
                    contentStream.drawXObject(pdxImage, 50, 50, pdfPageWidth - 100, 330);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (RuntimeException exception) {
                break; //There are no more images, end the loop.
            }
            
            contentStream.close();
            document.addPage(page);
        }
    }
    
    public void generateDocument(String fileName) {
        PDDocument document = null;
        try {
            document = new PDDocument();
            
            addContentToDocument(document);
            
            document.save(fileName);
            document.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (COSVisitorException exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Returns the next index number in pinList that has an image available.
     * Not all image entries are valid, and any invalid images are ignored.
     */
    private int getNextImageIndex() throws RuntimeException {
        File imageFile = null;
        
        while (imageFile == null && currentImage < pinList.size()) {
            imageFile = new File(imageDirectory + "\\" + pinList.get(currentImage).pinID + ".png");
            if (imageFile.exists() == true) {
                return currentImage++;
            } else {
                imageFile = null;
                currentImage++;
            }
        }
        
        throw new RuntimeException("There is no next image to get");
    }
    
    private static final int pdfPageWidth = 612; //In pdf "points"
    private static final int pdfPageHeight = 792; //In pdf "points"
}