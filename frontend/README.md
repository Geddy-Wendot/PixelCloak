# Frontend - PixelCloak UI (Java Swing)

## Overview

The frontend is a Java Swing application that provides the user interface for PixelCloak. It implements a modern dark-themed interface for hiding and revealing encrypted journal entries in images.

## Structure

```
frontend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── pixelcloak/
│                   ├── App.java                    # Entry point
│                   ├── ui/
│                   │   ├── MainFrame.java          # Main window
│                   │   ├── JournalPanel.java       # Journal editor
│                   │   └── LoginPanel.java         # Password & duress
│                            
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
mvn exec:java -Dexec.mainClass="com.pixelcloak.App"
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

### 2. JournalPanel.java (Main Interface)
**Purpose:** The primary interface handling image loading, text editing, and encryption operations.

**Features:**
- **Split View:** Text editor on left, Image preview on right.
- **Image Loader:** Validates and displays PNG/JPG images.
- **Security Controls:** Password input and action buttons (Load, Hide, Reveal).
- **Feedback:** Status bar with color-coded success/error messages.
- **Duress Protocol:** Entering "1234" triggers a decoy mode.

### 3. Steganography.java (LSB Algorithm)
**Purpose:** Embeds encrypted bytes into image pixels using 3-bit LSB (Red, Green, Blue channels).

**Methods:**
```java
public static BufferedImage embed(BufferedImage image, String message)
public static String extract(BufferedImage image)
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

**Last Updated:** December 2025
**Status:** In Development
