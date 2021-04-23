import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) {
        this.pathToFrame = pathToFrame;
        this.pathToAudio = pathToAudio;
        this.pathToFrameRgb = pathToFrameRgb;

        System.out.println("calculating shot boundaries...");
        analyzeShots();
        OutputUtil.writeShotBoundariesToFile(shots, VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_OUTPUT_FILENAME);
        System.out.println("calculating motion scores...");
        calculateMotionScore();
        //TODO: calculate audio score
        //TODO: calculate face detection score
        System.out.println("sorting shots by score...");
        Shot.Sorter.sortByScoreDesc(shots);
        shots.forEach((System.out::println));
        System.out.println("generating video summaries...");
        generatedSummarizedVideo();
    }

    public void analyzeShots() {
        int startFrame = 0;

        for (int i = 0; i < VideoConfig.NUM_FRAMES - 1; i++) {
            int sumDiffR = 0;
            int sumDiffG = 0;
            int sumDiffB = 0;

            RGB[][] currFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb",
                    VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
            RGB[][] nextFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + (i + 1) + ".rgb",
                    VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

            for(int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
                for(int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                    byte currR = currFrameRgb[y][x].getR();
                    byte currG = currFrameRgb[y][x].getG();
                    byte currB = currFrameRgb[y][x].getB();

                    byte nextR = nextFrameRgb[y][x].getR();
                    byte nextG = nextFrameRgb[y][x].getG();
                    byte nextB = nextFrameRgb[y][x].getB();

                    sumDiffR += Math.abs(currR - nextR);
                    sumDiffG += Math.abs(currG - nextG);
                    sumDiffB += Math.abs(currB - nextB);
                }
            }

            int avgDiff = (sumDiffR + sumDiffG + sumDiffB) / 3;

            if (avgDiff >= VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD) {
                Shot shot = new Shot(startFrame, i);
                shots.add(shot);
                startFrame = i + 1;
                System.out.println(shot);
            }
        }
    }

    public void calculateMotionScore() {
        for (Shot shot : shots) {
            double sumAvgRgbDiff = 0;

            for (int i = shot.getStartFrame(); i < shot.getEndFrame(); i++) {
                RGB[][] currFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb",
                        VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
                RGB[][] nextFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + (i + 1) + ".rgb",
                        VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

                int sumDiffR = 0;
                int sumDiffG = 0;
                int sumDiffB = 0;

                for(int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
                    for(int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                        byte currR = currFrameRgb[y][x].getR();
                        byte currG = currFrameRgb[y][x].getG();
                        byte currB = currFrameRgb[y][x].getB();

                        byte nextR = nextFrameRgb[y][x].getR();
                        byte nextG = nextFrameRgb[y][x].getG();
                        byte nextB = nextFrameRgb[y][x].getB();

                        sumDiffR += Math.abs(currR - nextR);
                        sumDiffG += Math.abs(currG - nextG);
                        sumDiffB += Math.abs(currB - nextB);
                    }
                }

                sumAvgRgbDiff += (sumDiffR + sumDiffG + sumDiffB) / 3.0;
            }

            shot.setMotionLevel(Math.round(sumAvgRgbDiff / (double) shot.getTotalNumFrames()));
        }
    }

    public void generatedSummarizedVideo() {
        int currSummarizedFrames = 0;
        List<AudioInputStream> summarizedAudioStreams = new ArrayList<>();
        List<Shot> summarizedShots = new ArrayList<>();

        for (Shot shot : shots) {
            if (currSummarizedFrames >= VideoConfig.NUM_SUMMARIZED_FRAMES) {
                break;
            }

            summarizedShots.add(shot);
            currSummarizedFrames += shot.getTotalNumFrames();
        }

        System.out.println("video duration: " + Math.round(currSummarizedFrames / (double) VideoConfig.FRAMES_PER_SECOND) + "s");

        Shot.Sorter.sortByTimeStampAsc(summarizedShots);

        for (Shot shot : summarizedShots) {
            for (int i = shot.getStartFrame(); i <= shot.getEndFrame(); i++) {
                RGB[][] rgbChannels = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb", VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
                BufferedImage bufferedImage = new BufferedImage(VideoConfig.FRAMES_WIDTH, VideoConfig.FRAMES_HEIGHT, BufferedImage.TYPE_INT_RGB);

                for (int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
                    for (int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                        RGB rgb = rgbChannels[y][x];
                        bufferedImage.setRGB(x, y, ImageUtil.rgbToPixel(rgb));
                    }
                }

                summarizedFrames.add(bufferedImage);
            }

            AudioInputStream audioInputStream = SoundUtil.trim(pathToAudio, shot.getStartTimeInFemtoSecond(), shot.getShotDurationInFemtoSecond());
            summarizedAudioStreams.add(audioInputStream);
        }

        this.summarizedAudioInputStream = SoundUtil.combine(summarizedAudioStreams);
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
