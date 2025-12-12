# PixelCloak - Steganographic Journal 

PixelCloak is a desktop application that allows you to hide encrypted journal entries within images using steganography. It provides a secure and private way to maintain a personal diary, with a "duress" feature for added security.

The project consists of a Java Swing frontend for the user interface and a Python backend for image analysis.

## Project Structure

```
PixelCloak/
├── backend/
│   ├── src/
│   └── ... (see backend/README.md for details)
├── frontend/
│   ├── src/
│   └── ... (see frontend/README.md for details)
└── README.md
```

## Features

-   **AES-256 Encryption:** Uses AES-GCM (Galois/Counter Mode) with PBKDF2 key derivation (600,000 iterations) to secure your text.
-   **LSB Steganography:** Embeds encrypted data into the Least Significant Bits of the image pixels, making it invisible to the naked eye.
-   **Image Entropy Analysis:** A Python backend analyzes images to ensure they are complex enough for secure data hiding.
-   **Modern Swing UI:** A calming, book-themed interface for a pleasant user experience.

## Getting Started

### Prerequisites

-   Java 17+ (JDK)
-   Maven 3.6+
-   Python 3.10+
-   IntelliJ IDEA: Recommended IDE for running this project.

### Installation and Setup

**1.**Configure Python Environment
The application requires the Python `Pillow` library to perform image analysis. 
Open your terminal and run
```bash
pip install pillow
```

### Running the Application

The application is composed of two main parts: the Java frontend and the Python backend. The frontend calls the backend for image analysis.

**1. Setup the Backend**

Navigate to the backend directory and install the required Python packages. Remember to activate the virtual environment first (`venv\Scripts\activate` on Windows or `source venv/bin/activate` on macOS/Linux).


The Java application will launch, and it will automatically call the Python script for image analysis when needed. Ensure that the `python` command is available in your system's PATH.

## Detailed Documentation

For more detailed information on each component, please refer to their respective README files:

-   **Frontend README**
-   **Backend README**

---
**Last Updated:** December 2025