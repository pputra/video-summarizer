package summarizer.calculations.face;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import summarizer.configs.VideoConfig;
import summarizer.configs.VideoSummarizerAnalysisParams;

import java.io.FileWriter;
import java.io.IOException;

public class FaceDetection {
    public static final String HAARCASCADE_FRONTAL_FACE_PATH = "haarcascades/haarcascade_frontalface_alt.xml";
    public static final String HAARCASCADE_PROFILE_FACE_PATH = "haarcascades/haarcascade_profileface.xml";
    public static final String HAARCASCADE_HEAD_SHOULDER_PATH = "haarcascades/haarcascade_head_shoulder.xml";
    public static final String HAARCASCADE_NOSE_PATH = "haarcascades/haarcascade_nose.xml";
    public static final String HAARCASCADE_MOUTH_PATH = "haarcascades/haarcascade_mouth.xml";

    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }

    public static Rect[] classify(Mat loadedImage, String cascadePath, double scaleFactor,
                                    int minNeighbors, int minWidth, int minHeight) {
        MatOfRect detectedObjects = new MatOfRect();

        CascadeClassifier frontalFaceClassifier = new CascadeClassifier();

        frontalFaceClassifier.load(cascadePath);

        frontalFaceClassifier.detectMultiScale(loadedImage,
                detectedObjects,
                scaleFactor,
                minNeighbors,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minWidth, minHeight),
                new Size()
        );

        return detectedObjects.toArray();
    }


    public static void main(String[] args) {
        OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String pathToFrames = args[0];

        try {
            FileWriter writer;

            writer = new FileWriter(VideoSummarizerAnalysisParams.NUM_DETECTED_FACES_OUTPUT_FILENAME);

            for (int i = 0; i < VideoConfig.NUM_FRAMES; i++) {
                Mat loadedImage = loadImage(pathToFrames + "frame" + i + ".jpg");

                int minSize = Math.round(loadedImage.rows() * 0.1f);

                Rect[] frontalFaces = classify(loadedImage, HAARCASCADE_FRONTAL_FACE_PATH,
                        1.1, 3, minSize, minSize);

                Rect[] profileFaces = classify(loadedImage, HAARCASCADE_PROFILE_FACE_PATH,
                        1.1, 3, minSize, minSize);

                Rect[] headShoulders = classify(loadedImage, HAARCASCADE_HEAD_SHOULDER_PATH,
                        1.1, 3, minSize, minSize);

                Rect[] noses = classify(loadedImage, HAARCASCADE_NOSE_PATH,
                        1.1, 3, minSize, minSize);

                Rect[] mouths = classify(loadedImage, HAARCASCADE_MOUTH_PATH,
                        1.1, 3, minSize, minSize);

                System.out.println("frame " + i + " frontal:" + frontalFaces.length + " profile:" +
                        profileFaces.length + " head shoulder:" + headShoulders.length +
                        " nose:" + noses.length + " mouth:" + mouths.length);

                String resultStr = frontalFaces.length +
                        " " +
                        profileFaces.length +
                        " " +
                        headShoulders.length +
                        " " +
                        noses.length +
                        " " +
                        mouths.length +
                        "\n";

                writer.write(resultStr);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
