public class Shot implements Comparable {
    private int startFrame;
    private int endFrame;
    private double motionLevel;
    private double audioLevel;

    public Shot(int startFrame, int endFrame, double motionLevel) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.motionLevel = motionLevel;
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

    @Override
    public String toString() {
        return "Shot{" +
                "startFrame=" + startFrame +
                ", endFrame=" + endFrame +
                ", motionLevel=" + motionLevel +
                ", audioLevel=" + audioLevel +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return (int) Math.round(((Shot) o).motionLevel - this.motionLevel);
    }
}
