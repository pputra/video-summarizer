package summarizer.entities;

import summarizer.configs.VideoConfig;
import summarizer.configs.VideoSummarizerAnalysisParams;

import java.util.List;

public class Shot {
    private int startFrame;
    private int endFrame;
    private double motionLevel;
    private double audioLevel;
    private int numFaces;

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

    public int getNumFaces() {
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

    public int getTotalScore() {
        return (int) Math.round(motionLevel * VideoSummarizerAnalysisParams.MOTION_LEVEL_WEIGHT +
                audioLevel * VideoSummarizerAnalysisParams.AUDIO_LEVEL_WEIGHT +
                numFaces * VideoSummarizerAnalysisParams.NUM_FACES_WEIGHT);
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

    public void setNumFaces(int numFaces) {
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
            shots.sort((o1, o2) -> o2.getTotalScore() - o1.getTotalScore());
        }
    }
}
