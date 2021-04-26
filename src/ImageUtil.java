import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageUtil {
    public static RGB[][] readRgbChannels(String path, int height, int width) {
        RGB[][] rgbChannels = new RGB[height][width];

        try {
            int frameLength = width*height*3;
            File file = new File(path);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            byte[] bytes = new byte[(int) (long) frameLength];

            raf.read(bytes);

            int ind = 0;

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    RGB rgb = new RGB(r, g, b);

                    rgbChannels[y][x] = rgb;
                    ind++;
                }
            }

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rgbChannels;
    }

    public static int rgbToPixel(RGB rgb) {
        return 0xff000000 | ((rgb.getR() & 0xff) << 16) | ((rgb.getG() & 0xff) << 8) | (rgb.getB() & 0xff);
    }

    public static BufferedImage rgbChannelsToBufferedImage(RGB[][] rgbChannels) {
        BufferedImage bufferedImage = new BufferedImage(VideoConfig.FRAMES_WIDTH, VideoConfig.FRAMES_HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < VideoConfig.FRAMES_HEIGHT; y++) {
            for (int x = 0; x < VideoConfig.FRAMES_WIDTH; x++) {
                RGB rgb = rgbChannels[y][x];
                bufferedImage.setRGB(x, y, ImageUtil.rgbToPixel(rgb));
            }
        }

        return bufferedImage;
    }
}
