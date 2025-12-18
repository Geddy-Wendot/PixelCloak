# PixelCloak — Steganographic Journal (local desktop)

PixelCloak lets you encrypt and hide short journal entries inside PNG images using authenticated AES-256 encryption and LSB steganography. The app is local-first: UI and crypto run in Java; a small Python helper is used for image entropy analysis.

## Repository layout (high level)

```
PixelCloak/
├── frontend/      # Java Swing UI (entry: com.pixelcloak.app.App)
├── backend/       # Core Java library (AESCrypto, Steganography, ImageAnalyzer)
├── scripts/       # Python helper: analyze_image.py
└── docs/          # documentation
```

## Features

- AES-256-GCM encryption with PBKDF2 key derivation (600,000 iterations).
- LSB steganography across RGB channels; payloads stored with a 4-byte length prefix.
- Python-based entropy check (`scripts/analyze_image.py`) to help avoid embedding into low-entropy images.
- Duress/decoy behavior in the UI (configurable in the frontend).

## Prerequisites

- Java 17+ (JDK)
- Maven 3.6+
- Python 3.8+ (for the analyzer)
- `pip` to install Python dependencies (Pillow)

## Quick start (development)

1. Install Python dependency used by the analyzer:

```bash
pip install Pillow
```

2. Build the Java modules (from repository root):

```bash
cd backend
mvn clean install
cd ../frontend
mvn clean package
```

3. Run the frontend (from `frontend`):

```bash
mvn exec:java -Dexec.mainClass="com.pixelcloak.app.App"
```

Notes:
- The frontend will call the Python helper for entropy analysis. The script is `scripts/analyze_image.py` and prints a single pipe-delimited result line such as `SAFE|5.87` or `UNSAFE|3.22`.
- The Java `ImageAnalyzer` implementation currently references an absolute `pythonPath` (example: `C:\\Python313\\python.exe`). If the analyzer fails to run, either ensure that path exists or update `backend/src/main/java/com/pixelcloak/core/ImageAnalyzer.java` to point to a valid Python executable (or to use the `python` on PATH).

## Running from a packaged jar

If you package the frontend into a jar, run the main class `com.pixelcloak.app.App`. Using `mvn exec:java` is the simplest development method.

## Documentation

See the module READMEs for more details:
- Frontend: `frontend/README.md`
- Backend: `backend/README.md`
- Development notes and API: `docs/`

---

**Last Updated:** December 18, 2025