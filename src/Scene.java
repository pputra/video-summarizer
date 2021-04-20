public class Scene implements Comparable {
    private int startFrame;
    private int endFrame;
    private double motionLevel;
    private double audioLevel;

    public Scene(int startFrame, int endFrame, double motionLevel) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.motionLevel = motionLevel;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "startFrame=" + startFrame +
                ", endFrame=" + endFrame +
                ", motionLevel=" + motionLevel +
                ", audioLevel=" + audioLevel +
                '}';
    }


    @Override
    public int compareTo(Object o) {
        return (int) Math.round(((Scene) o).motionLevel - this.motionLevel);
    }
}
