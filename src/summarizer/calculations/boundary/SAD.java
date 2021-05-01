package summarizer.calculations.boundary;

import summarizer.configs.VideoConfig;
import summarizer.configs.VideoSummarizerAnalysisParams;
import summarizer.entities.RGB;
import summarizer.entities.Shot;
import summarizer.utilities.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class SAD {
    public static List<Shot> getShotBoundaries(String pathToFrameRgb) {
        List<Shot> shots = new ArrayList<>();
        int startFrame = 0;

        for (int i = 0; i < VideoConfig.NUM_FRAMES - 1; i++) {
            int sumDiffR = 0;
            int sumDiffG = 0;
            int sumDiffB = 0;

            RGB[][] currFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + i + ".rgb",
                    VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);
            RGB[][] nextFrameRgb = ImageUtil.readRgbChannels(pathToFrameRgb + "frame" + (i + 1) + ".rgb",
                    VideoConfig.FRAMES_HEIGHT, VideoConfig.FRAMES_WIDTH);

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

            int avgDiff = (sumDiffR + sumDiffG + sumDiffB) / 3;

            if (avgDiff >= VideoSummarizerAnalysisParams.SHOT_BOUNDARIES_RGB_DIFF_AVG_THRESHOLD) {
                Shot shot = new Shot(startFrame, i);
                shots.add(shot);
                startFrame = i + 1;
            }
        }

        return shots;
    }
}
