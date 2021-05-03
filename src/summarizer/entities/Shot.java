package summarizer.entities;

import summarizer.configs.VideoConfig;
import summarizer.configs.VideoSummarizerAnalysisParams;

import java.util.List;

public class Shot {
    private int startFrame;
    private int endFrame;
    private double motionLevel;
    private double audioLevel;
    private double numFaces;

    public enum ScoringMetricTypes {
        MOTION,
        AUDIO,
        FACE
    }

    public Shot(){ }

    public Shot(int startFrame, int endFrame) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public double getMotionLevel() {
        return motionLevel;
    }

    public double getAudioLevel() {
        return audioLevel;
    }

    public double getNumFaces() {
        return numFaces;
    }

    public int getTotalNumFrames() {
        return endFrame - startFrame + 1;
    }

    public long getStartTimeInFemtoSecond() {
        return Math.round(startFrame / (float) VideoConfig.FRAMES_PER_SECOND * 1000000000000000.0);
    }

    public long getShotDurationInFemtoSecond() {
        return Math.round(getTotalNumFrames() / (float) VideoConfig.FRAMES_PER_SECOND * 1000000000000000.0);
    }

    public double getTotalScore() {
//        if (numFaces <= 0) {
//            return 0;
//        }

        return motionLevel * VideoSummarizerAnalysisParams.MOTION_LEVEL_WEIGHT +
                audioLevel * VideoSummarizerAnalysisParams.AUDIO_LEVEL_WEIGHT +
                numFaces * VideoSummarizerAnalysisParams.NUM_FACES_WEIGHT;
    }

    public static void normalizeScore(List<Shot> shots, ScoringMetricTypes type) {
        double max = -1.0 * Double.MAX_VALUE;
        double min = Double.MAX_VALUE;

        for (Shot shot : shots) {
            double currScore = 0.0;

            switch (type) {
                case MOTION:
                    currScore = shot.getMotionLevel();
                    break;
                case AUDIO:
                    currScore = shot.getAudioLevel();
                    break;
                case FACE:
                    currScore = shot.getNumFaces();
                    break;
            }

            max = Math.max(max, currScore);
            min = Math.min(min, currScore);
        }

        double maxMinDiff = max - min;

        for (Shot shot : shots) {
            switch (type) {
                case MOTION:
                    shot.setMotionLevel(maxMinDiff > 0? ((shot.getMotionLevel() - min) / maxMinDiff) : 0);
                    break;
                case AUDIO:
                    shot.setAudioLevel(maxMinDiff > 0? ((shot.getAudioLevel() - min) / maxMinDiff) : 0);
                    break;
                case FACE:
                    shot.setNumFaces(maxMinDiff > 0? ((shot.getNumFaces() - min) / maxMinDiff) : 0);
                    break;
            }
        }
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public void setMotionLevel(double motionLevel) {
        this.motionLevel = motionLevel;
    }

    public void setAudioLevel(double audioLevel){
        this.audioLevel = audioLevel;
    }

    public void setNumFaces(double numFaces) {
        this.numFaces = numFaces;
    }

    @Override
    public String toString() {
        return "Shot{" +
                "startFrame=" + startFrame +
                ", endFrame=" + endFrame +
                ", motionLevel=" + motionLevel +
                ", audioLevel=" + audioLevel +
                ", numFaces=" + numFaces +
                '}';
    }

    public static class Sorter {
        public static void sortByTimeStampAsc(List<Shot> shots) {
            shots.sort((o1, o2) -> o1.getStartFrame() - o2.getEndFrame());
        }

        public static void sortByScoreDesc(List<Shot> shots) {
            shots.sort((o1, o2) -> Double.compare(o2.getTotalScore(), o1.getTotalScore()));
        }
    }
}
