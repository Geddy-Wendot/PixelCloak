# PixelCloak API Specification

## Java-Python Interface Contract

This document defines the communication protocol between the Java frontend and Python analysis backend.

---

## 1. Image Entropy Analysis

### 1.1 Java → Python Request

**Invocation:**
```java
ProcessBuilder pb = new ProcessBuilder(
    pythonPath, 
    "scripts/analyze_image.py", 
    imagePath
);
pb.redirectErrorStream(true);
Process process = pb.start();
```

**Arguments:**
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| pythonPath | string | Yes | Path to the Python executable |
| script_path | string | Yes | Path to `analyze_image.py` |
| image_path | string | Yes | Absolute path to image file |

### 1.2 Python → Java Response

**Format:** Text (Pipe-delimited, e.g., "SAFE|7.12")

**Success Response:**
```text
SAFE|5.87
```

**Failure Response:**
```json
{
  "status": "error",
  "entropy": 0.0,
  "safe": false,
  "message": "Image file not found or corrupted"
}
```markdown
# PixelCloak API Specification

## Java ↔ Python Interface Contract

This document describes the runtime contracts between the Java frontend, the core encryption/steganography library, and the Python image-analysis helper script.

---

## 1. Image Entropy Analysis

### 1.1 Java → Python Invocation

Invocation (example):
```java
ProcessBuilder pb = new ProcessBuilder(
    pythonPath, // typically: C:\\Python313\\python.exe on developer machines
    "scripts/analyze_image.py",
    imagePath
);
pb.redirectErrorStream(true);
Process process = pb.start();
```

Notes:
- `scripts/analyze_image.py` must be present relative to the application working directory.
- `pythonPath` is currently hardcoded in the reference `ImageAnalyzer` to `C:\\Python313\\python.exe`.

### 1.2 Python → Java Response

Format: single-line text, pipe-delimited: `SAFE|<entropy>` or `UNSAFE|<entropy>`.

Success example:
```text
SAFE|5.87
```

Failure / diagnostic output: the Python helper may print JSON or plain text for human-readable errors, but the Java helper currently checks the first line for the `SAFE` prefix.

Response fields (convention used by the helper script):
- `SAFE` / `UNSAFE`: status prefix consumed by `ImageAnalyzer.isImageSafe()`.
- `<entropy>`: Shannon entropy as a floating point number (typical range 0.0–8.0).

`ImageAnalyzer` behavior in Java:
- `isImageSafe(File)`: returns `true` when the first line from the Python script starts with `SAFE`.
- `getEntropyScore(File)`: parses the entropy value from the same pipe-delimited line.
- Threshold used in UI: `>= 4.5` (checked in `JournalPanel.hideAndSave()`).

## 2. File Format & Hidden Payload Layout

### 2.1 Input Image

- Recommended: PNG (lossless), 8-bit per channel RGB/RGBA.
- Minimum practical size: 32×32, but usable capacity depends on channels and header overhead.

### 2.2 Encrypted Payload Layout (embedded into image LSBs)

The Java `AESCrypto` implementation encodes the encrypted payload as Base64 of the concatenation:

- Salt (16 bytes)
- IV (12 bytes)
- Ciphertext (variable, includes GCM authentication tag)

This Base64 string is what `Steganography.embed(...)` stores into the image (prefixed by a 4-byte length header when embedding bytes).

## 3. Cryptography (current implementation)

Algorithm & parameters (as implemented in `backend/src/.../AESCrypto.java`):

- Key derivation: `PBKDF2WithHmacSHA256`, iterations = `600_000`, Salt = 16 bytes.
- Symmetric cipher: `AES/GCM/NoPadding` (AES-256-GCM) for both encryption and decryption.
- IV length: 12 bytes (GCM standard).
- GCM tag length: 128 bits.

Java API:
- `AESCrypto.encrypt(String text, char[] password)` → `String` (Base64-encoded Salt|IV|Ciphertext).
- `AESCrypto.decrypt(String encryptedBase64, char[] password)` → `String` (plaintext).

Security notes:
- The implementation derives a 256-bit AES key from the provided password and the random salt; the salt is stored with the ciphertext so decryption can re-derive the key.
- The code currently clears password arrays in the UI after use where possible; care should be taken to avoid logging secrets.

## 4. Steganography (current implementation)

API (Java):
- `Steganography.embed(BufferedImage image, String message)` → `BufferedImage` (returns a new image with embedded bytes).
- `Steganography.extract(BufferedImage image)` → `String` (returns the hidden Base64 payload or `null`).

Behavioral details:
- The implementation encodes a 4-byte big-endian length header followed by the payload bytes.
- Bits are embedded using LSBs across channels in RGB order (red, green, blue) per pixel.
- Available capacity (bits) = `width * height * 3`.
- Practical payload capacity (bytes) ≈ `(width * height * 3) / 8 - 4` (accounting for the 4-byte header).

Example checks in UI:
- `JournalPanel` computes `maxBytes = (width * height * 3 / 8) - 4` and prevents embedding if the plaintext (post-encryption) exceeds this.

## 5. Frontend Integration Points

Key UI behaviors (found in `frontend`):
- `LoginPanel`: collects password (`getPassword()`), supports a duress check `isDuress(char[])` which currently returns true for `{'1','2','3','4'}` and triggers a fake todo text when used.
- `JournalPanel`:
  - `loadImage()` – lets user pick an image and updates preview.
  - `hideAndSave()` – runs analysis (`ImageAnalyzer.isImageSafe()`), checks entropy (`getEntropyScore()`), encrypts text with `AESCrypto.encrypt(...)`, then embeds via `Steganography.embed(...)` and saves as PNG.
  - `revealText()` – extracts payload via `Steganography.extract(...)` and decrypts with `AESCrypto.decrypt(...)`.
- Duress behavior: entering the panic code (`1234`) returns a fixed dummy todo list instead of performing decryption.

## 6. Errors & Diagnostics

Python helper typical exit/status signals:
- Prints `SAFE|<entropy>` or `UNSAFE|<entropy>` on stdout for normal analysis results.
- On failures the script may output diagnostic JSON or text; `ImageAnalyzer` prints the script output to stdout/stderr for inspection.

Java-side error conditions used in the UI:
- `Invalid password` / decryption failure: surfaced when GCM tag verification fails (AEADBadTagException).
- `Python not found` / process launch errors: surfaced when `scripts/analyze_image.py` is missing or `pythonPath` is invalid.
- `Image too simple` / low entropy: `hideAndSave()` enforces a threshold of `4.5`.

---

**Last Updated:** December 18, 2025

```
**Last Updated:** December 2024
