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
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| status | string | "success" or "error" |
| entropy | float | Shannon entropy value (0.0-8.0) |
| safe | boolean | true if entropy ≥ 5.0 |
| message | string | Human-readable explanation |

## 2. File Format Specifications

### 2.1 PNG Format (Input)

**Requirements:**
- Format: PNG (RFC 2083)
- Minimum size: 32×32 pixels
- Color space: RGB or RGBA
- Bit depth: 8-bit per channel

**Why PNG?**
- Lossless (LSB modifications don't degrade image)
- Widely supported
- Includes error detection (CRC)

### 2.2 Encrypted Data Format (Hidden in PNG)

**Structure:**
```
[IV (16 bytes)] [Ciphertext (variable length)] [Padding (PKCS5)]

Total Size = 16 + ceil(plaintext.length / 16) * 16
```

## 3. Cryptography Operations

### 3.1 Encryption (AES-256-GCM)

**Process:**
1. Generate random 16-byte Salt.
2. Derive 256-bit key from password + Salt using PBKDF2-HMAC-SHA256 (600,000 iterations).
3. Generate random 12-byte Initialization Vector (IV).
4. Encrypt plaintext with AES-256-GCM.
5. Concatenate Salt + IV + Ciphertext (incl. Tag) and Base64 encode.

**Java Entry Point:**
```java
AESCrypto.encrypt(plaintext, passwordChars)
 Returns: String (Base64 encoded)
```

### 3.2 Decryption (AES-256-CBC)

**Process:**
1. Derive same key from password using SHA-256
2. Extract first 16 bytes as IV from encrypted data
3. Extract remaining bytes as ciphertext
4. Decrypt ciphertext with AES-256-CBC
5. Return plaintext string

**Java Entry Point:**
```java
 AESCrypto.decrypt(encrypted, password)
 Returns: String (original plaintext)
```

## 4. Steganography Operations

### 4.1 LSB Embedding (Hiding Data)

**Process:**
1. Extract pixel array from image (RGB values)
2. Iterate through each encrypted data byte
3. For each bit in the data, replace the LSB of the Blue channel in a pixel
4. Formula: `New_Blue = (Old_Blue & 0xFE) | Secret_Bit`
5. Reconstruct image with modified pixels and save

**Capacity:** 1 bit per pixel. Example: 8 MP image ≈ 1 MB capacity

**Java Entry Point:**
```java
 Steganography.hideData(image, encryptedBytes)
 Returns: BufferedImage with hidden data
```

### 4.2 LSB Extraction (Revealing Data)

**Process:**
1. Extract pixel array from image
2. Iterate through pixels for expected data length
3. Extract the LSB from Blue channel of each pixel
4. Reconstruct bytes from extracted LSBs
5. Pass to AES decryption

**Java Entry Point:**
```java
 Steganography.extractData(image, dataLength)
 Returns: byte[] (encrypted data)
```

## 5. Error Codes and Status Messages

### Python Errors

| Code | Message | Cause |
|------|---------|-------|
| 100 | File not found | Image path invalid |
| 101 | File is not a PNG | Wrong format |
| 102 | Image corrupted | Invalid PNG data |
| 200 | Entropy too low | < 5.0 |

### Java Errors

| Code | Message | Cause |
|------|---------|-------|
| 10 | Invalid password | Decryption failed |
| 11 | AES error | Cipher initialization failed |
| 20 | Python not found | ProcessBuilder fails |
| 21 | Process timeout | Python takes > 5 sec |

---

**Last Updated:** December 2024
