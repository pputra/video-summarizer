package summarizer.utilities;

import summarizer.entities.Shot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OutputUtil {
    public static void writeShotBoundariesToFile(List<Shot> shots, String outPath) {
        try {
            FileWriter writer = new FileWriter(outPath);

            for (Shot shot : shots) {
                String output = shot.getStartFrame() + " " + shot.getEndFrame() + "\n";

                writer.write(output);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Shot> readShotBoundariesFromFile(String path) {
        List<Shot> shots = new ArrayList<>();
        try {
            File f = new File(path);
            Scanner reader = new Scanner(f);

            while (reader.hasNextLine()) {
                String[] tokens = reader.nextLine().split(" ");
                Shot shot = new Shot(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));

                shots.add(shot);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return shots;
    }

    public static Set<Integer> shotsToFrameLabelSet(List<Shot> shots) {
        Set<Integer> frameLabelSet = new HashSet<>();

        for (Shot shot : shots) {
            for (int i = shot.getStartFrame(); i <= shot.getEndFrame(); i++) {
                frameLabelSet.add(i);
            }
        }

        return frameLabelSet;
    }

    public static void setMotionVectorsFromFile(List<Shot> shots, String path) {
        try {
            File f = new File(path);
            Scanner reader = new Scanner(f);

            shots.forEach((shot -> {
                long sum = 0;

                for (int i = shot.getStartFrame(); i < shot.getEndFrame(); i++) {
                    long currVector = 0;

                    if (reader.hasNextLine()) {
                        currVector = Long.parseLong(reader.nextLine());
                    }

                    sum += currVector;
                }

                double avg = Math.round(sum / (double) (shot.getEndFrame() - shot.getStartFrame()));

                shot.setMotionLevel(avg);

                if (reader.hasNextLine()) {
                    reader.nextLine();
                }
            }));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setNumFacesFromFile(List<Shot> shots, String path) {
        try {
            File f = new File(path);
            Scanner reader = new Scanner(f);

            shots.forEach((shot -> {
                int totalCount = 0;
                int numShotContainFace = 0;

                for (int i = shot.getStartFrame(); i <= shot.getEndFrame(); i++) {
                    if (reader.hasNextLine()) {
                        String[] tokens = reader.nextLine().split(" ");
                        int numFrontalFace = Integer.parseInt(tokens[0]);
                        int numSideFace = Integer.parseInt(tokens[1]);
                        int numHeadAndShoulder = Integer.parseInt(tokens[2]);
                        int numNose = Integer.parseInt(tokens[3]);

                        if (numFrontalFace + numSideFace > 0) {
                            totalCount += numFrontalFace + numSideFace;
                            numShotContainFace++;
                        } else if (numNose > 0) {
                            totalCount += numNose;
                            numShotContainFace++;
                        }

                    }
                }

                if (numShotContainFace == 0) {
                    shot.setNumFaces((0));
                } else {
                    double avg = totalCount / (double) (numShotContainFace);
                    shot.setNumFaces((avg));
                }
            }));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
