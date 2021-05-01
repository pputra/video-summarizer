package summarizer.calculations.motion;

import summarizer.configs.VideoConfig;
import summarizer.entities.RGB;
import summarizer.entities.Shot;
import summarizer.utilities.ImageUtil;

import java.util.List;

public class SAD {
    public static void calculate(List<Shot> shots, String pathToFrameRgb) {
        for (Shot shot : shots) {
            double sumAvgRgbDiff = 0;

            for (int i = shot.getStartFrame(); i < shot.getEndFrame(); i++) {
                RGB[][] currFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb",
                        VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
                RGB[][] nextFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + (i + 1) + ".rgb",
                        VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

                int sumDiffR = 0;
                int sumDiffG = 0;
                int sumDiffB = 0;

                for(int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
                    for(int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                        byte currR = currFrameRgb[y][x].getR();
                        byte currG = currFrameRgb[y][x].getG();
                        byte currB = currFrameRgb[y][x].getB();

                        byte nextR = nextFrameRgb[y][x].getR();
                        byte nextG = nextFrameRgb[y][x].getG();
                        byte nextB = nextFrameRgb[y][x].getB();

                        sumDiffR += Math.abs(currR - nextR);
                        sumDiffG += Math.abs(currG - nextG);
                        sumDiffB += Math.abs(currB - nextB);
                    }
                }

                sumAvgRgbDiff += (sumDiffR + sumDiffG + sumDiffB) / 3.0;
            }

            shot.setMotionLevel(Math.round(sumAvgRgbDiff / (double) shot.getTotalNumFrames()));
        }
    }
}
