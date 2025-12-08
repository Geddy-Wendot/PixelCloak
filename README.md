# SecureVent - Steganographic Journal

SecureVent is a desktop application that allows you to hide encrypted journal entries within images using steganography. It provides a secure and private way to maintain a personal diary, with a "duress" feature for added security.

The project consists of a Java Swing frontend for the user interface and a Python backend for image analysis.

## Project Structure

```
securevent_v1/
├── backend/
│   ├── src/
│   └── ... (see backend/README.md for details)
├── frontend/
│   ├── src/
│   └── ... (see frontend/README.md for details)
└── README.md
```

## Features

-   **Rich Text Journaling:** Write and format your journal entries with an intuitive editor.
-   **AES-256 Encryption:** All entries are encrypted with a user-provided password before being hidden.
-   **Steganography:** Uses the Least Significant Bit (LSB) technique to embed data into images.
-   **Image Entropy Analysis:** A Python backend analyzes images to ensure they are complex enough for secure data hiding.
-   **Duress Mode:** A special "panic code" can be used to open a decoy journal, protecting your real entries under coercion.
-   **Book-like UI:** A calming, book-themed interface for a pleasant user experience.

## Getting Started

### Prerequisites

-   Java 17+ (JDK)
-   Maven 3.6+
-   Python 3.8+
-   pip

### Running the Application

The application is composed of two main parts: the Java frontend and the Python backend. The frontend calls the backend for image analysis.

**1. Setup the Backend**

Navigate to the backend directory and install the required Python packages. Remember to activate the virtual environment first (`venv\Scripts\activate` on Windows or `source venv/bin/activate` on macOS/Linux).

```bash
cd backend
pip install -r requirements.txt
```

**2. Build and Run the Frontend**

Navigate to the frontend directory, build, and run the Java application using Maven.

```bash
cd frontend
mvn clean package
mvn exec:java -Dexec.mainClass="com.securevent.App"
```

The Java application will launch, and it will automatically call the Python script for image analysis when needed. Ensure that the `python` command is available in your system's PATH.

## Detailed Documentation

For more detailed information on each component, please refer to their respective README files:

-   **Frontend README**
-   **Backend README**

---
**Last Updated:** December 2025