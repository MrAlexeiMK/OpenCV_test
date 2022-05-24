package mralexeimk.opencv.utils;

import mralexeimk.opencv.constants.ImplementationType;
import mralexeimk.opencv.interfaces.FrameProcess;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
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
            case CORRESPONDENCES_ORB -> {
                return frame -> {
                    File folder = new File("src/resources");
                    File[] listFiles = folder.listFiles();
                    if(listFiles != null) {
                        DescriptorMatcher matcher = DescriptorMatcher.create(
                                DescriptorMatcher.BRUTEFORCE_HAMMING);
                        Mat object = new Mat();
                        Imgproc.cvtColor(frame, object, Imgproc.COLOR_RGB2GRAY);
                        Imgproc.medianBlur(object, object, 7);

                        MatOfKeyPoint kp = new MatOfKeyPoint();
                        Mat descriptor = new Mat();

                        ORB orb = ORB.create();
                        orb.detectAndCompute(object, new Mat(), kp, descriptor);
                        Features2d.drawKeypoints(frame, kp, frame);

                        List<KeyPoint> objectPoints = kp.toList();
                        for (File file : listFiles) {
                            String[] spl = file.getName().split("\\.");
                            String ext = spl[spl.length-1];
                            if(!ext.equalsIgnoreCase("png")) continue;
                            Mat mask = Imgcodecs.imread(file.getAbsolutePath(), Imgproc.COLOR_RGB2GRAY);
                            MatOfKeyPoint kp_ = new MatOfKeyPoint();
                            Mat descriptor_ = new Mat();

                            orb.detectAndCompute(mask, new Mat(), kp_, descriptor_);

                            MatOfDMatch matches = new MatOfDMatch();
                            matcher.match(descriptor, descriptor_, matches);

                            List<DMatch> dms = matches.toList();
                            List<DMatch> betterMatches = new LinkedList<>();
                            for(DMatch dm : dms) {
                                if(dm.distance <= 30) {
                                    betterMatches.add(dm);
                                }
                            }

                            /*
                            if(listPoints.size() > 5) {
                                MatOfPoint2f points = new MatOfPoint2f();
                                points.fromList(listPoints);
                                Rect r = Imgproc.boundingRect(points);
                                Imgproc.rectangle(frame, r, CvUtils.WHITE, 2);
                                Imgproc.putText(frame, spl[0],
                                        new Point(r.x, r.y - 10), 0,
                                        0.7, CvUtils.BLACK);
                            }
                            */

                            MatOfDMatch matPoints = new MatOfDMatch();
                            matPoints.fromList(betterMatches);
                            Features2d.drawMatches(object, kp, mask, kp_, matPoints, frame);

                        }
                    }
                };
            }
            case OBJECT_DETECTION_YOLO -> {
                return frame -> {
                    MatOfInt classIds = new MatOfInt();
                    MatOfFloat scores = new MatOfFloat();
                    MatOfRect boxes = new MatOfRect();
                    YOLO.model.detect(frame, classIds, scores, boxes, 0.6f, 0.4f);

                    for (int i = 0; i < classIds.rows(); ++i) {
                        Rect box = new Rect(boxes.get(i, 0));
                        Imgproc.rectangle(frame, box, new Scalar(0, 255, 0), 2);

                        int classId = (int) classIds.get(i, 0)[0];
                        double score = scores.get(i, 0)[0];
                        String text = String.format("%s: %.2f", YOLO.classes.get(classId), score);
                        Imgproc.putText(frame, text, new Point(box.x, box.y - 5),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
                    }
                };
            }
        }
        return frame -> {};
    }
}
