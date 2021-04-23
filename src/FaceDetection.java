import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.FileWriter;
import java.io.IOException;

public class FaceDetection {
    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        return imageCodecs.imread(imagePath);
    }
    public static void main(String[] args) {
        OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            FileWriter writer;

            writer = new FileWriter(VideoSummarizerAnalysisParams.NUM_DETECTED_FACES_OUTPUT_FILENAME);

            for (int i = 7214; i < VideoConfig.NUM_FRAMES; i++) {
                Mat loadedImage = loadImage(args[0] + "frame" + i + ".jpg");

                MatOfRect facesDetected = new MatOfRect();

                CascadeClassifier cascadeClassifier = new CascadeClassifier();

                int minFaceSize = Math.round(loadedImage.rows() * 0.1f);

                cascadeClassifier.load("haarcascade_frontalface_alt.xml");

                cascadeClassifier.detectMultiScale(loadedImage,
                        facesDetected,
                        1.1,
                        3,
                        Objdetect.CASCADE_SCALE_IMAGE,
                        new Size(minFaceSize, minFaceSize),
                        new Size()
                );

                Rect[] facesArray = facesDetected.toArray();

                System.out.println("frame " + i + " num faces:"+ facesArray.length);

                writer.write(facesArray.length + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
