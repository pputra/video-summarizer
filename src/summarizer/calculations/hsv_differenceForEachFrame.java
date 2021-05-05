
import com.sun.jdi.IntegerValue;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;


public class hsv_differenceForEachFrame {

   JFrame frame;
   JFrame frame1;

   JLabel lbIm1;
   JLabel lbIm2;

   BufferedImage imgOne;
   BufferedImage img_origin;

   int width = 320;
   int height = 180;
   String h1;
   String h2;
   Set<Integer> set=new HashSet<Integer>();
   double[] differenceBetweenEachHsv=new double[16998];
   int count=0;

   /** Read Image RGB
    *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
    */
   private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
   {
      try
      {
         int frameLength = width*height*3;
          PrintStream out = new PrintStream("D:\\amusic_and_videos\\meridian_hsv_raw_average.txt");
          System.setOut(out);
         ArrayList<Double> hsv_average=new ArrayList<>();
         while(count<16197)
         {

            File file = new File(imgPath+"\\"+"frame"+count+".rgb");
            File file_1 = new File(imgPath+"\\"+"frame"+(count+1)+".rgb"); //

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            RandomAccessFile raf_1 = new RandomAccessFile(file_1, "r");
            raf.seek(0);
            raf_1.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];
            byte[] bytes_1 = new byte[(int) len];

            //  long len = frameLength;
            //   byte[] bytes = new byte[(int) len];

            raf.read(bytes);
            raf_1.read(bytes_1);
            int[][][] image=new int[3][512][512];
            int[][][] image_1=new int[3][512][512];
            double[][][] image_HSV=new double[3][512][512];
            double[][][] image_HSV_1=new double[3][512][512];
            double[] rgbFromHSV_return=new double[3];
            int[] rgbFromHSV_return_int=new int[3];
            byte[] rgbFromHSV_return_byte=new byte[3];
            double[][] imageAfterHSVToRGB_R= new double[512][512];
            double[][] imageAfterHSVToRGB_G= new double[512][512];
            double[][] imageAfterHSVToRGB_B= new double[512][512];
            int ind = 0;
            double sum_hsv=0;
            double sum_hsv_1=0;
            double[] bin_h=new double[360];
            for(int y = 0; y < height; y++)
            {
               for(int x = 0; x < width; x++)
               {

                  byte a = 0;
                  byte r = bytes[ind];
                  byte g = bytes[ind+height*width];
                  byte b = bytes[ind+height*width*2];
                  byte r_1 = bytes_1[ind];
                  byte g_1 = bytes_1[ind+height*width];
                  byte b_1 = bytes_1[ind+height*width*2];
                  //ind++;
                  // int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                  //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                  /////
                  image[0][x][y]=r&0xff;//r value; (int)r
              //    System.out.println("image "+image[0][x][y]);
                  image[1][x][y]=g&0xff;//g value;
                  image[2][x][y]=b&0xff;//b value
                  image_1[0][x][y]=r_1&0xff;//r value; (int)r
               //   System.out.println("image_1 "+image_1[0][x][y]);
                  image_1[1][x][y]=g_1&0xff;//g value;
                  image_1[2][x][y]=b_1&0xff;
                  ind++;
                  double[] hsv_return_from_rgb=new double[3];
                  double[] hsv_return_from_rgb_1=new double[3];
                  hsv_return_from_rgb=imageHSV_H(image[0][x][y],image[1][x][y],image[2][x][y]);
                  hsv_return_from_rgb_1=imageHSV_H(image_1[0][x][y],image_1[1][x][y],image_1[2][x][y]);
                  image_HSV[0][x][y]=hsv_return_from_rgb[0];
                //  System.out.println("dfafa"+image_HSV[0][x][y]);
                  image_HSV[1][x][y]=hsv_return_from_rgb[1];//s
                  image_HSV[2][x][y]=hsv_return_from_rgb[2];//v
                  sum_hsv=sum_hsv+image_HSV[0][x][y];
                  //System.out.println("sum_hsv "+sum_hsv);
                 // sum_hsv_1=sum_hsv_1+image_HSV[0][x][y];
                  image_HSV_1[0][x][y]=hsv_return_from_rgb_1[0];
                  image_HSV_1[1][x][y]=hsv_return_from_rgb_1[1];
                  image_HSV_1[2][x][y]=hsv_return_from_rgb_1[2];
                //  System.out.println("fdasfsa"+image_HSV_1[0][x][y]);
                  sum_hsv_1=sum_hsv_1+image_HSV_1[0][x][y];
                  //System.out.println("sum_hsv_1 "+sum_hsv_1);
                  // image_HSV[0][x][y]=imageHSV_H(image[0][x][y],image[1][x][y],image[2][x][y]);//h
                  // image_HSV[1][x][y]=imageHSV_S(image[0][x][y],image[1][x][y],image[2][x][y]);//s
                  // image_HSV[2][x][y]=imageHSV_V(image[0][x][y],image[1][x][y],image[2][x][y]);//V
                  /////hsv to rgb
                  /*rgbFromHSV_return=HSV_To_RGB(image_HSV[0][x][y], image_HSV[1][x][y],image_HSV[2][x][y]);
                  rgbFromHSV_return_int[0]=(int)rgbFromHSV_return[0];
                  rgbFromHSV_return_int[1]=(int)rgbFromHSV_return[1];
                  rgbFromHSV_return_int[2]=(int)rgbFromHSV_return[2];
                  rgbFromHSV_return_byte[0]=(byte)rgbFromHSV_return[0];
                  rgbFromHSV_return_byte[1]=(byte)rgbFromHSV_return[1];
                  rgbFromHSV_return_byte[2]=(byte)rgbFromHSV_return[2];*/
                  ////////////
                //  int pix = 0xff000000 | ((rgbFromHSV_return_byte[0] & 0xff) << 16) | ((rgbFromHSV_return_byte[1] & 0xff) << 8) | (rgbFromHSV_return_byte[2] & 0xff);
                  //int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);/////
                 // img.setRGB(x,y,pix);
                  // set.add(pix);
                  // System.out.println(set.size());

               }
            }
         //   System.out.println(sum_hsv);
           // System.out.println(sum_hsv_1);
           // System.out.println(sum_hsv-sum_hsv_1);


            differenceBetweenEachHsv[count]=Math.abs(sum_hsv_1-sum_hsv);
            System.out.println(count+" "+differenceBetweenEachHsv[count]/(320*180));
            hsv_average.add(differenceBetweenEachHsv[count]/(320*180));
         //   System.out.println(count);
            count++;
         }//while end
        // System.out.println("fdasfsa");
        /* try {
            File consoleFile = new File("D:\\amusic_and_videos\\hsv_raw.txt");
            FileOutputStream fos = new FileOutputStream(consoleFile);
            PrintStream output = new PrintStream(fos);
            // 重置输出
            System.setOut(output);
         } catch (IOException e) {
            e.printStackTrace();
         }*/
       //  PrintStream out = new PrintStream("D:\\amusic_and_videos\\hsv_raw.txt");
       //  System.setOut(out);
        /* for(int i=0;i<differenceBetweenEachHsv.length;i++)
         {
            System.out.println(differenceBetweenEachHsv[count]);
         }*/


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


   private void readImageRGB1(int width, int height, String imgPath, BufferedImage img)
   {
      try
      {
         int frameLength = width*height*3;

         File file = new File(imgPath);
         File file_1 = new File(imgPath);
         RandomAccessFile raf = new RandomAccessFile(file, "r");
         RandomAccessFile raf_1 = new RandomAccessFile(file_1, "r");
         raf.seek(0);
         raf_1.seek(0);

         long len = frameLength;
         byte[] bytes = new byte[(int) len];
         byte[] bytes_1 = new byte[(int) len];

         raf.read(bytes);
         // int[][][] image=new int[3][512][512];
         //  double[][][] image_HSV=new double[3][512][512];
         //   double[] rgbFromHSV_return=new double[3];
         //   int[] rgbFromHSV_return_int=new int[3];
         //  byte[] rgbFromHSV_return_byte=new byte[3];
         //  double[][] imageAfterHSVToRGB_R= new double[512][512];
         //  double[][] imageAfterHSVToRGB_G= new double[512][512];
         //  double[][] imageAfterHSVToRGB_B= new double[512][512];
         int ind = 0;
         for(int y = 0; y < height; y++)
         {
            for(int x = 0; x < width; x++)
            {
               byte a = 0;
               byte r = bytes[ind];
               byte g = bytes[ind+height*width];
               byte b = bytes[ind+height*width*2];

               byte r_1=bytes_1[ind];
               byte g_1=bytes_1[ind+height*width];
               byte b_1=bytes_1[ind+height*width*2];
               //ind++;
               // int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
               //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
               /////
               //   image[0][x][y]=r&0xff;//r value; (int)r
               //   image[1][x][y]=g&0xff;//g value;
               //   image[2][x][y]=b&0xff;//b value
               ind++;
               //  double[] hsv_return_from_rgb=new double[3];
               //  hsv_return_from_rgb=imageHSV_H(image[0][x][y],image[1][x][y],image[2][x][y]);
               //   image_HSV[0][x][y]=hsv_return_from_rgb[0];
               //   image_HSV[1][x][y]=hsv_return_from_rgb[1];
               //  image_HSV[2][x][y]=hsv_return_from_rgb[2];
               // image_HSV[0][x][y]=imageHSV_H(image[0][x][y],image[1][x][y],image[2][x][y]);//h
               // image_HSV[1][x][y]=imageHSV_S(image[0][x][y],image[1][x][y],image[2][x][y]);//s
               // image_HSV[2][x][y]=imageHSV_V(image[0][x][y],image[1][x][y],image[2][x][y]);//V
               //  rgbFromHSV_return=HSV_To_RGB(image_HSV[0][x][y], image_HSV[1][x][y],image_HSV[2][x][y]);
               //   rgbFromHSV_return_int[0]=(int)rgbFromHSV_return[0];
               //  rgbFromHSV_return_int[1]=(int)rgbFromHSV_return[1];
               //  rgbFromHSV_return_int[2]=(int)rgbFromHSV_return[2];
               //  rgbFromHSV_return_byte[0]=(byte)rgbFromHSV_return[0];
               // rgbFromHSV_return_byte[1]=(byte)rgbFromHSV_return[1];
               // rgbFromHSV_return_byte[2]=(byte)rgbFromHSV_return[2];
               //  int pix = 0xff000000 | ((r & 0xff) << 16) | ((rgbFromHSV_return_byte[1] & 0xff) << 8) | (rgbFromHSV_return_byte[2] & 0xff);
               int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
               int pix_1= 0xff000000 | ((r_1 & 0xff) << 16) | ((g_1 & 0xff) << 8) | (b_1 & 0xff);/////
               img.setRGB(x,y,pix);
               // set.add(pix);
               // System.out.println(set.size());

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
   /*public double[] imageHSV_H(int r,int g,int b)
   {
      double[] hsv=new double[3];
      double new_r = r / 255.0;
      double new_g = g / 255.0;
      double new_b = b / 255.0;

      // h, s, v = hue, saturation, value
      double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
      double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
      double diff = cmax - cmin; // diff of cmax and cmin.
      double h = -1, s = -1;

      // if cmax and cmax are equal then h = 0
      if (cmax == cmin)
         h = 0;

         // if cmax equal r then compute h
      else if (cmax == new_r)
         h = (60 * ((new_g - new_b) / diff) + 360) % 360;

         // if cmax equal g then compute h
      else if (cmax == new_g)
         h = (60 * ((new_b - new_r) / diff) + 120) % 360;

         // if cmax equal b then compute h
      else if (cmax == new_b)
         h = (60 * ((new_r - new_g) / diff) + 240) % 360;

      // if cmax equal zero
      if (cmax == 0)
         s = 0;
      else
         s = (diff / cmax) * 100;

      // compute v
      double v = cmax * 100;
      hsv[0]=h;
      hsv[1]=s;
      hsv[2]=v;
      return hsv;
   }*/
   public double[] imageHSV_H(int r,int g,int b)
   {
      // System.out.println("r: "+r);
      double new_r = r / 255.0;
      // System.out.println("new_r:"+new_r);
      double new_g = g / 255.0;
      // System.out.println("new_g:"+new_g);
      double new_b = b / 255.0;
      //  System.out.println("new_b:"+new_b);
      double[] hsv=new double[3];
      //double[][][] image_HSV=new double[3][512][512];
      double max,min,delta;
      double h;
      max = Math.max(new_r, Math.max(new_g, new_b)); // maximum of r, g, b
      min = Math.min(new_r, Math.min(new_g, new_b)); // minimum of r, g, b
     /* max= r > b ? ( r > g ? r : g ) : ( b > g ? b : g );
      min= r < b ? ( r < g ? r : g ) : ( b < g ? b : g );*/
      delta=max-min;
      double v=max;
      double s;

      if(max != 0)
      {
         s = delta / max;
      }
      else {
         s=0;
         h=-1;
         hsv[0]=h;
         hsv[1]=s;
         hsv[2]=v;
         return hsv;
      }
      if(delta==0)
      {
         h=0;
      }
      else
      {
         if( new_r == max )
         {
            h = ( new_g - new_b ) / delta;		// between yellow & magenta
         }

         else if( new_g == max )
         {
            h = 2 + ( new_b - new_r ) / delta;	// between cyan & yellow
         }

         else
         {
            h = 4 + ( new_r - new_g ) / delta;	// between magenta & cyan
         }

         h *= 60;				// degrees
         if( h < 0 )
         {
            h += 360;
         }


         if(Integer.valueOf(h1)>0||Integer.valueOf(h2)<359||Float.parseFloat(h1)>0.0||Float.parseFloat(h2)<359.0)
         {
            if(h<Integer.valueOf(h1)||h>Integer.valueOf(h2)||h<Float.parseFloat(h1)||h>Float.parseFloat(h2))
            {
               s=0;
               h=180;
            }
         }

      }


      hsv[0]=h;
      // System.out.println("hsv[0] is h:"+hsv[0]);
      hsv[1]=s;
      // System.out.println("hsv[1] is s:"+hsv[1]);
      hsv[2]=v;
      // System.out.println("hsv[2] is v:"+hsv[2]);
      return hsv;
   }
   /* public double imageHSV_V(int r,int g, int b)
    {
       int max,min,delta;
       double h;
       max= r > b ? ( r > g ? r : g ) : ( b > g ? b : g );
       min= r < b ? ( r < g ? r : g ) : ( b < g ? b : g );
       delta=max-min;
       double v=(double)max;
       return v;
    }
    public double imageHSV_S(int r,int g, int b) {
       int max, min, delta;
       double h;
       double s;
       max = r > b ? (r > g ? r : g) : (b > g ? b : g);
       min = r < b ? (r < g ? r : g) : (b < g ? b : g);
       delta = max - min;
       double v = (double) max;
       if (max != 0) {
          s = delta / max;
       } else {
           s = 0;
          h = -1;

       }
       return s;
    }*/
   public double[] HSV_To_RGB(double h,double s,double v)
   {
      double[] rgb_array=new double[3];
      int i;
      double f, p, q, t;
      double r,g,b;
      if( s == 0 ) {
         // achromatic (grey)
         r = v;
         g=v;
         b=v;
         rgb_array[0]=255*r;
         // System.out.println("rgb_array[0]"+rgb_array[0]);
         rgb_array[1]=255*g;
         // System.out.println("rgb_array[1]"+rgb_array[1]);
         rgb_array[2]=255*b;
         // System.out.println("rgb_array[2]"+rgb_array[2]);
         return  rgb_array;
      }
      h /= 60;			// sector 0 to 5
      i = (int) Math.floor(h);
      f = h - i;			// factorial part of h
      p = v * ( 1 - s );
      q = v * ( 1 - s * f );
      t = v * ( 1 - s * ( 1 - f ) );
      switch( i ) {
         case 0:
            r = v;
            g = t;
            b = p;
            break;
         case 1:
            r = q;
            g = v;
            b = p;
            break;
         case 2:
            r = p;
            g = v;
            b = t;
            break;
         case 3:
            r = p;
            g = q;
            b = v;
            break;
         case 4:
            r = t;
            g = p;
            b = v;
            break;
         default:		// case 5:
            r = v;
            g = p;
            b = q;
            break;
      }
      rgb_array[0]=255*r;
      // System.out.println("rgb_array[0]"+rgb_array[0]);
      rgb_array[1]=255*g;
      // System.out.println("rgb_array[1]"+rgb_array[1]);
      rgb_array[2]=255*b;
      // System.out.println("rgb_array[2]"+rgb_array[2]);
      return  rgb_array;
   }
   public void showIms(String[] args){

      // Read a parameter from command line
     /* String param1 = args[1];
      System.out.println("The second parameter was: " + param1);*/

      h1=args[1];
      System.out.println("h1："+h1);
      h2=args[2];
      System.out.println("h2："+h2);
      if(Integer.valueOf(h1)>Integer.valueOf(h2))
      {
         System.out.println("h1 should less than h2");
         System.exit(-1);
      }

      // Read in the specified image
      imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      img_origin=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      readImageRGB(width, height, args[0], imgOne);
     // readImageRGB1(width, height, args[0], img_origin);

      // Use label to display the image
      /*frame = new JFrame("image after hsv transformation");
      frame1 = new JFrame("origin image");
      GridBagLayout gLayout = new GridBagLayout();
      frame.getContentPane().setLayout(gLayout);
      frame1.getContentPane().setLayout(gLayout);

      lbIm1 = new JLabel(new ImageIcon(imgOne));
      lbIm2 = new JLabel(new ImageIcon(img_origin));
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
      frame1.getContentPane().add(lbIm2, c);

      frame.pack();
      frame.setVisible(true);
      frame1.pack();
      frame1.setVisible(true);*/
   }

   public static void main(String[] args) {
      hsv_differenceForEachFrame ren = new hsv_differenceForEachFrame();
      ren.showIms(args);
   }

}
