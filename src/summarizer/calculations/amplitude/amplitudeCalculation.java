import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class amplitudeCalculation {


   public static void main(String[] args)
   {
     /* int count=0;

      File file=new File("D:\\amusic_and_videos\\academic performance of USC\\576\\concert_shot_boundaries.txt");*/

      ArrayList<Integer> diff=new ArrayList<>();
      ArrayList<Integer> samples=new ArrayList<>();
      String[] temp;
      String delimeter = " ";
      int small;
      int large;
      int dif;
      float sample;
      float frame= (float) (1.0/30.0);
      System.out.println(frame);
      int lineCal=0;
      //temp = str.split(delimeter);
      try {
         File myObj = new File("D:\\amusic_and_videos\\academic performance of USC\\576\\meridian_shot_boundaries.txt");
         File myObj2 = new File("D:\\amusic_and_videos\\academic performance of USC\\576\\meridian.txt");
         Scanner myReader = new Scanner(myObj);
         Scanner myReader2 = new Scanner(myObj2);

         while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            temp = data.split(delimeter);
           // small=Float.parseFloat(temp[0]);
            small=Integer.parseInt(temp[0]);
            large=Integer.parseInt(temp[1]);
            dif=large-small;
            diff.add(dif);

            sample= (float) (dif*frame*48000);
            lineCal= (int) (lineCal+sample);
            System.out.println("sample "+(int)sample);
            samples.add((int) sample);
           // System.out.println(data);
         //   System.out.println("sample "+sample);
           // System.out.println(dif);

          //  System.out.println("temp "+" "+temp[1]);

            //+temp[0]
         }
         System.out.println("linecal "+lineCal);
         myReader.close();
         float left;
         float right;
         int count=0;
         ArrayList<Float> amplitudeForEachShot=new ArrayList<>();
         //ArrayList<Integer> numOfFramesOfEachShots=new ArrayList<>();
         ArrayList<Float> averageValueForEachShot=new ArrayList<>();
         float amplitude=0;
         int nextLineCount=samples.get(count);
         int totalLineCount=0;
         System.out.println(samples.size());
         while (myReader2.hasNextLine()&&nextLineCount!=0) {
            totalLineCount++;
            String ampli = myReader2.nextLine();
            temp = ampli.split(delimeter);
            // small=Float.parseFloat(temp[0]);
            left= Math.abs(Float.parseFloat(temp[0]));
            right=Math.abs(Float.parseFloat(temp[1]));
            amplitude=amplitude+(left+right)/2;
         //   dif=large-small;

          //  sample= (int) (dif*0.0333333*48000);

          //  System.out.println(data);
          //  System.out.println("sample "+sample);
            // System.out.println(dif);

            //  System.out.println("temp "+" "+temp[1]);

            //+temp[0]
            nextLineCount--;
            if(nextLineCount==0)
            {
               amplitudeForEachShot.add(amplitude);
               averageValueForEachShot.add(amplitude/diff.get(count));
               count++;
               System.out.println("count "+count);
               if(count>=samples.size())
               {
                  System.out.println(totalLineCount);
                  System.exit(100);
               }

               nextLineCount=samples.get(count);
             //  System.out.println(nextLineCount);
               while(nextLineCount==0)
               {
                  count++;
                  nextLineCount=samples.get(count);
               }
               amplitude=0;
            }

         }
         myReader2.close();



      } catch (FileNotFoundException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
      }
      //read the value first, get the difference




   }
}
