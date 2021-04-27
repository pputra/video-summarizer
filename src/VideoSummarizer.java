import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class VideoSummarizer {
    private final String pathToFrame;
    private final String pathToAudio;
    private final String pathToFrameRgb;

    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private AudioInputStream originalAudioStream;
    private List<BufferedImage> summarizedFrames = new ArrayList<>();
    private Map<Integer, BufferedImage> frameImageCache = new HashMap<>();
    private Set<Integer> summarizedFramesLabelSet = new HashSet<>();
    private TreeSet<Integer> startFramesSet = new TreeSet<>();
    private AudioInputStream summarizedAudioInputStream;
    private List<Shot> shots;

    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) throws IOException {
        this.pathToFrame = pathToFrame;
        this.pathToAudio = pathToAudio;
        this.pathToFrameRgb = pathToFrameRgb;

        System.out.println("calculating shot boundaries...");
        shots = OutputUtil.readShotBoundariesFromFile(VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_OUTPUT_FILENAME);
        System.out.println("calculating motion scores...");
        calculateMotionScore();
//        //TODO: calculate audio score
//        calculateAudioScore();
//        //TODO: calculate face detection score
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


    public void calculateAudioScore() throws IOException {

        InputStreamReader reader = null;
        BufferedReader buffReader = null;

        /**
         * revise the amplitutde txt file name before u run the program
         * standard name format: amplitudes.txt
         */

        try{
            FileInputStream fin = new FileInputStream(VideoSummarizerAnalysisParams.AMPLITUDES_FILE_NAME);
            reader = new InputStreamReader(fin);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        int samplesPerFrames = StdAudio.SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND;

        double leftChannel = 0;
        double rightChannel = 0;
        double avgAudioPerSample = 0;
        double avgAudioSumPerFrame = 0;
        //double avgAudioSumPerShot = 0;

        int sampleIndex_PerFrame = 0;
        int frameIndex = 0;
        //boolean isInOneShot = true;

        double[] tmp_arr = new double[20000];

        try{
            buffReader = new BufferedReader(reader);
            String strTmp = "";

            while((strTmp = buffReader.readLine())!=null){

                String[] audioChannels = strTmp.split("\\s+");
                leftChannel = new Double(audioChannels[0]);
                rightChannel = new Double(audioChannels[1]);
                avgAudioPerSample = (leftChannel + rightChannel) / 2.0;


                avgAudioSumPerFrame += avgAudioPerSample;
//                  System.out.println(avgAudioSumPerFrame);

                sampleIndex_PerFrame++;
//                    System.out.println("number of sample indexes in one frame: " + sampleIndex_PerFrame);
                if(sampleIndex_PerFrame == samplesPerFrames){
                    /* get average audio amplitude per frame */
                    //double avgAudioPerFrame = avgAudioSumPerFrame / (double)samplesPerFrames;
                    //System.out.println(avgAudioSumPerFrame);

                    //tmp_arr[frameIndex] = avgAudioPerFrame;
                    tmp_arr[frameIndex] = avgAudioSumPerFrame;

                    frameIndex++;

                    avgAudioSumPerFrame = 0;
                    sampleIndex_PerFrame = 0;
                }
//                    samplesPerFrames = StdAudio.SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND; //set it back for next 1470 samples
            }

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        int intr_idx = 0;  //interrupt index when change shot
        for (Shot shot : shots) {
            double avgSumPerShot = 0;
            //int idx = 0;
            int cur_startFrame = shot.getStartFrame(), cur_endFrame = shot.getEndFrame();


            avgSumPerShot = 0;
            for (int i = intr_idx; i < tmp_arr.length; i++) {
                if (i >= cur_startFrame && i <= cur_endFrame) {
                    avgSumPerShot += tmp_arr[i];
                } else {
                    intr_idx = i;
                    //double audioLevel = Math.round(avgSumPerShot / (double)(cur_endFrame - cur_startFrame));

                    //double audioLevel = Double.parseDouble(String.format("%.3f", avgSumPerShot / (double)(cur_endFrame - cur_startFrame)));
                    shot.setAudioLevel(Double.parseDouble(String.format("%.3f", avgSumPerShot / (double) (cur_endFrame - cur_startFrame))));   //shouldn't use shot.getTotalNumframe() cuz the current shot have already changed
                    System.out.println("avg audio sum per SHOT: " + avgSumPerShot);
                    break;
                }
            }
        }
        //System.out.println(frameIndex);
        buffReader.close();
    }


    public void generatedSummarizedVideo() {
        int currSummarizedFrames = 0;
        List<Shot> summarizedShots = new ArrayList<>();

        for (Shot shot : shots) {
            if (currSummarizedFrames >= VideoConfig.NUM_SUMMARIZED_FRAMES) {
                break;
            }

            startFramesSet.add(shot.getStartFrame());

            summarizedShots.add(shot);

            for (int i = shot.getStartFrame(); i <= shot.getEndFrame(); i++) {
                summarizedFramesLabelSet.add(i);
                RGB[][] rgbChannels = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb", VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
                frameImageCache.put(i, ImageUtil.rgbChannelsToBufferedImage(rgbChannels));
            }
            currSummarizedFrames += shot.getTotalNumFrames();
        }
        Shot.Sorter.sortByTimeStampAsc(summarizedShots);

//        System.out.println("DEBUG");
//        summarizedShots.forEach((System.out::println));

        System.out.println("video duration: " + Math.round(currSummarizedFrames / (double) VideoConfig.FRAMES_PER_SECOND) + "s");

//        for (int i = 2771; i < 3083; i++) {
//            startFramesSet.add(2771);
//            summarizedFramesLabelSet.add(i);
//            RGB[][] rgbChannels = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb", VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
//            frameImageCache.put(i, ImageUtil.rgbChannelsToBufferedImage(rgbChannels));
//        }

        summarizedAudioInputStream = SoundUtil.readAudioInputStream(pathToAudio);
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

    public Set<Integer> getSummarizedFramesLabelSet() {
        return summarizedFramesLabelSet;
    }

    public Map<Integer, BufferedImage> getFrameImageCache() {
        return frameImageCache;
    }

    public TreeSet<Integer> getStartFramesSet() {
        return startFramesSet;
    }

    public static void main(String[] args) throws IOException {
        VideoSummarizer videoSummarizer = new VideoSummarizer(args[0], args[1], args[2]);
        System.out.println("starting video player");
        new PlayVideo(videoSummarizer.getSummarizedAudioInputStream(), args[2],
                videoSummarizer.getSummarizedFramesLabelSet(), videoSummarizer.getFrameImageCache(), videoSummarizer.getStartFramesSet());
    }
}
