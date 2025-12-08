# Backend - Python Analysis Engine

## Overview

The backend is a Python-based analysis engine responsible for calculating Shannon Entropy to determine if an image is suitable for steganographic data hiding. It communicates with the Java frontend via subprocess calls.

## Structure

```
backend/
├── src/
│   └── analyze_image.py           # Main entropy calculator
├── tests/
│   └── test_entropy.py            # Unit tests
├── requirements.txt               # Python dependencies
└── README.md                      # Documentation
```

---

## Quick Start

### Prerequisites
- Python 3.8+
- pip (Python package manager)

### Installation

**1. Create Virtual Environment:**
```bash
cd backend
python -m venv venv
```

**2. Activate Virtual Environment:**

**Windows:**
```bash
venv\Scripts\activate
```

**macOS/Linux:**
```bash
source venv/bin/activate
```

**3. Install Dependencies:**
```bash
pip install -r requirements.txt
```

### Run Analysis

**Command Line:**
```bash
python src/analyze_image.py --image path/to/image.png
```

**Output:**
```json
{
  "status": "success",
  "entropy": 5.872,
  "safe": true,
  "message": "Image is safe for data hiding"
}
```

---

## Module: analyze_image.py

### Function: `calculate_entropy()`

This function computes the Shannon entropy of an image, a measure of its complexity and randomness. High-entropy images are better suited for steganography.

```python
from PIL import Image
import numpy as np

def calculate_entropy(image_path: str) -> float:
    """Calculate the Shannon entropy of an image.

    The image is converted to 8-bit grayscale to analyze its luminance
    complexity. An entropy of 0.0 indicates a solid color, while a value
    approaching 8.0 suggests high randomness.

    Args:
        image_path: Path to the image file.

    Returns:
        The entropy value as a float between 0.0 and 8.0.
    """
    # Convert image to 8-bit grayscale
    img = Image.open(image_path).convert('L')
    pixels = np.array(img).flatten()
    
    # Calculate probability distribution of pixel values
    hist, _ = np.histogram(pixels, bins=256, range=(0, 256))
    prob = hist / hist.sum()
    
    # Filter out zero probabilities to prevent log(0) errors
    prob = prob[prob > 0]
    
    # Calculate Shannon entropy
    entropy = -np.sum(prob * np.log2(prob))
    return float(entropy)
```

---

## Dependencies

### requirements.txt
```
Pillow>=9.0.0
numpy>=1.21.0
scipy>=1.7.0
pytest>=7.0.0
```

### Why Each Dependency?

| Package | Purpose |
|---------|---------|
| **Pillow** | Image loading and manipulation |
| **NumPy** | Numerical computation |
| **SciPy** | Scientific computing |
| **pytest** | Unit testing framework |

---

## Testing

### Run Tests

```bash
pytest tests/test_entropy.py -v
```

### Test Coverage

```bash
pytest tests/test_entropy.py --cov=src
```

---

## Performance

### Benchmark Results

| Image Size | Processing Time | Memory |
|------------|-----------------|--------|
| 256×256 | ~50 ms | 2 MB |
| 1024×1024 | ~150 ms | 8 MB |
| 2048×2048 | ~300 ms | 16 MB |
| 4096×4096 | ~500 ms | 32 MB |

**Target:** All images analyzed in < 2 seconds

---

## Integration with Java

### Java ProcessBuilder Call

```java
ProcessBuilder pb = new ProcessBuilder(
    "python",
    "backend/src/analyze_image.py",
    "--image", imageFile.getAbsolutePath(),
    "--format", "json"
);

Process process = pb.start();
BufferedReader reader = new BufferedReader(
    new InputStreamReader(process.getInputStream())
);
String jsonResponse = reader.readLine();
```

---

## Troubleshooting

### Issue: "ModuleNotFoundError: No module named 'PIL'"

**Solution:**
```bash
pip install Pillow
```

### Issue: "entropy is nan"

**Cause:** Image is completely solid color

**Fix:**
```python
if np.isnan(entropy):
    entropy = 0.0
```

### Issue: "Python not found when called from Java"

**Solutions:**
1. Add Python to system PATH
2. Use absolute path: `C:\\Python39\\python.exe`

---

**Last Updated:** December 2024
**Python Version:** 3.8+
**Status:** In Development
