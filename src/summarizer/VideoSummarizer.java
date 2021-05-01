package summarizer;

import summarizer.configs.VideoConfig;
import summarizer.configs.VideoSummarizerAnalysisParams;
import summarizer.entities.RGB;
import summarizer.entities.Shot;
import summarizer.player.PlayVideo;
import summarizer.utilities.ImageUtil;
import summarizer.utilities.OutputUtil;
import summarizer.utilities.SoundUtil;

import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static summarizer.configs.VideoConfig.AUDIO_SAMPLE_RATE;

public class VideoSummarizer {
    private final String pathToFrame;
    private final String pathToAudio;
    private final String pathToFrameRgb;

    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private final List<BufferedImage> summarizedFrames = new ArrayList<>();
    private final Map<Integer, BufferedImage> frameImageCache = new HashMap<>();
    private final Set<Integer> summarizedFramesLabelSet = new HashSet<>();
    private final TreeSet<Integer> startFramesSet = new TreeSet<>();
    private AudioInputStream summarizedAudioInputStream;
    private List<Shot> shots;

    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) throws IOException {
        this.pathToFrame = pathToFrame;
        this.pathToAudio = pathToAudio;
        this.pathToFrameRgb = pathToFrameRgb;

        System.out.println("calculating shot boundaries...");
        calculateShotBoundaries();
        System.out.println("calculating motion scores...");
        calculateMotionScore();
        System.out.println("calculating audio scores...");
        calculateAudioScore();
//      TODO: calculate face detection score
        System.out.println("sorting shots by score...");
        Shot.Sorter.sortByScoreDesc(shots);
        shots.forEach((System.out::println));
        System.out.println("generating video summaries...");
        generatedSummarizedVideo();
    }

    public void calculateShotBoundaries() {
        shots = OutputUtil.readShotBoundariesFromFile(VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_OUTPUT_FILENAME);
    }

    public void calculateMotionScore() {
        OutputUtil.setMotionVectorsFromFile(shots, VideoSummarizerAnalysisParams.MOTION_VECTORS_FILE_NAME);
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

        int samplesPerFrames = AUDIO_SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND;

        double leftChannel = 0;
        double rightChannel = 0;
        double avgAudioPerSample = 0;
        double avgAudioSumPerFrame = 0;
        int sampleIndex_PerFrame = 0;
        int frameIndex = 0;

        double[] tmp_arr = new double[VideoSummarizerAnalysisParams.AUDIO_BUFFER_SIZE];

        try{
            buffReader = new BufferedReader(reader);
            String strTmp = "";

            while((strTmp = buffReader.readLine())!=null){

                String[] audioChannels = strTmp.split("\\s+");
                leftChannel = new Double(audioChannels[0]);
                rightChannel = new Double(audioChannels[1]);
                avgAudioPerSample = (Math.abs(leftChannel) + Math.abs(rightChannel)) / VideoSummarizerAnalysisParams.AUDIO_CHANNEL;
                //avgAudioPerSample = (leftChannel + rightChannel) / VideoSummarizerAnalysisParams.AUDIO_CHANNEL;

                avgAudioSumPerFrame += avgAudioPerSample;
//              System.out.println(avgAudioSumPerFrame);

                sampleIndex_PerFrame++;
//              System.out.println("number of sample indexes in one frame: " + sampleIndex_PerFrame);
                if(sampleIndex_PerFrame == samplesPerFrames){

                    tmp_arr[frameIndex] = avgAudioSumPerFrame;
                    frameIndex++;

                    avgAudioSumPerFrame = 0;
                    sampleIndex_PerFrame = 0;
                }
//              samplesPerFrames = StdAudio.SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND; //set it back for next 1470 samples
            }

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

//        for (int i = 0; i < 31; i++){
//            System.out.println("index:" + i + " " + "tmp_arr value:" + tmp_arr[i]);
//        }

        //int intr_idx = 0;  //interrupt index when change shot
        for (Shot shot : shots) {
            double avgSumPerShot = 0;
            //int idx = 0;
            int cur_startFrame = shot.getStartFrame(), cur_endFrame = shot.getEndFrame();

            for (int i = shot.getStartFrame(); i < shot.getEndFrame(); i++) {
                avgSumPerShot += tmp_arr[i];
            }

            double audioLevel = avgSumPerShot / (double) (cur_endFrame - cur_startFrame - 1);
            System.out.println(audioLevel);
            shot.setAudioLevel(Double.parseDouble(String.format("%.5f", audioLevel)));   //shouldn't use shot.getTotalNumframe() cuz the current shot have already changed

//            System.out.println("frame: " + shot.getStartFrame());
//            avgSumPerShot = 0;
//            for (int i = intr_idx; i < tmp_arr.length; i++) {
//                if (i >= cur_startFrame && i < cur_endFrame) {
//                    System.out.println(avgSumPerShot);
//                    avgSumPerShot += tmp_arr[i];
//                } else {
//                    intr_idx = i;
//                    //double audioLevel = Math.round(avgSumPerShot / (double)(cur_endFrame - cur_startFrame));
//                    //double audioLevel = Double.parseDouble(String.format("%.3f", avgSumPerShot / (double)(cur_endFrame - cur_startFrame)));
//
//                    double audioLevel = avgSumPerShot / (double) (cur_endFrame - cur_startFrame - 1);
////                    System.out.println(audioLevel);
//
//                    //deal with audioLevel which is extremely small
////                    if(isInfinite(audioLevel)){
////                        audioLevel = 0;
////                    }
//                    shot.setAudioLevel(Double.parseDouble(String.format("%.5f", audioLevel)));   //shouldn't use shot.getTotalNumframe() cuz the current shot have already changed
//
//                    //shot.setAudioLevel(Double.parseDouble(String.format("%.0f", Math.abs(audioLevel))));   //shouldn't use shot.getTotalNumframe() cuz the current shot have already changed
//                    //System.out.println("avg audio sum per SHOT: " + avgSumPerShot);
//                    break;
//                }
//            }
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
