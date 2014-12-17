// Konstantin Stekhov
// Assignment 1
// CBIR class
// This class creates the GUI and then read the intensity and color code files. After that
// it finds out the distances between images and arranges the images based on the
// query image.

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Image;

public class CBIR extends JFrame implements ItemListener {

    private int numberOfFeatures = 89;
    private int numberOfImages = 100;

    private JLabel photographLabel = new JLabel();  //container to hold a large 
    private JButton[] button; //creates an array of JButtons
    private JCheckBox[] checkBox; // creates an array of checkboxes
    private int[] buttonOrder = new int[numberOfImages + 1]; //creates an array to keep up with the image order
    private double[] imageSize = new double[numberOfImages + 1]; //keeps up with the image sizes
    private GridLayout gridLayout1;
    private GridLayout gridLayout2;
    private GridLayout gridLayout3;
    private GridLayout gridLayout4;
    private GridLayout gridLayout5;
    private JPanel panelBottom1;
    private JPanel panelBottom2;
    private JPanel panelTop;
    private JPanel buttonPanel;
    private JPanel nestedButtonPanel;

    private Double[][] intensityMatrix = new Double[101][26];
    private Double[][] colorCodeMatrix = new Double[100][64];
    private Double[][] combinedMatrix = new Double[numberOfImages][numberOfFeatures];
    private SortedMap<Double, Integer> map;
    private List<Integer> selectedImages = new ArrayList<Integer>();
    int picNo = 0;
    int imageCount = 1; //keeps up with the number of images displayed since the first page.
    boolean optionsTurned = false;

    public CBIR() {
        //The following lines set up the interface including the layout of the buttons and JPanels.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Icon Demo: Please Select an Image");
        panelBottom1 = new JPanel();
        panelBottom2 = new JPanel();
        panelTop = new JPanel();
        buttonPanel = new JPanel();
        nestedButtonPanel = new JPanel();
        gridLayout1 = new GridLayout(4, 5, 5, 5);
        gridLayout2 = new GridLayout(2, 1);
        gridLayout3 = new GridLayout(1, 2, 5, 5);
        gridLayout4 = new GridLayout(6, 2);
        gridLayout5 = new GridLayout(1, 2);
        setLayout(gridLayout2);
        panelBottom1.setLayout(gridLayout1);
        panelBottom2.setLayout(gridLayout1);
        panelTop.setLayout(gridLayout3);
        add(panelTop);
        add(panelBottom1);
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(gridLayout4);
        nestedButtonPanel.setLayout(gridLayout5);
        panelTop.add(photographLabel);
        panelTop.add(buttonPanel);

        JButton previousPage = new JButton("Previous Page");
        JButton nextPage = new JButton("Next Page");
        JCheckBox relevanceOption = new JCheckBox("Relevance");
        JButton relevantSubmit = new JButton("Submit Relevant");
        JButton intensity = new JButton("Intensity");
        JButton colorCode = new JButton("Color Code");
        JButton intensityColor = new JButton("Intensity/Color Code");
        buttonPanel.add(intensity);
        buttonPanel.add(colorCode);
        buttonPanel.add(intensityColor);
        buttonPanel.add(relevanceOption);
        buttonPanel.add(relevantSubmit);
        buttonPanel.add(nestedButtonPanel);

        nestedButtonPanel.add(previousPage);
        nestedButtonPanel.add(nextPage);

        nextPage.addActionListener(new NextPageHandler());
        previousPage.addActionListener(new PreviousPageHandler());
        intensity.addActionListener(new IntensityHandler());
        colorCode.addActionListener(new ColorCodeHandler());
        relevanceOption.addActionListener(new RelevanceHandler());
        intensityColor.addActionListener(new IntensityColorHandler());
        relevantSubmit.addActionListener(new RelevantSubmitHandler());

        setSize(1200, 750);
        // this centers the frame on the screen
        setLocationRelativeTo(null);


        button = new JButton[101];
        checkBox = new JCheckBox[101];

        /* This for loop goes through the images in the database and stores them as icons and adds
         * the images to JButtons and then to the JButton array
        */
        for (int i = 1; i < (numberOfImages + 1); i++) {
            ImageIcon icon;
            icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
            if (icon != null) { // creating the image buttons for GUI
                // resizing images so they fit into buttons
                Image tempImg = icon.getImage();
                Image resizedImg = tempImg.getScaledInstance(240, 80, Image.SCALE_SMOOTH);
                button[i] = new JButton(new ImageIcon(resizedImg));
                checkBox[i] = new JCheckBox("Relevant");
                checkBox[i].addItemListener(this);
                panelBottom1.add(button[i]);
                button[i].addActionListener(new IconButtonHandler(i, icon));
                buttonOrder[i] = i;
                imageSize[i] = icon.getIconHeight() * icon.getIconWidth();
            }
        }
        // initializing the intensity matrix
        for (int row = 0; row < intensityMatrix.length; row++) {
            for (int col = 0; col < intensityMatrix[row].length; col++) {
                intensityMatrix[row][col] = 0.0;
            }
        }
        readIntensityFile(); // reading text files
        readColorCodeFile();
        makeCombinedMatrix();
        displayFirstPage();
    }

    public void itemStateChanged(ItemEvent e) {
        for (int i = 0; i < checkBox.length; i++) {
            if (e.getItem() == checkBox[i]) {
                if(checkBox[i].isSelected()){
                    selectedImages.add(i);
                } else {
                    selectedImages.remove(new Integer(i));
                }
            }
        }

    }

    /* This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
    */
    public void readIntensityFile() {
        BufferedReader br = null;
        String line = "";
        int lineNumber = 1;
        try {
            br = new BufferedReader(new FileReader("intensity.txt"));

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                for (int j = 0; j < parts.length; j++) {
                    Double i = Double.valueOf(parts[j]);
                    intensityMatrix[lineNumber][j + 1] = i;
                }
                lineNumber++;
            }
        } catch (IOException ex) {
            System.out.println("The file intensity.txt does not exist");
        } finally {
            try {
                br.close();
            } catch (Exception ex) {
            }
        }
    }

    /* This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
    */
    private void readColorCodeFile() {
        BufferedReader br = null;
        int lineNumber = 0;
        try {
            br = new BufferedReader(new FileReader("colorCodes.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                for (int j = 0; j < parts.length; j++) {
                    Double i = Double.valueOf(parts[j]);
                    colorCodeMatrix[lineNumber][j] = i;
                }
                lineNumber++;
            }
        } catch (IOException ex) {
            System.out.println("The file intensity.txt does not exist");
        } finally {
            try {
                br.close();
            } catch (Exception ex) {
            }
        }
    }

    // This function creates the combined matrix with both color codes and intesity features
    private void makeCombinedMatrix() {
        int tempCol = 0;
        Double[] featureAverage = new Double[numberOfFeatures];
        // copying matrices into the combined matrix
        for (int row = 0; row < combinedMatrix.length; row++) {
            Double sum = 0.0;
            for (int col = 1; col < intensityMatrix[row].length; col++) {
                combinedMatrix[row][tempCol] = intensityMatrix[row + 1][col] / imageSize[row + 1];
                tempCol++;
            }
            for (int col = 0; col < colorCodeMatrix[row].length; col++) {
                combinedMatrix[row][tempCol] = colorCodeMatrix[row][col] / imageSize[row + 1];
                tempCol++;
            }
            tempCol = 0;
        }

        // Finding mean values for features
        Double[] tempArr = new Double[numberOfFeatures];
        for (int i = 0; i < featureAverage.length; i++) {
            featureAverage[i] = 0.0;
        }
        for (int row = 0; row < combinedMatrix.length; row++) {
            for (int col = 0; col < combinedMatrix[row].length; col++) {
                tempArr[col] = combinedMatrix[row][col];
            }
            for (int i = 0; i < featureAverage.length; i++) {
                featureAverage[i] += tempArr[i];
            }
        }
        for (int i = 0; i < featureAverage.length; i++) {
            featureAverage[i] = featureAverage[i] / numberOfImages;
        }

        // finding standard deviations for features
        Double[] tempStdSum = new Double[numberOfFeatures];
        Double[] stdDev = new Double[numberOfFeatures];
        for (int i = 0; i < stdDev.length; i++) {
            stdDev[i] = 0.0;
        }
        for (int row = 0; row < combinedMatrix.length; row++) {
            for (int col = 0; col < combinedMatrix[row].length; col++) {
                tempStdSum[col] = Math.pow(combinedMatrix[row][col] - featureAverage[col], 2);
            }
            for (int i = 0; i < stdDev.length; i++) {
                stdDev[i] += tempStdSum[i];
            }
        }
        for (int i = 0; i < stdDev.length; i++){
            stdDev[i] = Math.sqrt(stdDev[i]/(numberOfImages - 1));
        }

        // creating the normalized features matrix
        for (int row = 0; row < combinedMatrix.length; row++) {
            for (int col = 0; col < combinedMatrix[row].length; col++) {
                if(featureAverage[col] == 0.0 || stdDev[col] == 0.0){
                    combinedMatrix[row][col] = 0.0;
                } else {
                    combinedMatrix[row][col] = (combinedMatrix[row][col] - featureAverage[col]) / stdDev[col];
                }
            }
        }
    }

    /*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
     * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
     * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
    */
    private void displayFirstPage() {
        int imageButNo = 0;
        panelBottom1.removeAll();
        if (buttonOrder.length > 21){
            // if checkboxes are clicked then create the box layout with picture and checkbox for
            // each image
            if (optionsTurned) {
                for (int i = 1; i < 21; i++) {
                    imageButNo = buttonOrder[i];
                    JPanel listPane = new JPanel();
                    listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
                    button[imageButNo].setPreferredSize(new Dimension(30, 10));
                    listPane.add(button[imageButNo]);
                    listPane.add(checkBox[imageButNo]);
                    panelBottom1.add(listPane);
                    panelBottom1.add(listPane);
                    imageCount++;
                }
            } else {
                for (int i = 1; i < 21; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    imageCount++;
                }
            }
            // if less than 21 image
        } else {
            if (optionsTurned) {
                // if checkboxes are clicked then create the box layout with picture and checkbox for
                // each image
                for (int i = 1; i < buttonOrder.length; i++) {
                    JPanel listPane = new JPanel();
                    listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
                    button[imageButNo].setPreferredSize(new Dimension(30, 10));
                    listPane.add(button[imageButNo]);
                    listPane.add(checkBox[imageButNo]);
                    panelBottom1.add(listPane);
                    imageCount++;
                }
            } else {
                for (int i = 1; i < buttonOrder.length; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    imageCount++;
                }
            }
        }

        panelBottom1.revalidate();
        panelBottom1.repaint();

    }

    /* This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the
     * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
    */
    private class IconButtonHandler implements ActionListener {
        int pNo = 0;
        ImageIcon iconUsed;

        IconButtonHandler(int i, ImageIcon j) {
            pNo = i;
            iconUsed = j;  //sets the icon to the one used in the button
        }

        public void actionPerformed(ActionEvent e) {
            photographLabel.setIcon(iconUsed);
            picNo = pNo;
        }

    }

    /* This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the
     * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
     * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class NextPageHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int imageButNo = 0;
            int endImage = imageCount + 20;
            // making sure that the images are not out of bounds
            if(imageCount > 81){
                imageCount = 81;
            } else if (imageCount < 1){
                imageCount = 1;
            }
            if (endImage <= 101) {
                panelBottom1.removeAll();
                // if checkboxes are clicked then create the box layout with picture and checkbox for
                // each image
                if(optionsTurned) {
                    for (int i = imageCount; i < endImage; i++) {
                        imageButNo = buttonOrder[i];
                        JPanel listPane = new JPanel();
                        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
                        button[imageButNo].setPreferredSize(new Dimension(30, 10));
                        listPane.add(button[imageButNo]);
                        listPane.add(checkBox[imageButNo]);
                        panelBottom1.add(listPane);
                        imageCount++;
                    }

                } else {
                    for (int i = imageCount; i < endImage; i++) {
                        imageButNo = buttonOrder[i];
                        panelBottom1.add(button[imageButNo]);
                        imageCount++;
                    }
                }
                panelBottom1.revalidate();
                panelBottom1.repaint();
            }
        }

    }

    /* This class implements an ActionListener for the previousPageButton.  The last image number to be displayed is set to the
     * current image count minus 40.  If the endImage number is less than 1, then the previous page button does not display any new 
     * images because the starting image is 1.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class PreviousPageHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int imageButNo = 0;
            int startImage = imageCount - 40;
            int endImage = imageCount - 20;
            // making sure that the images are not out of bounds
            if(imageCount > 81){
                imageCount = 81;
            } else if (imageCount < 1){
                imageCount = 1;
            }
            if (startImage >= 1) {
                panelBottom1.removeAll();

            /* The for loop goes through the buttonOrder array starting with the startImage value
             * and retrieves the image at that place and then adds the button to the panelBottom1.
            */
                // if checkboxes are clicked then create the box layout with picture and checkbox for
                // each image
                if(optionsTurned) {
                    for (int i = startImage; i < endImage; i++) {
                        imageButNo = buttonOrder[i];
                        JPanel listPane = new JPanel();
                        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
                        button[imageButNo].setPreferredSize(new Dimension(30, 10));
                        listPane.add(button[imageButNo]);
                        listPane.add(checkBox[imageButNo]);
                        panelBottom1.add(listPane);
                        imageCount--;
                    }
                } else {
                    for (int i = startImage; i < endImage; i++) {
                        imageButNo = buttonOrder[i];
                        panelBottom1.add(button[imageButNo]);
                        imageCount--;
                    }
                }

                panelBottom1.revalidate();
                panelBottom1.repaint();
            }
        }

    }

    /*
     * This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
     * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */
    private class IntensityHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            double[] distance = new double[101]; // distances for all images
            map = new TreeMap<Double, Integer>(); // map for sorting images
            double tempSum = 0; // temporary sum of the differences for one image bins
            double picSize = imageSize[picNo]; // size of the query image

            for (int row = 1; row < intensityMatrix.length; row++) {
                for (int col = 1; col < intensityMatrix[row].length; col++) {
                    // computing the distance between each bin of the query image and other images
                    double temp = Math.abs(intensityMatrix[picNo][col]/picSize - intensityMatrix[row][col]/imageSize[row]);
                    tempSum += temp;
                }
                distance[row] = tempSum;
                tempSum = 0.0; // resetting the tempSum
            }
            // sorting the images
            for(int i = 1; i < buttonOrder.length; i++){
                map.put(distance[i], i);
            }

            // put the button order values in the arraylist
            ArrayList tempAl = new ArrayList();
            for(double entry : map.keySet()) {
                int value = map.get(entry);
                tempAl.add(value);
            }
            Integer[] arg = (Integer[]) tempAl.toArray(new Integer[tempAl.size()]);
            // copy array list into a button order array
            for(int i = 1; i < arg.length; i++){
                buttonOrder[i] = arg[i-1];
            }
            imageCount = 1;
            displayFirstPage(); // displaying images
        }
    }

    private class ColorCodeHandler implements ActionListener {
        // This function arranges the images based on the color method and the query image
        public void actionPerformed(ActionEvent e) {
            double[] distance = new double[101]; // distances for all images
            map = new TreeMap<Double, Integer>(); // map for sorting images
            double tempSum = 0; // temporary sum of the differences for one image bins
            double picSize = imageSize[picNo]; // size of the query image

            // getting the color codes for all images
            for (int row = 0; row < colorCodeMatrix.length; row++) {
                for (int col = 0; col < colorCodeMatrix[row].length; col++) {
                    // computing the distance between each bin of the query image and other images
                    double temp = Math.abs(colorCodeMatrix[picNo-1][col]/picSize - colorCodeMatrix[row][col]/imageSize[row+1]);
                    tempSum += temp;
                 }
                distance[row+1] = tempSum; // put the sum in the distance array
                tempSum = 0.0;
            }

            // sorting the images according to their distances
            for(int i = 1; i < buttonOrder.length; i++){
                map.put(distance[i], i);
            }

            // put the button order values in the arraylist
            ArrayList tempAl = new ArrayList();
            for(double entry : map.keySet()) {
                int value = map.get(entry);
                tempAl.add(value);
            }
            Integer[] arg = (Integer[]) tempAl.toArray(new Integer[tempAl.size()]);
            // copy array list into a button order array
            for(int i = 1; i < arg.length; i++){
                buttonOrder[i] = arg[i-1];
            }
            imageCount = 1;
            displayFirstPage(); // display
        }
    }

    private class IntensityColorHandler implements ActionListener {
        public void actionPerformed(ActionEvent e){
            double[] distance = new double[numberOfImages + 1]; // distances for all images
            map = new TreeMap<Double, Integer>(); // map for sorting images
            double tempSum = 0; // temporary sum of the differences for one image bins

            // getting the color codes for all images
            for (int row = 0; row < combinedMatrix.length; row++) {
                for (int col = 0; col < combinedMatrix[row].length; col++) {
                    // computing the distance between each bin of the query image and other images
                    double temp = Math.abs(combinedMatrix[picNo-1][col] - combinedMatrix[row][col]);
                    tempSum += temp;
                }
                distance[row+1] = tempSum; // put the sum in the distance array
                tempSum = 0.0;
            }

            // sorting the images according to their distances
            for(int i = 1; i < buttonOrder.length; i++){
                map.put(distance[i], i);
            }

            // put the button order values in the arraylist
            ArrayList tempAl = new ArrayList();
            for(double entry : map.keySet()) {
                int value = map.get(entry);
                tempAl.add(value);
            }
            Integer[] arg = (Integer[]) tempAl.toArray(new Integer[tempAl.size()]);
            // copy array list into a button order array
            for(int i = 0; i < arg.length; i++){
                buttonOrder[i + 1] = arg[i];
            }
            imageCount = 1;
            displayFirstPage(); // display
        }
    }

    // This function adds the checkboxes to the images if
    // the relevance feedback is turned on
    private class RelevanceHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int imageButNo = 0;
            panelBottom1.removeAll();

            if(optionsTurned){
                optionsTurned = false;
                if(buttonOrder.length > 21) {
                    for (int i = 1; i < 21; i++) {
                        imageButNo = buttonOrder[i];
                        panelBottom1.add(button[imageButNo]);
                        imageCount++;
                    }
                } else {
                    for (int i = 1; i < buttonOrder.length; i++) {
                        imageButNo = buttonOrder[i];
                        panelBottom1.add(button[imageButNo]);
                        imageCount++;
                    }
                }
            } else {
                // if checkboxes are clicked then create the box layout with picture and checkbox for
                // each image
                optionsTurned = true;
                if(buttonOrder.length > 21) {
                    for (int i = 1; i < 21; i++) {
                        imageButNo = buttonOrder[i];
                        JPanel listPane = new JPanel();
                        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
                        button[imageButNo].setPreferredSize(new Dimension(30, 10));
                        listPane.add(button[imageButNo]);
                        listPane.add(checkBox[imageButNo]);
                        panelBottom1.add(listPane);
                        imageCount++;
                    }
                    // if less than 21 images
                } else {
                    for (int i = 1; i < buttonOrder.length; i++) {
                        imageButNo = buttonOrder[i];
                        panelBottom1.add(button[imageButNo]);
                        panelBottom1.add(checkBox[imageButNo]);
                        imageCount++;
                    }
                }

            }
            panelBottom1.revalidate();
            panelBottom1.repaint();
        }
    }

    // This function calculates updated weight values and find the distances
    // between the relevant images
    private class RelevantSubmitHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Double[] tempArr = new Double[numberOfFeatures];
            Double[] featureAverage = new Double[numberOfFeatures];
            Double[] tempStdSum = new Double[numberOfFeatures];
            Double[] stdDev = new Double[numberOfFeatures];
            Double[] updatedWeight = new Double[numberOfFeatures];
            Double[] normWeight = new Double[numberOfFeatures];
            Double[][]weights = new Double[numberOfImages][numberOfFeatures];

            // creating the weights matrix
            for (int row = 0; row < weights.length; row++) {
                for (int col = 0; col < weights[row].length; col++) {
                    weights[row][col] = 1.0/numberOfFeatures;
                }
            }
            int counter = 0;
            for (int i = 0; i < featureAverage.length; i++) {
                featureAverage[i] = 0.0;
            }

            // finding mean values
            for (Integer pic : selectedImages) {
                counter++;
                int element = pic - 1;
                for (int col = 0; col < combinedMatrix[element].length; col++) {
                    tempArr[col] = combinedMatrix[element][col];
                }

                for (int i = 0; i < featureAverage.length; i++) {
                    featureAverage[i] += tempArr[i];
                }
            }

            for (int i = 0; i < featureAverage.length; i++) {
                featureAverage[i] = featureAverage[i] / counter;
            }

            // finding standard deviations
            for (int i = 0; i < stdDev.length; i++) {
                stdDev[i] = 0.0;
            }
            for (Integer pic : selectedImages) {
                int element = pic - 1;
                for (int col = 0; col < combinedMatrix[element].length; col++) {
                    tempStdSum[col] = Math.pow(combinedMatrix[element][col] - featureAverage[col], 2);
                }
                for (int i = 0; i < stdDev.length; i++) {
                    stdDev[i] += tempStdSum[i];
                }
            }
            for (int i = 0; i < stdDev.length; i++){
                stdDev[i] = Math.sqrt(stdDev[i]/(counter - 1));
            }

            // finding the minimum non-zero standard deviation
            Double min = Double.MAX_VALUE;
            for (int i = 0; i < stdDev.length; i++) {
                if (stdDev[i] != 0 && stdDev[i] < min) {
                    min = stdDev[i];
                }
            }

            Double sum = 0.0;
            // finding updataed weights
            for(int i = 0; i < updatedWeight.length; i++){
                if(stdDev[i] == 0 && featureAverage[i] == 0){
                    updatedWeight[i] = 0.0;
                } else if (stdDev[i] == 0 && featureAverage[i] != 0) {
                    updatedWeight[i] = 1/(0.5 * min);
                } else {
                    updatedWeight[i] = 1/stdDev[i];
                }
                sum += updatedWeight[i];
            }
            // finding normalized weights
            for(int i = 0; i < normWeight.length; i++){
                normWeight[i] = updatedWeight[i]/sum;
            }
            // putting new weights into weights matrix
            for (Integer pic : selectedImages) {
                int element = pic - 1;
                for (int col = 0; col < weights[element].length; col++) {
                    weights[element][col] = normWeight[col];
                }
            }


            double[] distance = new double[numberOfImages + 1]; // distances for all images
            map = new TreeMap<Double, Integer>(); // map for sorting images
            double tempSum = 0; // temporary sum of the differences for one image bins

            // getting the color codes for all images
            for (int row = 0; row < combinedMatrix.length; row++) {
                for (int col = 0; col < combinedMatrix[row].length; col++) {
                    // computing the distance between each bin of the query image and other images
                    double temp = weights[picNo-1][col] *
                            (Math.abs(combinedMatrix[picNo-1][col] - combinedMatrix[row][col]));
                    tempSum += temp;
                }
                distance[row+1] = tempSum; // put the sum in the distance array
                tempSum = 0.0;
            }

            // sorting the images according to their distances
            for(int i = 1; i < buttonOrder.length; i++){
                map.put(distance[i], i);
            }

            // put the button order values in the arraylist
            ArrayList tempAl = new ArrayList();
            for(double entry : map.keySet()) {
                int value = map.get(entry);
                tempAl.add(value);
            }
            Integer[] arg = (Integer[]) tempAl.toArray(new Integer[tempAl.size()]);
            // copy array list into a button order array
            for(int i = 0; i < arg.length; i++){
                buttonOrder[i + 1] = arg[i];
            }
            imageCount = 1;
            displayFirstPage(); // display
        }
    }
}
