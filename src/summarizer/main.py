# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


# def print_hi(name):
#     # Use a breakpoint in the code line below to debug your script.
#     print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.
#
#
# # Press the green button in the gutter to run the script.
# if __name__ == '__main__':
#     print_hi('PyCharm')
#
# # See PyCharm help at https://www.jetbrains.com/help/pycharm/

from skimage.metrics import structural_similarity as ssim
import cv2
import numpy as np
import math

import sys

NUM_TOTAL_FRAMES = 16200


def compare_frames(curr_frame, next_frame):
    s = ssim(curr_frame, next_frame, multichannel=True)
    return s


if __name__ == '__main__':
    frames_path = sys.argv[1]
    print(frames_path)

    ######new#########
   #  start_frame = 0
   #  flag=0;
   #  list=[]
   #  sum=0
   #  file1 = open("D:\\amusic_and_videos\\test_data_final_soccer\\test_data\\soccer_start_ssim_raw_data.txt", "w")
   # # file2 = open("D:\\amusic_and_videos\\soccer_2_proData2_ssim_raw_AfterMerge.txt", "w")
   #  for i in range(NUM_TOTAL_FRAMES - 1):
   #      curr_frame = cv2.imread(frames_path + "\\frame" + str(i) + ".jpg")
   #      next_frame = cv2.imread(frames_path + "\\frame" + str(i + 1) + ".jpg")
   #
   #
   #      s = compare_frames(curr_frame, next_frame)
   #      # if(flag==0):
   #      #     list = [s]
   #      #     flag=1
   #      # else:
   #      list.append(s)
   #      sum=sum+s
   #      print(i, s)
   #      # 将所有boundary 存入数组中间
   #      # 重新验证boundary
   #      file1.write(str(i)+" "+ str(s)+"\n")
   #      shot_len = i - start_frame
   #  ###new#########################
   #  for j in range(len(list)):
   #      #print("///")
   #      # print(j,list.index(j))
   #      print(j,list[j])
    ##############################
    ################下面是处理已经读好boundary的程序##########
    sum=0
    listOfNum=[]
    list=[]
    # for k in range(len(list)):
    #     sum=sum+list[k]

    with open("D:\\amusic_and_videos\\test_data_final_soccer\\test_data\\soccer_start_ssim_raw_data.txt") as f:
        # for line in f.readlines():
        #     line = line.strip('\n')  # 去掉列表中每一个元素的换行符
        #     line.split(" ")
        #     print(line)
        while True:
            line = f.readline()
            line = line.strip('\n')# 逐行读取
            if not line:
                break
            line=line.split(" ")
            print("dafasf")
            print(line[1])  ## 这里加了 ',' 是为了避免 print 自动换行
            sum=sum+float(line[1])
            list.append(line[1])
            listOfNum.append(float(line[1]))
################
    average = sum / len(list)
    variants = 0
    for h in range(len(list)):
        variants = variants + pow((float(list[h]) - average), 2)

    variants = variants / len(list)
    std = math.sqrt(variants)
    print(average, variants, std)
    threshold = 0
    bound = []
    flag=0
    if (std <= 0.07):
        threshold = 3 * std
    elif (std > 0.07 and std <= 0.12):
        threshold = 2 * std
    elif (std > 0.12 and std <= 0.15):
        threshold = 1.7 * std
    elif (std > 0.15 and std < 0.2):
        threshold = 1.5 * std
    else:
        threshold = 1.2 * std
    index=0
    for count in range(len(list) - 1):
        curr_frame_1 = cv2.imread(frames_path + "\\frame" + str(count) + ".jpg")
        # next_frame_1 = cv2.imread(frames_path + "\\frame" + str(count + 1) + ".jpg")
        if (float(list[count]) < float(list[count - 1]) and float(list[count]) < float(list[count + 1])):
            if (abs(float(list[count]) - float(list[count + 1])) > threshold and abs(float(list[count]) - float(list[count - 1])) > threshold):
                bound.append(count)
               #  print(bound[index])
               #  index = index + 1
                new_count = count+1
               # for new_count in range(len(list) - 1):
                while(new_count<len(list) - 1):
                    next_frame_1 = cv2.imread(frames_path + "\\frame" + str(new_count + 1) + ".jpg")
                    new_s = compare_frames(curr_frame_1, next_frame_1)
                    print(count,new_count,new_s)
                    if (new_s >= average ):
                        print(new_s >= average)
                        bound.remove(count)
                        break
                    new_count=new_count+1
                    if(new_count-count>900):
                        break
                if(len(bound)>=2and(bound[len(bound)-1]-bound[len(bound)-2])<=30):
                    bound.pop()
                    flag=1

                # for ind in range(len(bound)):
                #     print(bound[ind])

  ##如果这一个和上一个也仅仅相差30(1秒钟)那么选择merge(if the last one and the second last one's difference less than 30, then merge them)


    print("finish")
    for new_new_count in range(len(bound)):
        print(bound[new_new_count])

        # concert_thresholds = s < 0.2 and shot_len > 5
        # meridian_thresholds = s < 0.3 and shot_len > 5
        # soccer_thresholds = s < 0.4 and shot_len > 5
        #
        # is_last_frame = i == NUM_TOTAL_FRAMES - 2
        #
        # if meridian_thresholds or is_last_frame:
        #     if is_last_frame:
        #         i = i + 1
        #
        #     print(str(start_frame) + " " + str(i))
        #
        #     start_frame = i + 1
