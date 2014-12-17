// Konstantin Stekhov
// Assignment 1
// ReadImage class
// This class reads images and creates the color code matrix and intensities matrix. After that
// the class writes down the intensities and color codes to the text files.

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import java.awt.Graphics;
import java.awt.Color;

public class ReadImage {
    int imageCount = 1; // number of images
    double intensityBins [] = new double [26]; // intensities for one image
    double intensityMatrix [][] = new double[101][26]; // intensities for all images
    double colorCodeBins [] = new double [64]; // color codes for one image
    double colorCodeMatrix [][] = new double[100][64]; // color codes for all images

    /*Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
     * getColorCode methods are called.
    */
    public ReadImage() {
        while(imageCount < 101){
            try {
                ImageIcon icon;
                icon = new ImageIcon(getClass().getResource("images/" + imageCount + ".jpg"));
                // Converting icon into BufferedImage
                if(icon != null){
                    BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics g = bi.createGraphics();
                    // paint the Icon to the BufferedImage.
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();
                    // getting the intensity and filling up the intensity matrix
                    intensityBins = getIntensity(bi, icon.getIconHeight(), icon.getIconWidth());
                    for(int i = 0; i < intensityBins.length; i++){
                        intensityMatrix[imageCount][i+1] = intensityBins[i];
                    }
                    // getting the color code and filling up the color code matrix
                    colorCodeBins = getColorCode(bi, icon.getIconHeight(), icon.getIconWidth());
                    for(int i = 0; i < colorCodeBins.length; i++){
                        colorCodeMatrix[imageCount-1][i] = colorCodeBins[i];
                    }
                } else {
                    // if file is not fund then exception
                    throw new FileNotFoundException("Error occurred when reading the file.");
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            imageCount++;
        }
        writeIntensity();
        writeColorCode();
    }

    // This function gets the intensity
    // Returns the intensity bins for one image
    public double[] getIntensity(BufferedImage image, int height, int width){
        int[] dataBuffInt = image.getRGB(0, 0, width, height, null, 0, width);
        double[] temp = new double[25];
        for(int i = 0; i < dataBuffInt.length; i++){
            Color c = new Color(dataBuffInt[i]);
            double localIntensity = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
            // getting the intensity values for 25 bins
//            for (int j = 0; j <= 230; j = j + 10) {
//                if (localIntensity > j && localIntensity <= j + 10) {
//                    temp[j/10]++;
//                }
//            }
//            // get the intensity value for the last bin
//            if(localIntensity >= 240 && localIntensity <= 255){
//                temp[24]++;
//            }
            if(localIntensity >= 0 && localIntensity < 10){
                temp[0]++;
            } else if(localIntensity >= 10 && localIntensity < 20){
                temp[1]++;
            } else if(localIntensity >= 20 && localIntensity < 30){
                temp[2]++;
            } else if(localIntensity >= 30 && localIntensity < 40){
                temp[3]++;
            } else if(localIntensity >= 40 && localIntensity < 50){
                temp[4]++;
            } else if(localIntensity >= 50 && localIntensity < 60){
                temp[5]++;
            } else if(localIntensity >= 60 && localIntensity < 70){
                temp[6]++;
            } else if(localIntensity >= 80 && localIntensity < 90){
                temp[7]++;
            } else if(localIntensity >= 90 && localIntensity < 100){
                temp[8]++;
            } else if(localIntensity >= 100 && localIntensity < 110){
                temp[9]++;
            } else if(localIntensity >= 110 && localIntensity < 120){
                temp[10]++;
            } else if(localIntensity >= 120 && localIntensity < 130){
                temp[11]++;
            } else if(localIntensity >= 130 && localIntensity < 140){
                temp[12]++;
            } else if(localIntensity >= 140 && localIntensity < 150){
                temp[13]++;
            } else if(localIntensity >= 150 && localIntensity < 160){
                temp[14]++;
            } else if(localIntensity >= 160 && localIntensity < 170){
                temp[15]++;
            } else if(localIntensity >= 170 && localIntensity < 180){
                temp[16]++;
            } else if(localIntensity >= 180 && localIntensity < 190){
                temp[17]++;
            } else if(localIntensity >= 190 && localIntensity < 200){
                temp[18]++;
            } else if(localIntensity >= 200 && localIntensity < 210){
                temp[19]++;
            } else if(localIntensity >= 210 && localIntensity < 220){
                temp[20]++;
            } else if(localIntensity >= 220 && localIntensity < 230){
                temp[21]++;
            } else if(localIntensity >= 230 && localIntensity < 240){
                temp[22]++;
            } else if(localIntensity >= 240 && localIntensity <= 255){
                temp[23]++;
            }
        }
        return temp;
    }

    // This function gets the intensity color code
    // Returns the color codes for one image
    public double[] getColorCode(BufferedImage image, int height, int width){
        int[] dataBuffInt = image.getRGB(0, 0, width, height, null, 0, width);
        double[] temp = new double[64]; // temporary array to hold values
        // filling the color code for the given image
        for(int i = 0; i < dataBuffInt.length; i++) {
            Color c = new Color(dataBuffInt[i]);
            // converting colors to the binary numbers
            String red = String.format("%8s", Integer.toBinaryString(c.getRed())).replace(' ', '0');
            String green = String.format("%8s", Integer.toBinaryString(c.getGreen())).replace(' ', '0');
            String blue = String.format("%8s", Integer.toBinaryString(c.getBlue())).replace(' ', '0');
            // getting first two numbers then concatinating and adding to the array
            temp[Integer.parseInt(red.substring(0, 2) + green.substring(0, 2) + blue.substring(0, 2), 2)]++;
        }
        return temp;

    }

    //This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
    public void writeColorCode(){
        Writer writer = null;
        // writing the color codes to the text file
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("colorCodes.txt"), "utf-8")); // create file
            for (int row = 0; row < colorCodeMatrix.length; row++) { // write color codes
                for (int col = 0; col < colorCodeMatrix[row].length; col++) {
                    writer.write((int)colorCodeMatrix[row][col] + ", ");
                }
                writer.write("\n");
            }
        // if write error then throw the exception
        } catch (IOException ex) {
            System.out.println("Writer error");
        } finally { // close file
            try {writer.close();} catch (Exception ex) {}
        }
    }

    //This method writes the contents of the intensity matrix to a file called intensity.txt
    public void writeIntensity(){
        Writer writer = null;
        // writing the intensities to the text file
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("intensity.txt"), "utf-8")); // create file
            for (int row = 1; row < intensityMatrix.length; row++) { // write intensities
                for (int col = 1; col < intensityMatrix[row].length; col++) {
                    writer.write((int)intensityMatrix[row][col] + ", ");
                }
                writer.write("\n");
            }
        // if write error then throw the exception
        } catch (IOException ex) {
            System.out.println("Writer error");
        } finally { // close the file
            try {writer.close();} catch (Exception ex) {}
        }
    }
}
