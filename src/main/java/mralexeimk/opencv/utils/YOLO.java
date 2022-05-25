package mralexeimk.opencv.utils;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.DetectionModel;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class YOLO {
    public static List<String> classes;
    public static DetectionModel model;

    public static void init(String fileNames, String cfgFile, String weightsFile) {
        init(fileNames, cfgFile, weightsFile, 250, 250);
    }

    public static void init(String fileNames, String cfgFile, String weightsFile, int width, int height) {
        try {
            classes = Files.readAllLines(Paths.get("src/main/resources/"+fileNames));
            Net net = Dnn.readNetFromDarknet("src/main/resources/"+cfgFile, "src/main/resources/"+weightsFile);
            model = new DetectionModel(net);
            model.setInputParams(1/255.0, new Size(width, height), new Scalar(0), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
