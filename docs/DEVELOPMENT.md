# PixelCloak Backend Development Guide

## Python Analysis Engine

This guide covers the Python component responsible for image entropy analysis.

---

## Directory Structure

```
backend/
├── src/
│   └── analyze_image.py      # Main entropy calculator
├── tests/
│   └── test_entropy.py       # Unit tests
├── requirements.txt          # Python dependencies
└── README.md                 # Documentation
```

---

## Installation

### 1. Create Virtual Environment

```bash
cd backend
python -m venv venv
```

### 2. Activate Virtual Environment

**Windows:**
```bash
venv\Scripts\activate
```

**macOS/Linux:**
```bash
source venv/bin/activate
```

### 3. Install Dependencies

```bash
pip install -r requirements.txt
```

**requirements.txt:**
```
Pillow>=9.0.0
numpy>=1.21.0
scipy>=1.7.0
pytest>=7.0.0
```

---

## Usage

### Command Line

```bash
python src/analyze_image.py path/to/image.png
```

**Output (Success):**
```json
{
  "status": "success",
  "entropy": 5.872,
  "safe": true,
  "message": "Image is safe for data hiding"
}
```

### Python API

```python
from scripts.analyze_image import calculate_entropy

entropy = calculate_entropy("path/to/image.png")
if entropy >= 5.0:
    print("SAFE for data hiding")
else:
    print("UNSAFE - choose a different image")
```

---

## How Entropy Calculation Works

### Shannon Entropy Formula

$$H(X) = -\sum_{i=0}^{255} p(i) \log_2 p(i)$$

Where:
- $p(i)$ = probability of pixel value $i$
- $\log_2$ = logarithm base 2

### Interpretation

| Range | Meaning | Example |
|-------|---------|---------|
| 0-2 | Almost no variation | Solid color |
| 2-4 | Low variation | Gradient |
| 4-6 | Medium variation | Textured surface |
| 6-8 | High variation | Complex scene |

### Implementation Steps

**Step 1: Load Image**
Open PNG file with PIL and convert to RGB colorspace. Flatten pixel array to 1D.

**Step 2: Calculate Histogram**
Count frequency of each pixel value (0-255) across all color channels.

**Step 3: Calculate Probability Distribution**
Normalize histogram by dividing by total pixel count to get probability of each value.

**Step 4: Calculate Shannon Entropy**
Apply formula: $H = -\sum p(i) \log_2(p(i))$ with epsilon guard to prevent log(0) errors.

**Python Implementation:** See `scripts/analyze_image.py`


## Testing

### Run Unit Tests

```bash
cd backend
pytest tests/test_entropy.py -v
```

### Test Coverage

```bash
pytest tests/test_entropy.py --cov=src
```

---

## Performance

### Benchmark Results

| Image Size | Time | Memory |
|------------|------|--------|
| 256×256 | ~50 ms | 2 MB |
| 1024×1024 | ~150 ms | 8 MB |
| 4096×4096 | ~500 ms | 32 MB |

**Target:** Analyze any image in < 2 seconds

---

## Troubleshooting

### Problem: "ModuleNotFoundError: No module named 'PIL'"

**Solution:**
```bash
pip install Pillow
```

### Problem: "entropy is nan"

**Cause:** All pixels have same value (solid color)

**Solution:**
```python
if np.isnan(entropy):
    entropy = 0.0
```

### Problem: Python not found when called from Java

**Check 1:** Verify Python is in system PATH: `python --version`

**Check 2:** Use absolute path to Python executable in Java's ProcessBuilder instead of relative path or "python" command

**Check 3:** Ensure image path passed to Python script is absolute, not relative

---

**Last Updated:** December 2024
**Python Version:** 3.8+
