import java.awt.Color;
import java.awt.Image;
import javax.swing.*;

public class PlayVideo extends JFrame {
    private final int FRAMES_PER_SECOND = 30;
    private final int NUM_FRAMES = 16200;

    private final JLabel label;
    private int frameIndex = 0;
    private final String[] frames = new String[NUM_FRAMES];

    public PlayVideo(String workDir) {
        super("Video Summarizer");
        initFrames(workDir);
        label = new JLabel();
        label.setBounds(0, 0, 1280, 720);

        loadFrame(0);

        Timer timer = new Timer(1000 / FRAMES_PER_SECOND, e -> {
            loadFrame(frameIndex);
            frameIndex++;
        });

        add(label);
        timer.start();
        setLayout(null);
        setSize(1280, 720);
        getContentPane().setBackground(Color.decode("#000000"));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initFrames(String workDir) {
        for (int i = 1; i < frames.length; i++) {
            frames[i] = workDir + "project_dataset/frames/soccer/frame" + i + ".jpg";
        }
    }

    private void loadFrame(int i) {
        ImageIcon icon = new ImageIcon(frames[i]);
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon newImc = new ImageIcon(newImg);
        label.setIcon(newImc);
    }

    public static void main(String[] args) {
        new PlayVideo(args[0]);
    }
}