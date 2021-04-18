import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.List;

public class SoundUtil {
    public static AudioInputStream trim(String sourceFileName, int startSecond, int secondsToCopy) {
        AudioInputStream inputStream;
        AudioInputStream shortenedStream = null;

        try {
            File file = new File(sourceFileName);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(file);
            int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = secondsToCopy * (int) format.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shortenedStream;
    }

    public static AudioInputStream combine(List<AudioInputStream> audioInputStreamList) {
        long totalLen = 0;

        for (AudioInputStream audioInputStream : audioInputStreamList) {
            totalLen += audioInputStream.getFrameLength();
        }

        if (totalLen <= 0) {
            return null;
        }

        return new AudioInputStream(
                new SequenceInputStream(Collections.enumeration(audioInputStreamList)),
                    audioInputStreamList.get(0).getFormat(), totalLen);
    }
}