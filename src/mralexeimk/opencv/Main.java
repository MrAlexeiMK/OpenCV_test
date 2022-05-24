package mralexeimk.opencv;

import mralexeimk.opencv.constants.ImplementationType;
import mralexeimk.opencv.guis.Camera;
import mralexeimk.opencv.utils.FrameProcessListener;
import mralexeimk.opencv.utils.YOLO;
import org.opencv.core.Core;

public class Main {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String[] args) {
        YOLO.init("coco_names", "yolov4.cfg", "yolov4.weights", 250, 250);
        Camera camera = new Camera(FrameProcessListener.get(ImplementationType.OBJECT_DETECTION_YOLO));
        camera.start();
    }
}
