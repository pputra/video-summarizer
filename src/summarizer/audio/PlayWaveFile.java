package summarizer.audio;

import summarizer.audio.PlaySound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class PlayWaveFile {

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
     */
    public static void play(String filename) {
	// opens the inputStream
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
			//inputStream = this.getClass().getResourceAsStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// initializes the playSound Object
		PlaySound playSound = new PlaySound(filename);

		// plays the sound
			try {
				playSound.play();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
    }
}
