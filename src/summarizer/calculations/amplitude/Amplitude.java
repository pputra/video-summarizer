package summarizer.calculations.amplitude;

import summarizer.audio.WaveFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Amplitude {


   public static void main(String[] args) throws IOException, UnsupportedAudioFileException {


      File file = new File("D:\\amusic_and_videos\\proj_dataset\\database\\audio\\concert.wav");
      // File file = args[0];
      WaveFile wav = new WaveFile(file);

      int amplitudeExample = wav.getSampleInt(140); // 140th amplitude value.
      System.out.println( wav.getFramesCount());


     /* for (int i = 0; i < wav.getFramesCount(); i++) {
         int amplitude = wav.getSampleInt(i);

         System.out.println(amplitude);
      }*/
   }
}
