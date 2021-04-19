import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VideoSummarizer {
    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private AudioInputStream originalAudioStream;
    private final List<BufferedImage> summarizedFrames = new ArrayList<>();
    private AudioInputStream summarizedAudioInputStream;
    private final List<Integer> sceneBoundaries = new ArrayList<>();

    // TODO: READ RGB FRAMES INSTEAD THEN PERFORM ANALYSIS
    // TODO: ANALYZE AUDIO FREQUENCY
    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) {
        getSceneBoundaries(pathToFrameRgb);

        // 2700 for testing purpose.
        for (int i = 0; i < 2700; i++) {
            try {
                originalFrames.add(ImageIO.read(new File(pathToFrame + "frame" + i + ".jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream waveStream = null;

        try {
            waveStream = new FileInputStream(pathToAudio);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            InputStream bufferedIn = new BufferedInputStream(waveStream);
            originalAudioStream = AudioSystem.getAudioInputStream(bufferedIn);

        } catch (UnsupportedAudioFileException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public void getSceneBoundaries(String path) {
        System.out.println("calculating scene boundaries: ");

        for (int i = 0; i < VideoConfig.NUM_FRAMES - 1; i++) {
            try {
                final int TOTAL_NUM_PIXEL = VideoConfig.FRAMES_HEIGHT * VideoConfig.FRAMES_WIDTH;
                int frameLength = TOTAL_NUM_PIXEL*3;

                File file = new File(path + "frame" + i + ".rgb");
                File nextFile = new File(path + "frame" + (i + 1) + ".rgb");

                RandomAccessFile raf = new RandomAccessFile(file, "r");
                RandomAccessFile nextRaf = new RandomAccessFile(nextFile, "r");

                raf.seek(0);
                nextRaf.seek(0);

                byte[] bytes = new byte[(int) (long) frameLength];
                byte[] nextBytes = new byte[(int) (long) frameLength];

                raf.read(bytes);
                nextRaf.read(nextBytes);

                int ind = 0;

                int sumDiffR = 0;
                int sumDiffG = 0;
                int sumDiffB = 0;

                for(int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
                    for(int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                        byte currR = bytes[ind];
                        byte currG = bytes[ind+TOTAL_NUM_PIXEL];
                        byte currB = bytes[ind+TOTAL_NUM_PIXEL*2];

                        byte nextR = nextBytes[ind];
                        byte nextG = nextBytes[ind+TOTAL_NUM_PIXEL];
                        byte nextB = nextBytes[ind+TOTAL_NUM_PIXEL*2];

                        sumDiffR += Math.abs(currR - nextR);
                        sumDiffG += Math.abs(currG - nextG);
                        sumDiffB += Math.abs(currB - nextB);

                        ind++;
                    }
                }

                int avgDiff = (sumDiffR + sumDiffG + sumDiffB) / 3;

                if (avgDiff >= VideoSummarizerAnalysisParams.SCENE_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD) {
                    System.out.println("frame boundary: " + i + " sum rgb diff avg: " + avgDiff);
                    sceneBoundaries.add(i);
                }

                raf.close();
                nextRaf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<BufferedImage> getOriginalFrames() {
        return originalFrames;
    }

    public AudioInputStream getOriginalAudioStream() {
        return originalAudioStream;
    }

    public static void main(String[] args) {
        VideoSummarizer videoSummarizer = new VideoSummarizer(args[0], args[1], args[2]);
        new PlayVideo(videoSummarizer.getOriginalFrames(),
                videoSummarizer.getOriginalAudioStream());
    }
}
