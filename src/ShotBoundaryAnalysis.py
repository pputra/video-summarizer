from skimage.metrics import structural_similarity as ssim
import numpy as np
import cv2

def mse(imageA, imageB):
    err= np.sum((imageA.astype("float") - imageB.astype("float")) ** 2)
    err /= float(imageA.shape[0] * imageA.shape[1])
    return err


def compare_image(imageA, imageB):
    m = mse(imageA, imageB)

    s = ssim(imageA, imageB, multichannel=True)
    print(m)
    print(s)

frame1 = cv2.imread("project_dataset/frames/concert/frame2867.jpg")
frame2 = cv2.imread("project_dataset/frames/concert/frame2869.jpg")

compare_image(frame1, frame2)
