
import com.sun.jdi.IntegerValue;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;


public class entropy_calculation_rgb {

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
   double[] entropy_rgbForEachFrame=new double[16998];
   double[] variants=new double[16998];
   double[] variants_hsv=new double[16998];
   int count=0;

   /** Read Image RGB
    *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
    */
   private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
   {
      try
      {
         int frameLength = width*height*3;
        /* PrintStream out = new PrintStream("D:\\amusic_and_videos\\meridian_hsv_raw_average_variants_1.txt");
         System.setOut(out);*/
         ArrayList<Double> hsv_average=new ArrayList<>();
         while(count<16197)
         {
            double average;

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

            int ind = 0;
            double sum_hsv=0;
            double sum_hsv_1=0;
            int[] bin_hsv=new int[360];
            int[] bin_rgb=new int[256];
            double[] probablity_rgb=new double[256];
            double[] probablity_rgb_1=new double[256];
            int sum_rgb=0;

            int[] bin_rgb_1=new int[256];
            int sum_rgb_1=0;

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
                   int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                   int grayScale=((r & 0xff)+(g&0xff)+(b&0xff))/3;
                  // System.out.println("pix "+((r & 0xff)+(g&0xff)+(b&0xff))/3);
                   int pix_1=0xff000000 | ((r_1 & 0xff) << 16) | ((g_1 & 0xff) << 8) | (b_1 & 0xff);
                   int grayScale_1=((r_1 & 0xff)+(g_1&0xff)+(b_1&0xff))/3;
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

                  bin_rgb[grayScale]++;
                  bin_rgb_1[grayScale_1]++;
                  sum_rgb=sum_rgb+grayScale;
                  sum_rgb_1=sum_rgb_1+grayScale_1;

                  ind++;



                  double[] hsv_return_from_rgb;
                  double[] hsv_return_from_rgb_1;
                  hsv_return_from_rgb=imageHSV_H(image[0][x][y],image[1][x][y],image[2][x][y]);
                  hsv_return_from_rgb_1=imageHSV_H(image_1[0][x][y],image_1[1][x][y],image_1[2][x][y]);
                  image_HSV[0][x][y]=hsv_return_from_rgb[0];
                  image_HSV[0][x][y]=image_HSV[0][x][y]<0?0:image_HSV[0][x][y];
                  image_HSV[0][x][y]=image_HSV[0][x][y]>359?359:image_HSV[0][x][y];
                  // System.out.println("dfafa"+image_HSV[0][x][y]);
                  image_HSV[1][x][y]=hsv_return_from_rgb[1];//s
                  image_HSV[2][x][y]=hsv_return_from_rgb[2];//v
                  sum_hsv=sum_hsv+image_HSV[0][x][y];
                  bin_hsv[(int)image_HSV[0][x][y]]++;
                  //System.out.println("sum_hsv "+sum_hsv);
                  // sum_hsv_1=sum_hsv_1+image_HSV[0][x][y];
                  image_HSV_1[0][x][y]=hsv_return_from_rgb_1[0];
                  image_HSV_1[1][x][y]=hsv_return_from_rgb_1[1];
                  image_HSV_1[2][x][y]=hsv_return_from_rgb_1[2];
                  //  System.out.println("fdasfsa"+image_HSV_1[0][x][y]);
                  sum_hsv_1=sum_hsv_1+image_HSV_1[0][x][y];


               }




            }
            average=sum_rgb/(320*180);
            double square=0;
            double average_hsv=sum_hsv/(320*180);
            double square_hsv=0;
            if(sum_rgb!=0)
            {
               for(int i=0;i<bin_rgb.length;i++)
               {

                  probablity_rgb[i]=((double) bin_rgb[i])/((double) sum_rgb);
                  square= square+Math.pow(bin_rgb[i]-average,2);
                  square_hsv=square_hsv+Math.pow(bin_hsv[i]-average_hsv,2);
                 //NaN 是从 Math.log(probablity_rgb[i] 出来的

                //  System.out.println(i+" "+bin_rgb[i]+"   sum_rgb "+sum_rgb);
                //  System.out.println("bin_rgb[i]/sum_rgb   "+bin_rgb[i]/sum_rgb);
                //  System.out.println("probablity_rgb["+i+"] "+" "+probablity_rgb[i]);
                //  System.out.println(i+" "+bin_rgb[i]);
                  //System.out.println("Math.log(2.0) "+Math.log(2.0));
                //  System.out.println(probablity_rgb[i]* (Math.log(probablity_rgb[i])/Math.log(2.0)));
                  if(probablity_rgb[i]!=0)
                  {
                     entropy_rgbForEachFrame[count]=entropy_rgbForEachFrame[count]+probablity_rgb[i]* (Math.log(probablity_rgb[i])/Math.log(2.0));
                  }


                  //probablity_rgb_1[i]=bin_rgb_1[i]/sum_rgb_1;
               }
            }
            else
            {
               probablity_rgb[0]=1;
               entropy_rgbForEachFrame[count]=0;
               square= 0;
            }
        //    System.out.println("entropy_rgbForEachFrame["+count+"] "+entropy_rgbForEachFrame[count]);
            variants[count]=square/256;
            variants_hsv[count]=square_hsv/360;
         //   System.out.println(count+"  "+variants[count]);
           // System.out.println(count+"  "+variants_hsv[count]);

            differenceBetweenEachHsv[count]=Math.abs(sum_hsv_1-sum_hsv);
          //  System.out.println(count+" "+differenceBetweenEachHsv[count]/(320*180));
            hsv_average.add(differenceBetweenEachHsv[count]/(320*180));
            //   System.out.println(count);
            count++;
         }//while end
         double sumTotalFrameHsv=0;
        for(int i=0;i<variants_hsv.length;i++)
        {
           sumTotalFrameHsv=sumTotalFrameHsv+variants_hsv[i];
        }

        double average_total_hsvForAllFrames=sumTotalFrameHsv/16200;
        System.out.println("average_total_hsvForAllFrames "+average_total_hsvForAllFrames);
        double variantsForAllHSV=0;
         for(int i=0;i<variants_hsv.length;i++)
         {
            //sumTotalFrameHsv=sumTotalFrameHsv+variants_hsv[i];
            variantsForAllHSV=variantsForAllHSV+Math.pow(variants_hsv[i]-average_total_hsvForAllFrames,2);

         }
         double real_variantsForAllHSV=variantsForAllHSV/16200;
         double std_hsvAllFrame=Math.sqrt(real_variantsForAllHSV);
         System.out.println("real_variantsForAllHSV "+real_variantsForAllHSV +" std_hsvAllFrame "+std_hsvAllFrame);

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
         raf_1.read(bytes_1);
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

               ind++;

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

   }

   public static void main(String[] args) {
      entropy_calculation_rgb ren = new entropy_calculation_rgb();
      ren.showIms(args);
   }

}

