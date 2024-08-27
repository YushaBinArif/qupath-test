package qupath.lib.gui.ABMIL;

import qupath.lib.gui.QuPathGUI;
import qupath.lib.objects.PathObject;
import qupath.lib.regions.ImageRegion;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import java.awt.image.BufferedImage;

import static qupath.lib.scripting.QP.getCurrentHierarchy;

/**
 * A QuPath extension that displays a histogram of p-values for the selected regions.
 *
 * Usage: First Run ABMIL, after that choose any selection tool and select the desired regions and then run this extension to display the histogram
 *
 * @author Yousha Arif
 */

public class DisplayHistogramCommand implements Runnable{


    private static String _resourceDirectoryPath = "qupath-extension-ABMIL/src/main/resources/";
    private static String _filePvalue = "pValue.txt";
    private static String _fileHistogramScript = "hist.py";
    private static String _fileHistogramImage = "histImage.png";
    private static String _pythonExecutableCommand = "python ";

    private static String command = _pythonExecutableCommand + _resourceDirectoryPath + _fileHistogramScript + " " + _resourceDirectoryPath + _filePvalue  + " " + _resourceDirectoryPath + _fileHistogramImage;
    //Example "python qupath-extension-ABMIL/src/main/resources/hist.py qupath-extension-ABMIL/src/main/resources/pValue.txt qupath-extension-ABMIL/src/main/resources/histImage.png"

    private QuPathGUI qupathGUI;

    DisplayHistogramCommand(QuPathGUI qupath) {
        qupathGUI = qupath;
    }


    @Override
    public void run() {

        var H = getCurrentHierarchy();
        var SO = H.getSelectionModel().getSelectedObject();
        var ROI = SO.getROI();
        var IR = ImageRegion.createInstance(ROI);

        //Get all the ROIs inside the selected region
        var A = H.getAnnotationObjects().stream().filter( o -> IR.intersects(o.getROI().getBoundsX(), o.getROI().getBoundsY(), o.getROI().getBoundsWidth(), o.getROI().getBoundsHeight()) == true && o.getID() != SO.getID());

        // Write P-values to a text file
        //TODO: check if the list is empty
        try (Writer W = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(_resourceDirectoryPath + _filePvalue ), "utf-8"))) {

            Collection<PathObject> POs = A.
                    filter(l-> l != null).
                    collect(Collectors.toList());

            for (PathObject PO: POs) {
                W.write(PO.getPValue().toString()+System.lineSeparator());

            }
            System.out.println("Writing P-values : Completed");
        }
        catch (Exception E){
            System.out.println(E.getMessage());
        }



        // Send P-values to a python script to visualize p-values as a histogram

        try {
            System.out.println("Executing :" + command);



            Process P = Runtime.getRuntime().exec(command);

            BufferedReader R = new BufferedReader(new InputStreamReader(P.getErrorStream()));
            String OUT = R.readLine();
            System.out.println("List of errors from python script, null means no errors:");
            System.out.println(OUT);

            //Visualize the histogram

            File F = Paths.get(".", _resourceDirectoryPath, "histImage.png").normalize().toFile();
            if(F.exists()){
                BufferedImage IMG = ImageIO.read(F);
                ImageIcon IC = new ImageIcon(IMG);
                JFrame JF =new JFrame();
                JF.setLayout(new FlowLayout());
                JF.setSize(800,600);
                JLabel JL =new JLabel();
                JL.setIcon(IC);
                JF.add(JL);
                JF.setVisible(true);

            }
            else {
                System.out.println("Histogram File Not Found");
                System.out.println(F.getAbsolutePath().toString());
            }


        } catch (IOException e) {
           throw new RuntimeException(e);
        }



    }
}


// Code for future use

//File F = new File( "/Users/arifyousha/Documents/Takeuchi Lab/CV/QuPath Development/QuPath/qupath-extension-ABMIL/src/main/resources/"+ "histImage.png");
//File F = new File( this.getClass().getResource("histImage.png").getPath());
//String N = String.valueOf(Math.round((float) Math.round(PO.getPValue() * 100) / 100));
//PO.setName(N);