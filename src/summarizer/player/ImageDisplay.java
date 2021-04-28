package summarizer.player;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;


public class ImageDisplay {

   Timer timer=new Timer();

   String filePath_1;
   JFrame frame;
   JLabel lbIm1;
   BufferedImage imgOne;
   BufferedImage imgTwo;
   int width = 320;
   int height = 180;
   int count=0;
   boolean begin=true;
   int frameRate=30;
   /** Read Image RGB
    *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
    */

   class Read extends TimerTask {

      String filePath;

      /*public Read()
      {

        // filePath=args[0]+"\\"+"frame"+count+".rgb";
         count++;
         System.out.println(count);
      }*/
      public void run()
      {
         // readImageRGB(width, height, filePath_1+count+".rgb", imgOne);
        // count=count+1;
       //  System.out.println("count "+count);


         if(begin==true)
         {
            readImageRGB(width, height, filePath_1+count+".rgb", imgOne);
            count++;
            readImageRGB(width, height, filePath_1+(count+1)+".rgb", imgTwo);
           // frame = new JFrame();
            lbIm1.setIcon(new ImageIcon(imgOne));
            GridBagConstraints c = new GridBagConstraints();
            frame.getContentPane().add(lbIm1,c);
            frame.pack();
           /* GridBagLayout gLayout = new GridBagLayout();
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

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
           // frame.setVisible(false); //you can't see me!
            frame.dispose(); *///Destroy the JFrame object
            begin=false;
            imgOne=imgTwo;
         }
         else
         {
            readImageRGB(width, height, filePath_1+(count)+".rgb", imgTwo);
            //frame = new JFrame();
            ///////
            lbIm1.setIcon(new ImageIcon(imgOne));
            GridBagConstraints c = new GridBagConstraints();
            frame.getContentPane().add(lbIm1,c);
            frame.pack();
            /*GridBagLayout gLayout = new GridBagLayout();
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

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            begin=false;*/
            //////
            imgOne=imgTwo;
            count++;
         }

        if(count==16200)
        {
           timer.cancel();
        }

      }
   }


   public void readImageRGB(int width, int height, String imgPath, BufferedImage img)
   {
      try
      {
         int frameLength = width*height*3;

         File file = new File(imgPath);
         RandomAccessFile raf = new RandomAccessFile(file, "r");
         raf.seek(0);

         long len = frameLength;
         byte[] bytes = new byte[(int) len];

         raf.read(bytes);

         int ind = 0;
         for(int y = 0; y < height; y++)
         {
            for(int x = 0; x < width; x++)
            {
               byte a = 0;
               byte r = bytes[ind];
               byte g = bytes[ind+height*width];
               byte b = bytes[ind+height*width*2];

               int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
               //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
               img.setRGB(x,y,pix);
               ind++;
            }
         }
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void showIms(String[] args){

      // Read a parameter from command line
      String param1 = args[1];
      System.out.println("The second parameter was: " + param1);
      // filePath_1=args[0]+"\\"+"frame"+count+".rgb";
      filePath_1=args[0]+"\\"+"frame";

      // Read in the specified image
      imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      imgTwo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      ///////
   //   GridBagLayout gLayout = new GridBagLayout();
    //  frame.getContentPane().setLayout(gLayout);
      timer = new Timer();
      /*timer.schedule(new Read(),
              0,        //initial delay
              2*1000);  //subsequent rate*/
      timer.schedule(new Read(),
              0,        //initial delay
              (long)(1000*0.999)
                      /frameRate);
      System.out.println((long)(1000)
              /frameRate);
     /* Read read=new Read();
      read.run();
      count=count+90;*/

      /*Read read1=new Read(args);
      read1.run();*/
      // timer.schedule();
      // readImageRGB(width, height, args[0], imgOne);

      // Use label to display the image
      /*frame = new JFrame();
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
      frame.setVisible(true);*/
   }

   public static void main(String[] args) {
      ImageDisplay ren = new ImageDisplay();
      ren.showIms(args);
      //////sound play
      if (args.length < 1) {
         System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
         return;
      }
      String filename = args[1];

      // opens the inputStream
      FileInputStream inputStream;
      try {
         inputStream = new FileInputStream(filename);
         //inputStream = this.getClass().getResourceAsStream(filename);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         return;
      }

      // initializes the playSound Object
//      PlaySound playSound = new PlaySound(inputStream);
//
//      // plays the sound
//      try {
//         playSound.play();
//      } catch (PlayWaveException e) {
//         e.printStackTrace();
//         return;
//      }
   }

}
