package qupath.lib.gui.ABMIL;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.objects.PathObject;
import qupath.lib.objects.PathObjects;
import qupath.lib.roi.ROIs;
import qupath.lib.scripting.QP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ABMILExtensionCommand implements Runnable {

    private static final String SHELL_FILE_NAME = "qupath-extension-ABMIL/src/main/resources/test.sh";
    private static String WSI_FILE_NAME;
    private QuPathGUI qupathGUI;

    ABMILExtensionCommand(QuPathGUI qupath) {
        File shellFile = new File(SHELL_FILE_NAME);
        if (!shellFile.setExecutable(true)) {
            System.out.println("Can not set executable for shell file.");
        }
        qupathGUI = qupath;
    }

    public String getWSIFileName() {
        return  WSI_FILE_NAME;
    }

    static String filepath = null ;
    static String pval_filepath = null;

    public void setWsiFileName() {
        var imagePath = qupathGUI.getImageData().getServerPath();
        var splittedImagePathList = imagePath.split(":");
        var splittedImagePath =  splittedImagePathList[splittedImagePathList.length-1];
        WSI_FILE_NAME = (splittedImagePath.split("\\["))[0];
    }

    @Override
    public void run() {

        setWsiFileName();
        if (WSI_FILE_NAME.isEmpty()) {
            return;
        }
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("sh", SHELL_FILE_NAME, WSI_FILE_NAME);

        var WSI_NAME = WSI_FILE_NAME.substring(WSI_FILE_NAME.length()-11,WSI_FILE_NAME.length()-4).toString();


        // 画像のパスを指定

        var CSVPath = "../test_qupath/" + WSI_NAME + "_color.csv";
        var CSVPath_att = "../test_qupath/" + WSI_NAME + ".csv";

        try {
            // Run script
            Process process = processBuilder.start();

            // Read output
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            //System.out.println(output.toString());

            filepath = CSVPath;
            pval_filepath = CSVPath_att;
            SetColorViaCSV();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void SetColorViaCSV() {
        Path path = Paths.get(filepath);
        Path path_Pvalue = Paths.get(pval_filepath);
        try {
            // CSVファイルの読み込み
            List<String> lines = Files.readAllLines(path, Charset.forName("Shift-JIS"));
            List<String> pValuesList = Files.readAllLines(path_Pvalue, Charset.forName("Shift-JIS"));

            int MAX_DATA_NUMS = lines.size();

            //System.out.println(MAX_DATA_NUMS);

            var imageData = qupath.lib.scripting.QP.getCurrentImageData();
            var plane = qupath.lib.gui.scripting.QPEx.getCurrentViewer().getImagePlane();


            var tiles = new PathObject[MAX_DATA_NUMS];

            for (int i = 0; i < (MAX_DATA_NUMS); i++) {

                String[] data = lines.get(i).split(",");
                String[] P = pValuesList.get(i).split(",");

                int tileSize = 224;

                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                int r = Integer.parseInt(data[2]);
                int g = Integer.parseInt(data[3]);
                int b = Integer.parseInt(data[4]);
                Float pValue = Math.abs(Float.parseFloat(P[2]));


                var roi = ROIs.createRectangleROI(x, y, tileSize, tileSize, plane);
                PathObject A = PathObjects.createAnnotationObject(roi);

                A.setColor(r,g,b);
                A.setPValue(pValue);

                tiles[i] = A;

            }

            qupath.lib.scripting.QP.addObjects(tiles);


        } catch (IOException e) {
            System.out.println("ファイル読み込みに失敗");
        }

    }

}