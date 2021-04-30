package summarizer.calculations.motion;

import java.io.*;
import java.util.ArrayList;

public class MotionVector{
   int width = 320;
   int height = 180;
   int[][] oriImagePixel=new int[320][180];
   int[][] oriImagePixel_next=new int[320][180];
   int blockWidth=15;
   int blockHeight=16;
   int blockSize=16*15;
   int blockNum=320*180/(16*15);// col 12 row 20
   int k=8;
   int blockMotionNum=(2*k+1)^2;
   int rowNum=320/16;
   int colNum=180/15;//12
   int count1=0;
   ArrayList<Integer> shot_arr=new ArrayList<>();

   private final String RGB_PATH;

   public MotionVector(String rgbPath) {
      RGB_PATH = rgbPath;
   }

   public void calculate() {
//      int[] motionVectorForOneImage=new int[16198];
      FileWriter writer;
      try {
         String MOTION_VECTOR_RESULT_FILENAME = "motion_vectors.txt";
         writer = new FileWriter(MOTION_VECTOR_RESULT_FILENAME);

         int count=0;
         int frameLength = width*height*3;
         while(count<=16198) {
            File file = new File(RGB_PATH+"frame"+count+".rgb");
            File file_1 = new File(RGB_PATH+"frame"+(count+1)+".rgb");
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            RandomAccessFile raf_1 = new RandomAccessFile(file_1, "r");
            raf.seek(0);
            raf_1.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];
            byte[] bytes_1 = new byte[(int) len];
           // byte[] bytes = new byte[(int) len];

            raf.read(bytes);
            raf_1.read(bytes_1);

            int ind = 0;
            for(int y = 0; y < height; y++) {
               for(int x = 0; x < width; x++) {
                  byte a = 0;
                  byte r = bytes[ind];
                  byte g = bytes[ind+height*width];
                  byte b = bytes[ind+height*width*2];


                  byte r_1 = bytes_1[ind];
                  byte g_1 = bytes_1[ind+height*width];
                  byte b_1 = bytes_1[ind+height*width*2];

                  int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                  int pix_1 = 0xff000000 | ((r_1 & 0xff) << 16) | ((g_1 & 0xff) << 8) | (b_1 & 0xff);
                  //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                  oriImagePixel[x][y]=pix;
                  oriImagePixel_next[x][y]=pix_1;
                  ind++;
               }
               raf.close();
               raf_1.close();
            }

            int motionVectorForOneImage =calculateMotionVector(oriImagePixel,oriImagePixel_next);
            System.out.println("count "+count+" motionVectorForOneImage["+count+"] "+motionVectorForOneImage);
            writer.write(motionVectorForOneImage + "\n");
            count++;
         }//while end

         writer.close();

         //caluculate the average motion vector for each shot
         double[] shotMotion=new double[shot_arr.size()/2];
         for(int i=0;i<shotMotion.length;i=i+2) {
            int sum=0;
            int difference=shot_arr.get(i+1)-shot_arr.get(i);
            for(int j=shot_arr.get(i);j<difference;j++) {
               sum=sum+shot_arr.get(j);
            }

            shotMotion[i]=sum/difference;
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public int calculateMotionVector(int[][] frame_n,int[][] frame_nPlusOne)
   {
      //count1++;
     // System.out.println("count1 "+count1);
      int blockCount=0;
      int x_original=0;
      int y_original=0;
      int[] leftCorner;
      int[] cicular;
      int[] target=new int[2];
      //Pair<Integer,Integer> pair;
      int x_shift=0;
      int y_shift=0;
      int totalMotion=0;
      int min;
      int[][] initialBlock=new int[16][15];
      int[][] compareBlock=new int[16][15];
      int absoluteDifferenceForOneImage=0;
      while(blockCount<blockNum)
      {
         min=0;
         leftCorner=new int[]{x_original+x_shift*blockHeight,y_original+y_shift*blockWidth};
        // System.out.println("leftCorner[0] "+leftCorner[0]+"leftCorner[1] "+leftCorner[1]);
         if(y_original+y_shift*blockWidth==165)
         {
            x_shift++;
            y_shift=0;
         }
         else
         {
            y_shift++;
         }
         blockCount++;
         initialBlock=blockGet(leftCorner,frame_n);
         int new_value;
         compareBlock=blockGet(leftCorner,frame_nPlusOne);
         new_value=compareTwoBlocks(initialBlock,compareBlock);
         target=new int[]{leftCorner[0],leftCorner[1]};
         for(int i=-k;i<=k;i++)
         {
            for(int j=-k;j<k;j++)
            {

                // define the original one

              /* new_value=0;
               min=new_value;
               target=new int[]{leftCorner[0],leftCorner[1]};*/
               int new_x=leftCorner[0]+i;
               int new_y=leftCorner[1]+j;

               // 判定 new pair 的元素是否 出界 有四个
               if(!(new_x<0||new_x>=305||new_y<0||new_y>=166))
               {
                 // pair=new Pair<>(leftCorner[0]+i,leftCorner[y]+j);
                  cicular=new int[]{new_x,new_y};
                  compareBlock=blockGet(cicular,frame_nPlusOne);
                  new_value=compareTwoBlocks(initialBlock,compareBlock);
                //  System.out.println("new_value "+new_value);
                  /*if(i==-k&&j==-k)
                  {
                     min=new_value;
                     target=new int[]{cicular[0],cicular[1]};

                  }
                  else
                  {*/
                     if(new_value<min)
                     {
                        min=new_value;
                        target=new int[]{cicular[0],cicular[1]};
                     }

                 /* }*/

               }

            }
         }
       //  System.out.println(Math.sqrt(Math.pow((leftCorner[0]-target[0]),2)+Math.pow(leftCorner[1]-target[1],2)));
         absoluteDifferenceForOneImage= (int) (absoluteDifferenceForOneImage+Math.sqrt(Math.pow(leftCorner[0]-target[0],2)+Math.pow(leftCorner[1]-target[1],2)));
        // System.out.println("absoluteDifferenceForOneImage "+absoluteDifferenceForOneImage);
      }
//      System.out.println("absoluteDifferenceForOneImage "+absoluteDifferenceForOneImage);
      return absoluteDifferenceForOneImage;
   }

   public int[][] blockGet(int[] leftCorner,int[][] image)
   {
      int xCoordinatesOfLeftCorner=leftCorner[0];
   //   System.out.println("xCoordinatesOfLeftCorner"+ xCoordinatesOfLeftCorner);

      int yCoordinatesOfLeftCorner=leftCorner[1];
   //   System.out.println("yCoordinatesOfLeftCorner"+ yCoordinatesOfLeftCorner);
      int[][] arr=new int[16][15];
      for(int x=0;x<16;x++)
      {
         for(int y=0;y<15;y++)
         {
            arr[x][y]=image[xCoordinatesOfLeftCorner+x][yCoordinatesOfLeftCorner+y];
         }
      }

      return arr;
   }

   public int compareTwoBlocks(int[][] originalBlock,int[][] compareBlock)
   {
      int value=0;
      for(int x=0;x<16;x++)
      {
         for(int y=0;y<15;y++)
         {
            value=value+Math.abs(originalBlock[x][y]-compareBlock[x][y]);
         }
      }

      return value;
   }
   public void showIms(String[] args) throws FileNotFoundException {

      // Read a parameter from command line
//      String param1 = args[1];
//      shotBoundary=new File(args[1]);
//      Scanner myReader=new Scanner(shotBoundary);
//      System.out.println("The second parameter was: " + param1);
//      String delimeter=" ";
//      String[] temp;
//      int count=0;
//      while(myReader.hasNextLine())
//      {
//         String data = myReader.nextLine();
//         temp = data.split(delimeter);
//         shot_arr.add(Integer.parseInt(temp[0]));
//         System.out.println(shot_arr.get(count));
//         count++;
//         shot_arr.add(Integer.parseInt(temp[1]));
//         System.out.println(shot_arr.get(count));
//         count++;
//
//         // small=Float.parseFloat(temp[0]);
//        // small=Integer.parseInt(temp[0]);
//         //large=Integer.parseInt(temp[1]);
//      }

      // Read in the specified image
   }

   public static void main(String[] args) {
      String pathToRgb = args[0];
      MotionVector motionVector = new MotionVector(pathToRgb);
      motionVector.calculate();
   }

}


/*

 if(count==0)
         {
         leftCorner= new int[]{x, y};
         colCount++;
         }
         else
         {
         if(colCount<colNum-1)
        {
        colCount++;

        }
        else
        {
        colCount=0;
        rowCount++;

        }

        leftCorner= new int[]{x+blockHeight*rowCount, y + blockWidth*colCount};
        System.out.println("leftcorner[0] "+leftCorner[0]+"leftcorner[1]"+" " +leftCorner[1]);
        }*/
