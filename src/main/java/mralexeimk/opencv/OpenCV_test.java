package mralexeimk.opencv;

import mralexeimk.opencv.constants.ImplementationType;
import mralexeimk.opencv.guis.Camera;
import mralexeimk.opencv.utils.FrameProcessListener;
import mralexeimk.opencv.utils.YOLO;
import org.opencv.core.Core;

import java.io.File;

public class OpenCV_test {
    static {
        File dll = new File("src/main/resources/" + Core.NATIVE_LIBRARY_NAME + ".dll");
        System.load(dll.getAbsolutePath());
    }
    public static void main(String[] args) {
        YOLO.init("coco/coco_names", "coco/yolov4.cfg", "coco/yolov4.weights", 250, 250);
        Camera camera = new Camera(FrameProcessListener.get(ImplementationType.OBJECT_DETECTION_YOLO));
        camera.start();
    }
}
