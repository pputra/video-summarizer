import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PlayVideo extends JFrame implements ActionListener {
    private final int FRAMES_PER_SECOND = 30;
    // 3 minutes frames only
    private final int NUM_FRAMES = 5400;
    private final int FRAMES_HEIGHT = 480;
    private final int FRAMES_WIDTH = 640;

    private JLabel framesLabel;
    private final Button playButton = new Button("play");
    private final Button pauseButton = new Button("pause");
    private final Button stopButton = new Button("stop");

    private int frameIndex = 0;
    private final List<BufferedImage> frames = new ArrayList<>();
    private PlaySound sound;

    private static volatile boolean isVideoPlaying = false;

    public PlayVideo(String framesWorkDir, String soundWorkDir) {
        super("Video Summarizer");
        initVideoPlayer(framesWorkDir);

        Thread videoTh = new Thread(() -> {
            while (frameIndex < NUM_FRAMES) {
                try {
                    if (isVideoPlaying) {
                        loadFrame(frameIndex);
                        frameIndex++;
                        Thread.sleep(1000 / FRAMES_PER_SECOND);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread soundTh = new Thread(() -> {
            sound = new PlaySound(soundWorkDir);
            while (frameIndex < NUM_FRAMES) {
                if (isVideoPlaying) {
                    sound.play();
                } else {
                    sound.pause();
                }
            }
        });

        videoTh.start();
        soundTh.start();
    }

    private void initVideoPlayer(String workDir) {
        initFrames(workDir);
        framesLabel = new JLabel();
        framesLabel.setBounds(0, 0, FRAMES_WIDTH, FRAMES_HEIGHT);

        loadFrame(frameIndex);

        add(framesLabel);
        add(playButton);
        add(pauseButton);
        add(stopButton);

        setLayout(new FlowLayout());
        setSize(1280, 720);
        getContentPane().setBackground(Color.decode("#000000"));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        stopButton.addActionListener(this);

        setVisible(true);
    }

    private void initFrames(String workDir) {
        for (int i = 0; i < NUM_FRAMES; i++) {
            try {
                frames.add(ImageIO.read(new File(workDir + "frame" + i + ".jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFrame(int i) {
        framesLabel.setIcon(new ImageIcon(frames.get(i)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            System.out.println("play button clicked");
            isVideoPlaying = true;
        } else if (e.getSource() == pauseButton) {
            System.out.println("pause button clicked");
            isVideoPlaying = false;
        } else if (e.getSource() == stopButton) {
            System.out.println("stop button clicked");
            isVideoPlaying = false;
            frameIndex = 0;
            sound.stop();
        }
    }

    public static void main(String[] args) {
        new PlayVideo(args[0], args[1]);
    }
}