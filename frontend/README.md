# Frontend - SecureVent UI (Java Swing)

## Overview

The frontend is a Java Swing application that provides the user interface for SecureVent. It implements a "Book Mode" aesthetic with calming colors and intuitive workflow for hiding and revealing encrypted journal entries in images.

## Structure

```
frontend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── securevent/
│                   ├── App.java                    # Entry point
│                   ├── ui/
│                   │   ├── MainFrame.java          # Main window
│                   │   ├── JournalPanel.java       # Journal editor
│                   │   ├── GalleryPanel.java       # Image gallery
│                   │   └── LoginPanel.java         # Password & duress
│                   ├── core/
│                   │   ├── Steganography.java      # LSB encoding/decoding
│                   │   ├── AESCrypto.java          # Encryption/decryption
│                   │   └── ImageAnalyzer.java      # Entropy validation
│                   └── utils/
│                       └── Constants.java          # Configuration
├── lib/                                             # External JARs
├── pom.xml                                          # Maven configuration
└── README.md
```

---

## Quick Start

### Prerequisites
- Java 17+ (JDK)
- Maven 3.6+

### Build

**Using Maven:**
```bash
cd frontend
mvn clean compile
```

### Run

**Using Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.securevent.App"
```

**Direct Java:**
```bash
java -cp target/classes com.securevent.App
```

---

## Component Descriptions

### 1. App.java (Entry Point)
```java
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
```

### 2. MainFrame.java (Main Window)
**Purpose:** Container for all UI panels and navigation

**Features:**
- Tab-based navigation
- Status bar
- Menu bar (File, Edit, Help)
- Responsive layout

### 3. JournalPanel.java (Journal Editor)
**Purpose:** Text editor for writing journal entries

**Features:**
- Rich text editing
- Character count
- Word count
- Search functionality

### 4. GalleryPanel.java (Image Selection)
**Purpose:** Browse and select images

**Features:**
- Thumbnail previews
- Entropy score display
- Metadata display

### 5. LoginPanel.java (Password & Duress)
**Purpose:** Handle authentication

**Features:**
- Password field (masked input)
- Panic code detection ("1234")
- Duress mode activation

### 6. Steganography.java (LSB Algorithm)
**Purpose:** Hide and extract encrypted data in images

**Methods:**
```java
public BufferedImage hideData(BufferedImage image, byte[] data)
public byte[] extractData(BufferedImage image, int length)
```

### 7. AESCrypto.java (Encryption)
**Purpose:** AES-256-CBC encryption and decryption

**Methods:**
```java
public byte[] encrypt(String plaintext, String password)
public String decrypt(byte[] encrypted, String password)
```

### 8. ImageAnalyzer.java (Python Bridge)
**Purpose:** Call Python entropy analyzer

**Methods:**
```java
public AnalysisResult analyzeImage(File imageFile)
```

---

## UI Styling

### Color Scheme
- **Primary:** Earth brown (#8B6F47)
- **Secondary:** Soft blue (#4A90E2)
- **Accent:** Calming green (#3CA85C)
- **Background:** Off-white (#F5F1E8)
- **Text:** Dark gray (#2C3E50)

---

## Threading Model

### Swing EDT
All UI updates must occur on the Event Dispatch Thread (EDT):
```java
SwingUtilities.invokeLater(() -> {
    journalPanel.setText("Updated text");
});
```

### Background Tasks
Long operations run on separate threads:
```java
new Thread(() -> {
    AnalysisResult result = imageAnalyzer.analyzeImage(file);
    SwingUtilities.invokeLater(() -> {
        displayResult(result);
    });
}).start();
```

---

## Testing

### Unit Tests
```bash
mvn test
```

### Build Portable JAR
```bash
mvn clean package
java -jar target/securevent-1.0.0-SNAPSHOT.jar
```

---

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Core language |
| Swing | Built-in | UI framework |
| AWT | Built-in | Graphics |
| JUnit | 5.x | Testing |

---

## Troubleshooting

### Issue: "Cannot find Python executable"
**Solution:** Ensure Python is in system PATH or provide absolute path in ImageAnalyzer.java

### Issue: "UI elements not rendering"
**Solution:** Run on Swing EDT using `SwingUtilities.invokeLater()`

### Issue: "Out of memory with large images"
**Solution:** Downscale images before processing

---

**Last Updated:** December 2024
**Status:** In Development
