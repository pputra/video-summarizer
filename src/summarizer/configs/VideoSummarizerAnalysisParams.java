package summarizer.configs;

public class VideoSummarizerAnalysisParams {
    public static final int SHOT_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD = 2000000;
    public static final double MOTION_LEVEL_WEIGHT = 0.5;
    public static final double AUDIO_LEVEL_WEIGHT = 0.5;
    public static final int AUDIO_BUFFER_SIZE = 20000;
    public static final double AUDIO_CHANNEL = 2.0;

    public static final String SHOT_BOUNDARIES_OUTPUT_FILENAME = "shot_boundaries.txt";
    public static final String NUM_DETECTED_FACES_OUTPUT_FILENAME = "num_detected_faces.txt";
    public static final String AMPLITUDES_FILE_NAME = "meridian.txt";
}
