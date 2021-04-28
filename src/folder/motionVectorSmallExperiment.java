package folder;

public class motionVectorSmallExperiment {



   public void callFunc()
   {
      int[][] arr=new int[320][180];
      int[][] arr_1=new int[320][180];
      int blockWidth=15;
      int blockHeight=16;
      int blockNum=(320*180)/(16*15);
      int[] leftCorner;
      System.out.println("blockNum"+blockNum);
      int countBlock=0;
      int[][] originalBlock=new int[16][15];
      int[][] compareBlock=new int[16][15];
      int x_orginal=0;
      int x_shift=0;
      int y_original=0;
      int y_shift=0;
      int k=8;
      for(int i=0;i<320;i++)
      {
         for(int j=0;j<180;j++)
         {
            arr[i][j]=i+j;
            arr_1[i][j]=i+j+1;
         }
      }
      int totalMotion=0;
      while(countBlock<blockNum)
      {
         leftCorner=new int[]{x_orginal+x_shift*blockHeight,y_original+y_shift*blockWidth};
         System.out.println("leftCorner[0] "+leftCorner[0]+"leftCorner[1] "+leftCorner[1]);
         if(y_original+y_shift*blockWidth==165)
         {
            x_shift++;
            y_shift=0;
         }
         else
         {
            y_shift++;
         }

         countBlock++;

         originalBlock=getBlock(leftCorner,arr);
         int[] leftCornerForNPlusOne=new int[2];
         int min=0;
         int[] theSmallestBlock=new int[2];
         for(int i=-k;i<=k;i++)
         {
            for(int j=-k;j<=k;j++)
            {
               int new_x=leftCorner[0]+i;//_leftCornerForNextFrame
               int new_y=leftCorner[1]+j;//_leftCornerForNextFrame

               if(!(new_x<0||new_x>=305||new_y<0||new_y>=166))
               {

                  leftCornerForNPlusOne[0]=new_x;
                  leftCornerForNPlusOne[1]=new_y;
                  compareBlock=getBlock(leftCornerForNPlusOne,arr_1);
                  int absoluteValue=returnDiffOfTwoBlocks(originalBlock,compareBlock);
                  if(j==-k&&i==-k)
                  {
                     min=absoluteValue;
                     theSmallestBlock[0]=new_x;
                     theSmallestBlock[1]=new_y;
                  }
                  else
                  {
                     if(absoluteValue<min)
                     {
                        min=absoluteValue;
                        theSmallestBlock[0]=new_x;
                        theSmallestBlock[1]=new_y;

                     }
                  }


               }
            }
         }
         totalMotion=  (totalMotion+(int)Math.abs(Math.sqrt((leftCorner[0]-theSmallestBlock[0])^2+(leftCorner[1]-theSmallestBlock[1])^2)));
         System.out.println("totalMotion: "+totalMotion);



      }

      System.out.println("totalMotion "+totalMotion);
   }
   public static void main(String[] args)
   {

      motionVectorSmallExperiment moti=new motionVectorSmallExperiment();
      moti.callFunc();

   }

   public int[][] getBlock(int[] leftCorner, int[][] array)
   {
      int[][] return_arr=new int[16][15];
      int x_beginCoordinates=leftCorner[0];
      int y_beginCoordinates=leftCorner[1];
      for(int row=0;row<16;row++)
      {
         for(int col=0;col<15;col++)
         {
            return_arr[row][col]=array[x_beginCoordinates+row][y_beginCoordinates+col];
         }
      }



      return return_arr;
   }

   public int returnDiffOfTwoBlocks(int[][] frameN,int[][] frameNPlus1)
   {
      int diff=0;

      for(int row=0;row<16;row++)
      {
         for(int col=0;col<15;col++)
         {
            diff+=Math.abs(frameN[row][col]-frameNPlus1[row][col]);
         }
      }
      return diff;
   }
}
