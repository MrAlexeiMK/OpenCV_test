package mralexeimk.opencv.guis;

import mralexeimk.opencv.interfaces.FrameProcess;
import mralexeimk.opencv.utils.CvUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class Camera extends Thread {
    private volatile boolean active;
    private final int cameraIndex;
    private int width, height;
    private FrameProcess frameProcess;

    private final JFrame window;
    private final JLabel label;

    @Override
    public void run() {
        VideoCapture camera = new VideoCapture(cameraIndex);
        if (!camera.isOpened()) {
            window.setTitle("Can't connect to camera " + cameraIndex);
            active = false;
            return;
        }
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
        try {
            Mat frame = new Mat();
            BufferedImage img;
            while (active) {
                if (camera.read(frame)) {
                    frameProcess.frameProcess(frame);
                    img = CvUtils.MatToBufferedImage(frame);
                    if (img != null) {
                        ImageIcon imageIcon = new ImageIcon(img);
                        label.setIcon(imageIcon);
                        label.repaint();
                        window.pack();
                    }
                } else {
                    System.out.println("Can't capture a frame");
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            close();
        } finally {
            camera.release();
            active = false;
        }
    }

    public void close() {
        active = false;
        window.dispose();
        System.exit(0);
    }

    public Camera() {
        this(frame -> {});
    }

    public Camera(FrameProcess frameProcess) {
        this(frameProcess, 0);
    }

    public Camera(FrameProcess frameProcess, int cameraIndex) {
        this(frameProcess, cameraIndex, "Camera");
    }

    public Camera(FrameProcess frameProcess, int cameraIndex, String title) {
        this(frameProcess, cameraIndex, title, 640, 480, true);
    }

    public Camera(FrameProcess frameProcess, int cameraIndex, String title, int width, int height, boolean toCenter) {
        this.frameProcess = frameProcess;
        this.cameraIndex = cameraIndex;
        this.width = width;
        this.height = height;
        active = true;

        window = new JFrame(title);
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setResizable(false);
        if(toCenter) window.setLocationRelativeTo(null);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        label = new JLabel();
        window.setContentPane(label);
        window.setVisible(true);
    }

    public boolean isActive() {
        return active;
    }
}

