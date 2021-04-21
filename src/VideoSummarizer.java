import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoSummarizer {
    private final String pathToFrame;
    private final String pathToAudio;
    private final String pathToFrameRgb;

    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private AudioInputStream originalAudioStream;
    private final List<BufferedImage> summarizedFrames = new ArrayList<>();
    private AudioInputStream summarizedAudioInputStream;
    private final List<Shot> shots = new ArrayList<>();

    // TODO: READ RGB FRAMES INSTEAD THEN PERFORM ANALYSIS
    // TODO: ANALYZE AUDIO FREQUENCY
    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) {
        this.pathToFrame = pathToFrame;
        this.pathToAudio = pathToAudio;
        this.pathToFrameRgb = pathToFrameRgb;

        System.out.println("calculating shot boundaries...");
        analyzeShots();
        System.out.println("calculating motion scores...");
        calculateMotionScore();
        System.out.println("sorting shots by motion level...");
        Collections.sort(shots);
        shots.forEach((System.out::println));
        System.out.println("generating video summaries...");
        generatedSummarizedVideo();
        // 2700 for testing purpose.
//        for (int i = 0; i < 2700; i++) {
//            try {
//                originalFrames.add(ImageIO.read(new File(pathToFrame + "frame" + i + ".jpg")));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        InputStream waveStream = null;
//
//        try {
//            waveStream = new FileInputStream(pathToAudio);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            InputStream bufferedIn = new BufferedInputStream(waveStream);
//            originalAudioStream = AudioSystem.getAudioInputStream(bufferedIn);
//
//        } catch (UnsupportedAudioFileException | IOException e1) {
//            e1.printStackTrace();
//        }

        // 90 s audio for testing purposes
//        originalAudioStream = SoundUtil.trim(pathToAudio, 0, 90);
    }

    public void analyzeShots() {
        int startFrame = 0;

        for (int i = 0; i < VideoConfig.NUM_FRAMES - 1; i++) {
            try {
                final int TOTAL_NUM_PIXEL = VideoConfig.FRAMES_HEIGHT * VideoConfig.FRAMES_WIDTH;
                int frameLength = TOTAL_NUM_PIXEL*3;

                File file = new File(pathToFrameRgb + "frame" + i + ".rgb");
                File nextFile = new File(pathToFrameRgb + "frame" + (i + 1) + ".rgb");

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

                if (avgDiff >= VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD) {
                    Shot shot = new Shot(startFrame, i);
                    shots.add(shot);
                    startFrame = i + 1;
                    System.out.println(shot);
                }

                raf.close();
                nextRaf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void calculateMotionScore() {
        for (Shot shot : shots) {
            double sumAvgRgbDiff = 0;

            for (int i = shot.getStartFrame(); i < shot.getEndFrame(); i++) {
                try {
                    final int TOTAL_NUM_PIXEL = VideoConfig.FRAMES_HEIGHT * VideoConfig.FRAMES_WIDTH;
                    int frameLength = TOTAL_NUM_PIXEL*3;

                    File file = new File(pathToFrameRgb + "frame" + i + ".rgb");
                    File nextFile = new File(pathToFrameRgb + "frame" + (i + 1) + ".rgb");

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

                    sumAvgRgbDiff += (sumDiffR + sumDiffG + sumDiffB) / 3.0;
                    raf.close();
                    nextRaf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            shot.setMotionLevel(Math.round(sumAvgRgbDiff / (double) shot.getTotalNumFrames()));
        }
    }

    public void generatedSummarizedVideo() {
        int currSummarizedFrames = 0;
        List<AudioInputStream> summarizedAudioStreams = new ArrayList<>();

        for (Shot shot : shots) {
            if (currSummarizedFrames >= VideoConfig.NUM_SUMMARIZED_FRAMES) {
                this.summarizedAudioInputStream = SoundUtil.combine(summarizedAudioStreams);
                return;
            }

            currSummarizedFrames += shot.getTotalNumFrames();

            for (int i = shot.getStartFrame(); i <= shot.getEndFrame(); i++) {
                try {
                    summarizedFrames.add(ImageIO.read(new File(pathToFrame + "frame" + i + ".jpg")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            AudioInputStream audioInputStream = SoundUtil.trim(pathToAudio, shot.getStartTimeInFemtoSecond(), shot.getShotDurationInFemtoSecond());
            summarizedAudioStreams.add(audioInputStream);
        }
    }

    public List<BufferedImage> getOriginalFrames() {
        return originalFrames;
    }

    public AudioInputStream getOriginalAudioStream() {
        return originalAudioStream;
    }

    public List<BufferedImage> getSummarizedFrames() {
        return summarizedFrames;
    }

    public AudioInputStream getSummarizedAudioInputStream() {
        return summarizedAudioInputStream;
    }

    public static void main(String[] args) {
        VideoSummarizer videoSummarizer = new VideoSummarizer(args[0], args[1], args[2]);
        System.out.println("starting video player");
        new PlayVideo(videoSummarizer.getSummarizedFrames(),
                videoSummarizer.getSummarizedAudioInputStream());
    }
}
