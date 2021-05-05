from skimage.metrics import structural_similarity as ssim
import cv2
import sys

SEARCH_RANGE = 2000
SSIM_TH = 0.6
INPUT_FILE_NAME = 'meridian_boundaries.txt'
OUTPUT_FILE_NAME = 'merged_boundaries.txt'

if __name__ == '__main__':
    frames_path = sys.argv[1]
    f = open(INPUT_FILE_NAME, 'r')

    boundaries = []

    for x in f:
        boundaries.append(list(map(int, x.split(' '))))

    print(boundaries)

    merged_boundaries = []

    curr_frame_pointer = 0

    for i in range(len(boundaries) - 1):
        boundary = boundaries[i]

        merged_boundary = [boundary[0], boundary[1]]

        curr_frame = cv2.imread(frames_path + "frame" + str(boundary[1]) + ".jpg")

        should_merge = False

        for j in range(i + 1, len(boundaries)):
            next_boundary = boundaries[j]

            next_frame = cv2.imread(frames_path + "frame" + str(next_boundary[0]) + ".jpg")

            ssim_res = ssim(curr_frame, next_frame, multichannel=True)

            if next_boundary[0] - boundary[1] >= SEARCH_RANGE:
                break

            if ssim_res >= SSIM_TH:
                should_merge = True
                # print(boundary[1], next_boundary[1], ssim_res)
                merged_boundary[1] = next_boundary[1]

        is_merged = False

        if should_merge:
            for j in range(len(merged_boundaries)):
                prev_merged_boundary = merged_boundaries[j]

                lo = prev_merged_boundary[0]
                hi = prev_merged_boundary[1]

                if merged_boundary[0] >= lo and merged_boundary[0] < hi and merged_boundary[1] >= hi:
                    prev_merged_boundary[1] = merged_boundary[1]
                    is_merged = True

                if merged_boundary[0] >= lo and merged_boundary[0] < hi and merged_boundary[1] <= hi:
                    is_merged = True

            if not is_merged:
                merged_boundaries.append(merged_boundary)
        else:
            if len(merged_boundaries) > 0:
                last_merged_boundary = merged_boundaries[len(merged_boundaries) - 1]
                if last_merged_boundary[1] < merged_boundary[0]:
                    merged_boundaries.append(merged_boundary)
            else:
                merged_boundaries.append(merged_boundary)

    last_boundary = merged_boundaries[len(merged_boundaries) - 1][1]

    if last_boundary != 16199:
        merged_boundaries.append([last_boundary + 1, 16199])

    print(merged_boundaries)

    f = open(OUTPUT_FILE_NAME, 'w')

    for start, end in merged_boundaries:
        f.write(str(start) + ' ' + str(end) + '\n')

    f.close()
