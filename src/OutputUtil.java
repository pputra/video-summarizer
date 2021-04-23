import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
}
