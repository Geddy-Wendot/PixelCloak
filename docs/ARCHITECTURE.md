# PixelCloak Architecture Documentation

## System Overview

PixelCloak follows a **Hybrid Full-Stack Architecture** combining Java (Frontend/Core Logic) and Python (Analysis Engine).

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     USER (PixelCloak Desktop App)                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  Java Swing UI  │ (MainFrame, JournalPanel, etc.)
                    │  ("Book Mode")  │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼───────┐  ┌────────▼───────┐
│ Steganography  │  │  AES Crypto    │  │ Image Analyzer │
│ (LSB RGB Algo) │  │ (AES-GCM-256)  │  │  (Calls Script)│
└────────┬───────┘  └────────┬───────┘  └────────┬───────┘
         │                   │                   │
         │                   └─────────┬─────────┘
         │                             │
         │         ┌──────────────────▼──────────────────┐
         │         │   ProcessBuilder (IPC Bridge)       │
         │         │   Spawns: python scripts/analyze_image.py │
         │         └──────────────────┬──────────────────┘
         │                            │
         │                   ┌────────▼────────┐
         │                   │   Python 3      │
         │                   │  (PIL + NumPy)  │
         │                   │  Shannon Entropy│
         │                   │   Calculator    │
         │                   └────────┬────────┘
         │                            │
         │         ┌──────────────────▼──────────────────┐
         │         │  Returns: SAFE|SCORE                │
         │         │  or UNSAFE|SCORE                    │
         │         └──────────────────┬──────────────────┘
         │                            │
         └────────────────┬───────────┘
                          │
                  ┌───────▼────────┐
                  │   Image File   │
                  │  (PNG with     │
                  │   hidden data) │
                  └─────────────────┘
```

## Component Details

### 1. Presentation Layer (Java Swing)
**Location:** `frontend/src/main/java/com/securevent/ui/`

**Components:**
- **MainFrame.java** – Main application window container
- **JournalPanel.java** – Text editor for journal entries
- **LoginPanel.java** – Password input + Duress Protocol detection

### 2. Application Logic Layer (Java Core)
**Location:** `frontend/src/main/java/com/securevent/core/`

**Components:**

#### A. Steganography Engine (Steganography.java)
- LSB embedding and extraction
- **Algorithm:** LSB (Least Significant Bit) modification.
- **Channels:** Embeds data in Red, Green, and Blue channels sequentially.
- **Capacity:** 3 bits per pixel (1 bit per channel).

#### B. Cryptography Engine (AESCrypto.java)
- **Algorithm:** AES-256-GCM (Galois/Counter Mode).
- **Key Derivation:** PBKDF2WithHmacSHA256 (600,000 iterations).

#### C. Image Analyzer (ImageAnalyzer.java)
- Calls Python subprocess
- Passes image path via ProcessBuilder
- Parses entropy score

### 3. Analysis Engine (Python)
**Location:** `backend/src/analyze_image.py`

**Implementation:**
- Shannon Entropy calculation
- Image validation
- Threshold-based safety check

### 4. Persistence Layer (SQLite)
**Location:** `data/securevent_audit.db`

**Schema:**
- audit_logs table (timestamp, operation, filename, entropy, status)
- Performance indices
- Reporting views

## Inter-Process Communication (IPC)

### Java ↔ Python Bridge

**Method:** ProcessBuilder (OS-level process spawning)

```java
ProcessBuilder pb = new ProcessBuilder(
    "python", 
    "backend/src/analyze_image.py", 
    imagePath
);
Process process = pb.start();
BufferedReader reader = new BufferedReader(
    new InputStreamReader(process.getInputStream())
);
String jsonResponse = reader.readLine();
```

## Data Flow: Hiding a Secret

```
User opens image
    ↓
Java loads PNG into BufferedImage
    ↓
Java calls ImageAnalyzer.analyzeImage(path)
    ↓
ProcessBuilder spawns: python analyze_image.py <path>
    ↓
Python calculates entropy → Returns JSON
    ↓
entropy >= 5.0?
    YES: Continue ✓
    NO: Show error ❌
    ↓
User types journal entry
    ↓
Java calls AESCrypto.encrypt(entry, password)
    ↓
Result: IV + ciphertext
    ↓
Java calls Steganography.hideData(image, encryptedBytes)
    ↓
Replace LSBs with encrypted data bits
    ↓
Save modified PNG to disk
    
```

## Data Flow: Revealing a Secret

```
User loads PNG with hidden data
    ↓
Java loads PNG into BufferedImage
    ↓
User enters password
    ↓
Check if password == "1234"?
    YES: Display decoy To-Do List → EXIT
    NO: Continue ✓
    ↓
Java calls Steganography.extractData(image)
    ↓
Extract LSBs from Blue channel
    ↓
Java calls AESCrypto.decrypt(ciphertext, password)
    ↓
Display journal entry
    
```

## Steganography (LSB Algorithm)

**Formula:**
For each channel (Red, Green, Blue) of a pixel:
$$\text{New\_Channel} = (\text{Old\_Channel} \, \& \, 0xFE) \, | \, \text{Secret\_Bit}$$

**Effect:**
- Color change: +/- 1 value on 0-255 scale (invisible to human eye).
- Data capacity: 3 bits per pixel.

## Entropy Analysis (Smart Defense)

**Formula:**
$$H(X) = -\sum_{i=0}^{255} p(i) \log_2 p(i)$$

**Thresholds:**
- Entropy < 5.0: UNSAFE ❌
- Entropy ≥ 5.0: SAFE ✓

---

**Last Updated:** December 2024
