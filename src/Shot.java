import java.util.List;

public class Shot {
    private int startFrame;
    private int endFrame;
    private double motionLevel;
    private double audioLevel;


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

    public int getTotalNumFrames() {
        return endFrame - startFrame + 1;
    }

    public long getStartTimeInFemtoSecond() {
        return Math.round(startFrame / (float) VideoConfig.FRAMES_PER_SECOND * 1000000000000000.0);
    }

    public long getShotDurationInFemtoSecond() {
        return Math.round(getTotalNumFrames() / (float) VideoConfig.FRAMES_PER_SECOND * 1000000000000000.0);
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

    @Override
    public String toString() {
        return "Shot{" +
                "startFrame=" + startFrame +
                ", endFrame=" + endFrame +
                ", motionLevel=" + motionLevel +
                ", audioLevel=" + audioLevel +
                '}';
    }

    public static class Sorter {
        public static void sortByTimeStampAsc(List<Shot> shots) {
            shots.sort((o1, o2) -> o1.getStartFrame() - o2.getEndFrame());
        }

        public static void sortByScoreDesc(List<Shot> shots) {
            shots.sort((o1, o2) -> {
                int score1 = (int) Math.round(o1.motionLevel * VideoSummarizerAnalysisParams.MOTION_LEVEL_WEIGHT +
                        o1.getAudioLevel() * VideoSummarizerAnalysisParams.AUDIO_LEVEL_WEIGHT);

                int score2 = (int) Math.round(o2.motionLevel * VideoSummarizerAnalysisParams.MOTION_LEVEL_WEIGHT +
                        o2.getAudioLevel() * VideoSummarizerAnalysisParams.AUDIO_LEVEL_WEIGHT);

                return score2 - score1;
            });
        }
    }
}
