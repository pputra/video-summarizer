import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PlayVideo extends JFrame implements ActionListener {
    private final int FRAMES_PER_SECOND = 30;
    private final int NUM_FRAMES = 16200;
    private final int FRAMES_HEIGHT = 480;
    private final int FRAMES_WIDTH = 640;

    private final JLabel framesLabel;
    private final Button playButton = new Button("play");
    private final Button pauseButton = new Button("pause");
    private final Button stopButton = new Button("stop");

    private int frameIndex = 0;
    private final String[] frames = new String[NUM_FRAMES];

    private static volatile boolean isVideoReady = false;

    public PlayVideo(String workDir) {
        super("Video Summarizer");
        initFrames(workDir);
        framesLabel = new JLabel();
        framesLabel.setBounds(0, 0, FRAMES_WIDTH, FRAMES_HEIGHT);

        loadFrame(0);

        Timer timer = new Timer(1000 / FRAMES_PER_SECOND, e -> {
            loadFrame(frameIndex);
            frameIndex++;
        });

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
        timer.start();
        isVideoReady = true;
    }

    private void initFrames(String workDir) {
        for (int i = 1; i < frames.length; i++) {
            frames[i] = workDir + "frame" + i + ".jpg";
        }
    }

    private void loadFrame(int i) {
        ImageIcon icon = new ImageIcon(frames[i]);
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(FRAMES_WIDTH, FRAMES_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon newImc = new ImageIcon(newImg);
        framesLabel.setIcon(newImc);
    }

    public static void main(String[] args) {
        Thread videoTh = new Thread(() -> {
            new PlayVideo(args[0]);
        });

        Thread soundTh = new Thread(() -> {
            while (!isVideoReady);
            PlayWaveFile.play(args[1]);
        });

        videoTh.start();
        soundTh.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}