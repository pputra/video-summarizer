package summarizer.calculations.boundary;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BoundaryColorHistogramDetect {

   JFrame frame;
   JLabel lbIm1;
   BufferedImage imgOne;
   int width = 320;
   int height = 180;
   int totalFrame = 16200;
   ArrayList<Integer> arrayList = new ArrayList<>();
   int[] array;//0 to the 255
   int[] nextArray=new int[256];
   String filePath;
   boolean changeArray = false;

   ///Map<Integer,Integer> map=new HashMap<>();

   /**
    * Read Image RGB
    * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
    */
   private void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
      File f;
      int sum;


      try {
         for (int i = 0; i < totalFrame; i++) {
            if (changeArray == true) {
               array=new int[256];
               changeArray=false;
            }
           // sum=0;
            int frameLength = width * height * 3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for (int y = 0; y < height; y++) {
               for (int x = 0; x < width; x++) {
                  //sum=0;
                  byte a = 0;
                  byte r = bytes[ind];
                  byte g = bytes[ind + height * width];
                  byte b = bytes[ind + height * width * 2];
                  //sum=()
                  int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                  //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                  if(changeArray==true)//new frame
                  {

                     changeArray=false;
                  }
                  else
                  {
                     
                  }
                  img.setRGB(x, y, pix);
                  ind++;
               }
            }
         }


      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

     /* try
      {
         f = new File("D:\\amusic_and_videos\\out2.jpg");
         ImageIO.write(img, "jpg", f);
      }
      catch (IOException e)
      {
         System.out.println(e);
      }*/
   }

   public void showIms(String[] args) {

      // Read a parameter from command line
      String param1 = args[1];
      System.out.println("The second parameter was: " + param1);
      filePath = args[0];
      // Read in the specified image
      imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      readImageRGB(width, height, args[0], imgOne);

      // Use label to display the image
      frame = new JFrame();
      GridBagLayout gLayout = new GridBagLayout();
      frame.getContentPane().setLayout(gLayout);

      lbIm1 = new JLabel(new ImageIcon(imgOne));

      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.CENTER;
      c.weightx = 0.5;
      c.gridx = 0;
      c.gridy = 0;

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = 1;
      frame.getContentPane().add(lbIm1, c);

      frame.pack();
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      BoundaryColorHistogramDetect ren = new BoundaryColorHistogramDetect();

      ren.showIms(args);
   }

}

