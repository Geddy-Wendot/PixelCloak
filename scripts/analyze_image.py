import sys
import math
import os
import traceback # Import this to see error details

try:
    from PIL import Image
except ImportError:
    # If this prints, it means Java is using a Python that doesn't have Pillow installed
    print("ERROR|MISSING_LIBRARY: Pillow is not installed. Run 'pip install pillow'")
    sys.exit(1)

def calculate_entropy(image_path):
    try:
        if not os.path.exists(image_path):
            return -1 

        img = Image.open(image_path)
        img = img.convert('L') # Convert to Grayscale
        
        histogram = img.histogram()
        image_size = img.size[0] * img.size[1]
        
        entropy = 0
        for count in histogram:
            if count > 0:
                p = count / image_size
                entropy -= p * math.log2(p)
                
        return entropy
    except Exception:
        # STOP returning 0. Print the actual error!
        print("ERROR|PYTHON_CRASH: " + traceback.format_exc().replace('\n', ' '))
        return 0

if __name__ == "__main__":
    if len(sys.argv) > 1:
        img_path = sys.argv[1]
        score = calculate_entropy(img_path)
        
        # If score is 0, we likely already printed an ERROR above
        if score > 0:
            # Threshold check
            if score > 4.5:
                print(f"SAFE|{score:.2f}")
            else:
                print(f"UNSAFE|{score:.2f}")
    else:
        print("ERROR|No path provided")