import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PlayVideo extends JFrame implements ActionListener, ChangeListener {
    private JLabel framesLabel;
    private final Button playButton = new Button("play");
    private final Button pauseButton = new Button("pause");
    private final Button stopButton = new Button("stop");
    private JSlider slider;

    private int frameIndex = 0;
    private final Set<Integer> summarizedFramesLabelSet;
    private final String RGB_PATH;
    private final PlaySound sound;

    private Thread videoTh;
    private Thread soundTh;

    private static volatile boolean isVideoPlaying = false;

    public PlayVideo(AudioInputStream audioInputStream, String rgbPath, Set<Integer> summarizedFramesLabelSet) {
        RGB_PATH = rgbPath;

        sound = new PlaySound(audioInputStream);

        this.summarizedFramesLabelSet = summarizedFramesLabelSet;

        initVideoPlayer();

        videoTh = createVideoThread();

        soundTh = createSoundThread();

        videoTh.start();

        soundTh.start();
    }

    private Thread createVideoThread() {
        return new Thread(() -> {
            while (frameIndex < VideoConfig.NUM_FRAMES) {
                try {
                    if (isVideoPlaying) {
                        loadFrame(frameIndex);
                        frameIndex = (int) (sound.getCurrTimeMillisecond() * VideoConfig.FRAMES_PER_SECOND / 1000);
                        slider.setValue((int) Math.round((sound.getCurrTimeMillisecond() / 1000.0)));
                        Thread.sleep(1000 / VideoConfig.FRAMES_PER_SECOND);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Thread createSoundThread() {
        return new Thread(() -> {
            while (frameIndex < VideoConfig.NUM_FRAMES) {
                int currFrameIndex = (int) (sound.getCurrTimeMillisecond() * VideoConfig.FRAMES_PER_SECOND / 1000);

                if (!summarizedFramesLabelSet.contains(currFrameIndex)) {
                    while (!summarizedFramesLabelSet.contains(currFrameIndex)) {
                        currFrameIndex++;

                        if (currFrameIndex >= VideoConfig.NUM_FRAMES) {
                            currFrameIndex = 0;
                        }
                    }

                    long startTimeInMicroSec = Math.round(currFrameIndex / (float) VideoConfig.FRAMES_PER_SECOND * 1000000.0f);

                    sound.setCurrTimeInMicroSecond(startTimeInMicroSec);
                }

                if (isVideoPlaying) {
                    sound.play();
                } else {
                    sound.pause();
                }
            }
        });
    }

    private void initVideoPlayer() {
        framesLabel = new JLabel();
        framesLabel.setBounds(0, 0, VideoConfig.FRAMES_WIDTH, VideoConfig.FRAMES_HEIGHT);

        loadFrame(frameIndex);

        add(framesLabel);
        add(playButton);
        add(pauseButton);
        add(stopButton);

        slider = new JSlider(JSlider.HORIZONTAL, 0, (int) sound.getTotalDurationInSecond(), 1);
        slider.setBackground(Color.WHITE);
        slider.setOpaque(true);
        add(slider);

        setLayout(new FlowLayout());
        setSize(1280, 720);
        getContentPane().setBackground(Color.decode("#000000"));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        stopButton.addActionListener(this);
        slider.addChangeListener(this);

        setVisible(true);
    }

    private void loadFrame(int i) {
        RGB[][] rgbChannels = ImageUtil.readRgbChannels(this.RGB_PATH + "frame" + i + ".rgb",
                VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

        framesLabel.setIcon(new ImageIcon(ImageUtil.rgbChannelsToBufferedImage(rgbChannels)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            boolean userDidReplay = frameIndex >= VideoConfig.NUM_FRAMES - 1;

            if (userDidReplay) {
                frameIndex = 0;
                slider.setValue(0);
                videoTh = createVideoThread();
                sound.stop();
                soundTh = createSoundThread();
                videoTh.start();
                soundTh.start();
            }

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
            slider.setValue(0);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int currSoundTimeInSecond = (int) sound.getCurrTimeMillisecond() / 1000;
        int currSliderTimeInSecond = ((JSlider) e.getSource()).getValue();

        boolean userDidModifyTheSlider = Math.abs(currSoundTimeInSecond - currSliderTimeInSecond) > 1;

        if (userDidModifyTheSlider) {
            sound.setCurrTimeSecond(currSliderTimeInSecond);
        }
    }
}