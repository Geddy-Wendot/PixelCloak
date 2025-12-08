# Backend - Python Analysis Engine

## Overview

The backend is pure Java core library for SecureVent. It encapsulates all the criticall security and processing logic including AES-256 encryption, LSB Steganography, and Shannon Entropy Analysis.

## Structure

```
backend/
├── src/
│   └── java/
│       └── com/
│           └── securevent/
│               └── core/
│                   ├── AESCrypto.java      # AES-256-GCM Encryption
│                   ├── Steganography.java  # LSB Image Encoding/Decoding
│                   └── ImageAnalyzer.java  # Entropy & Complexity validation
├── pom.xml                                 # Maven configuration
└── README.md                               # This documentation
```

---

## Quick Start

### Prerequisites
- Java 17+ (JDK)
- Maven 3.6+

### Installation
Since this is a core lib, it's typically built as part of the main project or installed to your local repository.

**1. Build the Backend:**
```bash
cd backend
mvn clean install
```

## Core Modules
**1. AESCrypto.java**
- Handles the encryption of user journals before they touch the image.
- Algorithm: AES-256-GCM (Galois/Counter Mode)
- Key Derivation: PBKDF2WithHmacSHA256 (65,536 iterations)

**Features:**
- Authenticated Encryption: Ensures data hasn't been tampered with.
- Salt & IV: Automatically generates random Salt (16 bytes) and IV (12 bytes) for every entry.
- Storage: Packs [Salt + IV + CipherText] into a single Base64 string.

**Usage:**
``` java
String encrypted = AESCrypto.encrypt("My Secret Diary", "UserPassword123");
String decrypted = AESCrypto.decrypt(encrypted, "UserPassword123")
```

**2. Steganography.java**
Implements the Least Significant Bit (LSB) algorithm to hide data imperceptibly.

- Technique: Modifies the LSB of the Blue color channel.
- Capacity: 1 bit per pixel.
- Header: The first 32 pixels store the length of the data (integer) to ensure accurate extraction.

**Usage:**
```java
// Hide text
BufferedImage protectedImage = Steganography.embed(originalImage, encryptedString);

// Reveal text
String hiddenData = Steganography.extract(protectedImage);

```
**3. ImageAnalyzer.java (Validation)**
Replaces the Python analysis engine. It calculates the complexity of an image to prevent users from hiding data in simple images (like a solid white box) which would make the noise obvious.

- Method: Calculates Shannon Entropy on grayscale pixel intensity.
- Threshold: > 4.5 is recommended for safe hiding.

**Usage:**
```java
AnalysisResult result = ImageAnalyzer.analyze(image);
if (result.isSafe()) {
    System.out.println("Image is complex enough: " + result.entropy());
}
```
Last Updated: December 2025 Status: Development.