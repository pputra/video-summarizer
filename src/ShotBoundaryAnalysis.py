from skimage.metrics import structural_similarity as ssim
import cv2

import sys

NUM_TOTAL_FRAMES = 16200

def compare_frames(curr_frame, next_frame):
    s = ssim(curr_frame, next_frame, multichannel=True)
    return s

if __name__ == '__main__':
    frames_path = sys.argv[1]

    start_frame = 0

    for i in range(NUM_TOTAL_FRAMES - 1):
        curr_frame = cv2.imread(frames_path + "frame" + str(i) + ".jpg")
        next_frame = cv2.imread(frames_path + "frame" + str(i + 1) + ".jpg")

        s = compare_frames(curr_frame, next_frame)

        shot_len = i - start_frame

        concert_thresholds = s < 0.2 and shot_len > 5
        meridian_thresholds = s < 0.3 and shot_len > 5
        soccer_thresholds = s < 0.4 and shot_len > 5

        is_last_frame = i == NUM_TOTAL_FRAMES - 2

        if meridian_thresholds or is_last_frame:
            if is_last_frame:
                i = i + 1

            print(str(start_frame) + " " + str(i))

            start_frame = i + 1
