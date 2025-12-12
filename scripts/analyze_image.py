import sys
import math
import os
import traceback # Added for debugging

try:
    from PIL import Image
except ImportError:
    print("ERROR|Missing Pillow library. Run 'pip install pillow'")
    sys.exit(1)

def calculate_entropy(image_path):
    try:
        if not os.path.exists(image_path):
            return -1 # File not found

        img = Image.open(image_path)
        img = img.convert('L') # Grayscale for analysis

        histogram = img.histogram()
        image_size = img.size[0] * img.size[1]

        entropy = 0
        for count in histogram:
            if count > 0:
                p = count / image_size
                entropy -= p * math.log2(p)

        return entropy
    except Exception:
        # PRINT THE ACTUAL ERROR to the console so Java can read it
        print("ERROR|Python Exception: " + traceback.format_exc().replace('\n', ' '))
        return 0

if __name__ == "__main__":
    if len(sys.argv) > 1:
        img_path = sys.argv[1]
        score = calculate_entropy(img_path)

        # If score is 0, it likely failed inside calculate_entropy
        if score == 0:
            # We already printed the error above if it was an exception
            pass
        elif score > 4.5:
            print(f"SAFE|{score:.2f}")
        else:
            print(f"UNSAFE|{score:.2f}")
    else:
        print("ERROR|No path provided")