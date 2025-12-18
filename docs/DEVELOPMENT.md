# PixelCloak Python Analysis Development Guide

This guide covers the small Python helper used for image entropy analysis (`scripts/analyze_image.py`). The script lives in the repository `scripts/` directory (not under `backend/`).

---

## Directory Structure (relevant)

```
scripts/
└── analyze_image.py      # Main entropy calculator used by Java via ProcessBuilder
```

---

## Installation

Recommended: create a virtual environment at the project root and install the minimal dependency.

```bash
python -m venv .venv
# activate (Windows PowerShell)
.venv\Scripts\Activate.ps1
# or (POSIX)
source .venv/bin/activate
pip install Pillow
```

Optionally add a `requirements.txt` with:

```
Pillow>=9.0.0
```

---

## Usage

Run the helper from the project root; the script prints a single pipe-delimited line to stdout:

```bash
python scripts/analyze_image.py path/to/image.png
```

Typical one-line outputs:

- `SAFE|<entropy>` (example: `SAFE|5.87`)
- `UNSAFE|<entropy>` (example: `UNSAFE|3.22`)
- `ERROR|...` (diagnostic when the script fails, e.g., missing library or bad path)

### Python API (local use)

```python
from scripts.analyze_image import calculate_entropy

score = calculate_entropy("path/to/image.png")
if score < 0:
    print("Error reading image")
elif score > 4.5:
    print("SAFE")
else:
    print("UNSAFE")
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

There are currently no Python unit tests included for the entropy helper in this repository.

If you want to add tests, create a `tests/` folder (project root) and use `pytest`. Example:

```bash
python -m venv .venv
.venv\Scripts\activate
pip install pytest Pillow
pytest -q
```

Example test skeleton (place under `tests/test_entropy.py`):

```python
from scripts.analyze_image import calculate_entropy

def test_solid_color(tmp_path):
    # create or copy a solid color fixture and assert entropy == 0
    assert calculate_entropy(str(tmp_path / "solid.png")) == 0
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

Notes & tips:
- When Java spawns the script via `ProcessBuilder`, ensure the `python` executable used has Pillow installed. The app's reference `ImageAnalyzer` currently uses an absolute path (`C:\\Python313\\python.exe`) which may need updating on your machine.
- Diagnostic output from the script uses a simple `ERROR|...` prefix; inspect the appended traceback text to see the underlying exception.

---

**Last Updated:** December 18, 2025
**Python Version:** 3.8+
