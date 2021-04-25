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

        # print(str(i) + " " + str(s))

        shot_len = i - start_frame

        if s < 0.25 and shot_len > 5:
            print(str(start_frame) + " " + str(i))
            start_frame = i + 1
