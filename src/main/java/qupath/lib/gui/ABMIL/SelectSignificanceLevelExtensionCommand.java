package qupath.lib.gui.ABMIL;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.objects.PathObject;
import qupath.lib.objects.PathObjects;
import qupath.lib.roi.ROIs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SelectSignificanceLevelExtensionCommand implements Runnable{

    private static String WSI_FILE_NAME;

    private QuPathGUI qupathGUI;

    static String filepath = null;

    String CSVPath = null;

    static Float significance_level;

    SelectSignificanceLevelExtensionCommand(QuPathGUI qupath) {
        qupathGUI = qupath;
    }

    public void setWsiFileName() {
        var imagePath = qupathGUI.getImageData().getServerPath();
        var splittedImagePathList = imagePath.split(":");
        var splittedImagePath =  splittedImagePathList[splittedImagePathList.length-1];
        WSI_FILE_NAME = (splittedImagePath.split("\\["))[0];
    }
    @Override
    public void run() {

        JFramegetname frame = new JFramegetname("Please a significance level (float).");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

    }

    public static void SetColorViaCSV() {
        Path path = Paths.get(filepath);
        try {

            qupath.lib.scripting.QP.clearAllObjects();

            // CSVファイルの読み込み
            List<String> lines = Files.readAllLines(path, Charset.forName("Shift-JIS"));

            int MAX_DATA_NUMS = lines.size();

            System.out.println(MAX_DATA_NUMS);

            var plane = qupath.lib.gui.scripting.QPEx.getCurrentViewer().getImagePlane();

            int count = 0;
            for (int i = 0; i < (MAX_DATA_NUMS); i++) {
                String[] data = lines.get(i).split(",");
                float a = Float.parseFloat(data[2]);
                if (a >= significance_level){
                    count += 1;
                }
            }

            var tiles = new PathObject[count];

            count = 0;
            for (int i = 0; i < (MAX_DATA_NUMS); i++) {

                String[] data = lines.get(i).split(",");

                int tileSize = 224;

                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                float a = Float.parseFloat(data[2]);

                System.out.println(a);
                System.out.println(significance_level);

                if (a >= significance_level){

                    System.out.println(x);
                    System.out.println(y);

                    var roi = ROIs.createRectangleROI(x, y, tileSize, tileSize, plane);
                    PathObject A = PathObjects.createAnnotationObject(roi);

                    A.setColor(255,0,0);
                    tiles[count] = A;

                    count += 1;

                }

            }

            qupath.lib.scripting.QP.addObjects(tiles);

//            OverlayOptions.showAnnotations.set(false);

        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }

    }

    class JFramegetname extends JFrame implements ActionListener{
        JLabel label;
        JTextField text;

        JFramegetname(String title){
            setTitle(title);
            setBounds(200, 200, 600, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            text = new JTextField(20);
            JButton button = new JButton("OK");
            button.addActionListener(this);

            JPanel p = new JPanel();
            p.add(text);
            p.add(button);

            label = new JLabel();

            Container contentPane = getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
            contentPane.add(label, BorderLayout.SOUTH);
        }

        public void actionPerformed(ActionEvent e){
            significance_level = Float.parseFloat(text.getText());

            dispose();

            setWsiFileName();
            if (WSI_FILE_NAME.isEmpty()) {
                return;
            }

            var WSI_NAME = WSI_FILE_NAME.substring(WSI_FILE_NAME.length()-11,WSI_FILE_NAME.length()-4).toString();

            CSVPath = "../test_qupath/" + WSI_NAME + "_att.csv";

            filepath = CSVPath;
            SetColorViaCSV();

        }


    }

}
