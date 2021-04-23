import javax.sound.sampled.AudioInputStream;
import java.awt.image.BufferedImage;
<<<<<<< HEAD
import java.io.*;
import java.util.*;
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> 4223a578ab064a0236feca2a315a3605dbb32ce0

public class VideoSummarizer {
    private final String pathToFrame;
    private final String pathToAudio;
    private final String pathToFrameRgb;

    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private AudioInputStream originalAudioStream;
    private final List<BufferedImage> summarizedFrames = new ArrayList<>();
    private AudioInputStream summarizedAudioInputStream;
    private final List<Shot> shots = new ArrayList<>();

    public VideoSummarizer(String pathToFrame, String pathToAudio, String pathToFrameRgb) throws IOException {
        this.pathToFrame = pathToFrame;
        this.pathToAudio = pathToAudio;
        this.pathToFrameRgb = pathToFrameRgb;

        System.out.println("calculating shot boundaries...");
        analyzeShots();
        OutputUtil.writeShotBoundariesToFile(shots, VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_OUTPUT_FILENAME);
        System.out.println("calculating motion scores...");
        calculateMotionScore();
        //TODO: calculate audio score
        calculateAudioScore();

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


    public void calculateAudioScore() throws IOException {
        //read all rows from txt file
        InputStreamReader reader = null;
        BufferedReader buffReader = null;

        /**
         *
         * revise the txt file path before u run the program
         *
         */
        String txtFilePath = "/xxx/Desktop/concert.txt";
        try{
            FileInputStream fin = new FileInputStream(txtFilePath);
            reader = new InputStreamReader(fin);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        double leftChannel = 0;
        double rightChannel = 0;
        double avgAudioPerSample = 0;
//        int sampleIndex = 0;
//        Map<Integer, Double> samplePair = new HashMap<>();
//        PriorityQueue<Map.Entry<Integer, Double>>  maxHeap = new PriorityQueue<>();  //get first n big amplitude
//        PriorityQueue<Map.Entry<Integer, Double>>  minHeap = new PriorityQueue<>();  //get last n small amplitude

        int samplesPerFrames = StdAudio.SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND;

        int totalFrames = 0;  //

        try{
            buffReader = new BufferedReader(reader);
            String strTmp = "";

            while((strTmp = buffReader.readLine())!=null){
                    double avgAudioSumPerFrame = 0;

                    String[] audioChannels = strTmp.split("\\s+");
                    leftChannel = new Double(audioChannels[0]);
                    rightChannel = new Double(audioChannels[1]);
                    avgAudioPerSample = (leftChannel + rightChannel) / 2.0;


                    while(samplesPerFrames != 0){
                        samplesPerFrames--;
                        if(avgAudioPerSample == 0)  continue;
                        avgAudioSumPerFrame += avgAudioPerSample;

                    }
                    System.out.println(avgAudioSumPerFrame);

//                    if(samplesPerFrames != 0) {
//                        avgAudioSumPerFrame /= samplesPerFrames;  //get average audio amplitude per frame
//                    }
                    totalFrames++;  //

//                    if(totalFrames >= Shot.getStartFrame() || totalFrames <= Shot.getEndFrame()){
//                        Shot.setMotionLevel(avgAudioSumPerFrame);
//                    }

                    samplesPerFrames = StdAudio.SAMPLE_RATE / VideoConfig.FRAMES_PER_SECOND; //set it back for next 1470 samples




//                    samplePair.put(sampleIndex, avgAudioPerSample);
//                    if(avgAudioPerSample != 0){
//                        if(avgAudioPerSample > 0){
//                            maxHeap.offer(new Map.Entry<Integer, Double>() {
//                                @Override
//                                public Integer getKey() {
//                                    return sampleIndex;
//                                }
//
//                                @Override
//                                public Double getValue() {
//                                    return samplePair.get(sampleIndex);
//                                }
//
//                                @Override
//                                public Double setValue(Double value) {
//                                    return samplePair.get(sampleIndex);
//                                }
//                            });
//
//                        }else{
//                            minHeap.offer(new Map.Entry<Integer, Double>() {
//                                @Override
//                                public Integer getKey() {
//                                    return sampleIndex;
//                                }
//
//                                @Override
//                                public Double getValue() {
//                                    return samplePair.get(sampleIndex);
//                                }
//
//                                @Override
//                                public Double setValue(Double value) {
//                                    return samplePair.get(sampleIndex);
//                                }
//                            });
//                        }
//                    }
//
//                    System.out.println(maxHeap.peek().getValue());
//                    System.out.println(minHeap.peek().getValue());
//
//                Integer sampleIdx =  new Integer(index);
    //                if(avgAudioPerSample != 0){
    //                    if(avgAudioPerSample > 0){
    //                        positive_amplitude.put(sampleIdx, avgAudioPerSample);
    //                    }else {
    //                        negative_amplitude.put(sampleIdx, avgAudioPerSample);
    //                    }
    //                }
    //                index++;


                    //System.out.println(avgAudioPerSample);

            }

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        System.out.println(totalFrames);
        buffReader.close();

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

    public static void main(String[] args) throws IOException {
        VideoSummarizer videoSummarizer = new VideoSummarizer(args[0], args[1], args[2]);
        System.out.println("starting video player");
        new PlayVideo(videoSummarizer.getSummarizedFrames(),
                videoSummarizer.getSummarizedAudioInputStream());
    }
}
