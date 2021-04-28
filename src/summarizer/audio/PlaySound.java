package summarizer.audio;

import java.io.*;

import javax.sound.sampled.*;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {
    private InputStream waveStream;
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private Clip clip;

    /**
     * CONSTRUCTOR
     */
    public PlaySound(String fileName) {
		try {
			FileInputStream inputStream = new FileInputStream(fileName);
			//inputStream = this.getClass().getResourceAsStream(filename);
			this.waveStream = inputStream;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		AudioInputStream audioInputStream = null;
		try {
			//audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);

			//add buffer for mark/reset support, modified by Jian
			InputStream bufferedIn = new BufferedInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public PlaySound(AudioInputStream audioInputStream) {
		try {
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void play() {
		clip.start();
    }

	public void pause() {
		if (clip.isRunning()) {
			clip.stop();
		}
	}

	public void stop() {
    	clip.stop();
		clip.setFramePosition(0);
	}

	public long getCurrTimeMillisecond() {
		return (long) (clip.getMicrosecondPosition() / 1000.0);
	}

	public long getTotalDurationInSecond() {
		return (long) (clip.getMicrosecondLength() / 1000000.0);
	}

	public void setCurrTimeSecond(int sec) {
    	clip.setMicrosecondPosition(sec * 1000000);
	}

	public void setCurrTimeInMicroSecond(long microSec) {
		clip.setMicrosecondPosition(microSec);
	}
}
