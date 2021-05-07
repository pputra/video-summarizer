package summarizer.player;

import summarizer.utilities.ImageUtil;
import summarizer.audio.PlaySound;
import summarizer.configs.VideoConfig;
import summarizer.entities.RGB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
    private final Map<Integer, BufferedImage> frameImageCache;
    private final TreeSet<Integer> startFrameSet;
    private final String RGB_PATH;
    private final PlaySound sound;

    private Thread videoTh;
    private Thread soundTh;

    private static volatile boolean isVideoPlaying = false;
    private final int FRAME_OFFSET_DELAY = 0;

    public PlayVideo(AudioInputStream audioInputStream, String rgbPath,
                     Set<Integer> summarizedFramesLabelSet, Map<Integer, BufferedImage> frameImageCache,
                     TreeSet<Integer> startFrameSet) {
        RGB_PATH = rgbPath;

        sound = new PlaySound(audioInputStream);

        this.summarizedFramesLabelSet = summarizedFramesLabelSet;
        this.frameImageCache = frameImageCache;
        this.startFrameSet = startFrameSet;

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
                        loadFrame(Math.max(0, frameIndex - FRAME_OFFSET_DELAY));
                        frameIndex = (int) (sound.getCurrTimeMillisecond() * VideoConfig.FRAMES_PER_SECOND / 1000);
                        slider.setValue((int) Math.round((sound.getCurrTimeMillisecond() / 1000.0)));
//                        Thread.sleep(900 / VideoConfig.FRAMES_PER_SECOND);
                    }
                } catch (Exception e) {
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
                    sound.pause();
                    while (!summarizedFramesLabelSet.contains(currFrameIndex)) {
                        currFrameIndex++;

                        if (currFrameIndex >= VideoConfig.NUM_FRAMES) {
                            currFrameIndex = 0;
                        }
                    }

//                    currFrameIndex = (int) (sound.getCurrTimeMillisecond() * VideoConfig.FRAMES_PER_SECOND / 1000);
                    long startTimeInMicroSec = Math.round(currFrameIndex / (float) VideoConfig.FRAMES_PER_SECOND * 1000000.0f);

                    sound.setCurrTimeInMicroSecond(startTimeInMicroSec);
                    sound.play();
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
        if (frameImageCache.containsKey(i)) {
            ImageIcon icon = new ImageIcon(frameImageCache.get(i));
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            ImageIcon newImc = new ImageIcon(newImg);
            framesLabel.setIcon(newImc);

            return;
        }

        RGB[][] rgbChannels = ImageUtil.readRgbChannels(this.RGB_PATH + "frame" + i + ".rgb",
                VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

        ImageIcon icon = new ImageIcon(ImageUtil.rgbChannelsToBufferedImage(rgbChannels));
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        ImageIcon newImc = new ImageIcon(newImg);

        framesLabel.setIcon(newImc);
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