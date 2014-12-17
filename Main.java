// Konstantin Stekhov
// Assignment
// Main class
// This class is a driver class that runs the program.
import javax.swing.*;

public class Main {
    public static void main(String args[]) {
        new ReadImage(); // read images and create text files
        //create GUI and order images
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CBIR app = new CBIR();
                app.setVisible(true);
           }
        });
    }
}
