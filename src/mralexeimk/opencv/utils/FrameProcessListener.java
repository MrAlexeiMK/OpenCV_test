package mralexeimk.opencv.utils;

import mralexeimk.opencv.constants.ImplementationType;
import mralexeimk.opencv.interfaces.FrameProcess;
import org.opencv.core.*;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FrameProcessListener {
    public static FrameProcess get(ImplementationType implementationType) {
        switch (implementationType) {
            case CONTOURS -> {
                return frame -> {
                    List<MatOfPoint> points = new ArrayList<>();

                    Mat gray = new Mat();
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
                    Imgproc.medianBlur(gray, gray, 5);
                    Imgproc.Canny(gray, gray, 80, 200);
                    Imgproc.findContours(gray, points, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                    Imgproc.drawContours(frame, points, -1, new Scalar(255, 255, 255));

                    for(MatOfPoint point : points) {
                        Rect r = Imgproc.boundingRect(point);
                        Imgproc.rectangle(frame, new Point(r.x, r.y),
                                new Point(r.x + r.width - 1, r.y + r.height - 1),
                                CvUtils.WHITE);
                    }
                };
            }
            case KEY_POINTS -> {
                return frame -> {
                    Mat gray = new Mat();
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
                    Imgproc.medianBlur(gray, gray, 5);
                    
                    MatOfKeyPoint kp = new MatOfKeyPoint();
                    SIFT sift = SIFT.create();
                    sift.detect(gray, kp);
                    Features2d.drawKeypoints(frame, kp, frame, CvUtils.WHITE);
                };
            }
        }
        return frame -> {};
    }
}
