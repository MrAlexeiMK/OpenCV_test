package mralexeimk.opencv;

import mralexeimk.opencv.constants.ImplementationType;
import mralexeimk.opencv.guis.Camera;
import mralexeimk.opencv.utils.FrameProcessListener;

public class Main {
    public static void main(String[] args) {
        Camera camera = new Camera(FrameProcessListener.get(ImplementationType.KEY_POINTS));
        camera.start();
    }
}
