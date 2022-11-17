# for every image in the folder assets\images2
# if the name file has the word "Agent" in it, add a red border of 1px

from PIL import Image, ImageDraw, ImageFont
import random
import cv2
import os

path = "assets/images2"

# col1, col2, col3, col4 are shades darkgreen, darkgrey, black, and darkbrown
col1 = (0, 75, 0)
col2 = (75, 75, 75)
col3 = (0, 0, 0)
col4 = (0, 37, 75)

for f in os.listdir(path):
    img = cv2.imread(os.path.join(path, f))
    if "AGENT" in f:
        cv2.rectangle(
            img, (0, 0), (img.shape[0]-1, img.shape[1]-1), (0, 0, 255), 3)
    else:
        # add a mossy pattern on the border, to make it look like a mossy stone wall, or something
        # do it with a for loop, and a random number generator to decide if the pixel is mossy or not
        # you can do it by layers
        for j in range(5):

            # left border
            for i in range(0, img.shape[0]):
                num = random.randint(1, 6)
                if num == 1:
                    img[i, j] = col1
                elif num == 2:
                    img[i, j] = col2
                elif num == 3:
                    img[i, j] = col3
                else:
                    img[i, j] = col4

            # top border
            for i in range(0, img.shape[0]):
                num = random.randint(1, 6)
                if num == 1:
                    img[j, i] = col1
                elif num == 2:
                    img[j, i] = col2
                elif num == 3:
                    img[j, i] = col3
                else:
                    img[j, i] = col4

            for i in range(0, img.shape[0]):
                num = random.randint(1, 6)
                if num == 1:
                    img[i, img.shape[1]-1-j] = col1
                elif num == 2:
                    img[i, img.shape[1]-1-j] = col2
                elif num == 3:
                    img[i, img.shape[1]-1-j] = col3
                else:
                    img[i, img.shape[1]-1-j] = col4

            for i in range(0, img.shape[0]):
                num = random.randint(1, 6)
                if num == 1:
                    img[img.shape[0]-1-j, i] = col1
                elif num == 2:
                    img[img.shape[0]-1-j, i] = col2
                elif num == 3:
                    img[img.shape[0]-1-j, i] = col3
                else:
                    img[img.shape[0]-1-j, i] = col4

    cv2.imwrite(os.path.join(path, f), img)
