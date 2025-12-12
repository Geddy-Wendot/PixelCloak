# Backend - Core Logic & Analysis

## Overview

The backend module contains the Core Java Logic (Encryption, Steganography) and the bridge to the Python Analysis Engine.

## Structure

```
backend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── pixelcloak/
│                   └── core/
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
- Key Derivation: PBKDF2WithHmacSHA256 (600,000 iterations)

**Features:**
- Authenticated Encryption: Ensures data hasn't been tampered with.
- Salt & IV: Automatically generates random Salt (16 bytes) and IV (12 bytes) for every entry.
- Storage: Packs [Salt + IV + CipherText] into a single Base64 string.

**Usage:**
``` java
char[] pass = "UserPassword123".toCharArray();
String encrypted = AESCrypto.encrypt("My Secret Diary", pass);
String decrypted = AESCrypto.decrypt(encrypted, pass);
```

**2. Steganography.java**
Implements the Least Significant Bit (LSB) algorithm to hide data imperceptibly.

- Technique: Modifies the LSB of Red, Green, and Blue channels.
- Capacity: 3 bits per pixel.
- Header: The first 32 bits (across channels) store the length of the data.

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
boolean isSafe = ImageAnalyzer.isImageSafe(imageFile);
if (isSafe) {
    System.out.println("Image is complex enough.");
}
```
Last Updated: December 2025 Status: Development.