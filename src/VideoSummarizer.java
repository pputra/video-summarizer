import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VideoSummarizer {
    private final List<BufferedImage> originalFrames = new ArrayList<>();
    private AudioInputStream originalAudioStream;
    private final List<BufferedImage> summarizedFrames = new ArrayList<>();
    private AudioInputStream summarizedAudioInputStream;

    // TODO: READ RGB FRAMES INSTEAD THEN PERFORM ANALYSIS
    // TODO: ANALYZE AUDIO FREQUENCY
    public VideoSummarizer(String pathToFrame, String pathToAudio) {
        for (int i = 0; i < VideoConfig.NUM_FRAMES; i++) {
            try {
                originalFrames.add(ImageIO.read(new File(pathToFrame + "frame" + i + ".jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream waveStream = null;

        try {
            waveStream = new FileInputStream(pathToAudio);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            InputStream bufferedIn = new BufferedInputStream(waveStream);
            originalAudioStream = AudioSystem.getAudioInputStream(bufferedIn);

        } catch (UnsupportedAudioFileException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public List<BufferedImage> getOriginalFrames() {
        return originalFrames;
    }

    public AudioInputStream getOriginalAudioStream() {
        return originalAudioStream;
    }

    public static void main(String[] args) {
        VideoSummarizer videoSummarizer = new VideoSummarizer(args[0], args[1]);
        new PlayVideo(videoSummarizer.getOriginalFrames(),
                videoSummarizer.getOriginalAudioStream());
    }
}
