import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
}
