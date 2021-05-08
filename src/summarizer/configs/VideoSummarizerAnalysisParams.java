package summarizer.configs;

public class VideoSummarizerAnalysisParams {
    public static final int SHOT_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD = 2000000;
    public static final double MOTION_LEVEL_WEIGHT = 0.33;
    public static final double AUDIO_LEVEL_WEIGHT = 0.33;
    public static final double NUM_FACES_WEIGHT = 0.33;
    public static final int AUDIO_BUFFER_SIZE = 20000;
    public static final double AUDIO_CHANNEL = 2.0;

    public static final String SHOT_BOUNDARIES_OUTPUT_FILENAME = "boundaries/test_data.txt";
    public static final String NUM_DETECTED_FACES_OUTPUT_FILENAME = "faces/test_data.txt";
    public static final String AMPLITUDES_FILE_NAME = "amplitudes/final/test_data.txt";
    public static final String MOTION_VECTORS_FILE_NAME = "motionVectors/test_data.txt";
}
