# gdg-minihackathon1
HealthTech- Technology for wellness

# SecureVent 
### *Privacy is the Foundation of Peace of Mind.*

SecureVent is a digital wellness application that allows students, parents, jounalist or government officials to write sensitive data without the fear of judgment or privacy breaches. it utilizes **Steganography** and **AES Encryption** to coceal text data inside image files.

**Problem:** Digital anxiety prevents **honesty expression. People fear their infomation leaking. 
**Solution:** A steganography tool that disguises sensitive data entries as innocent "Nature Photos".

## Tech Stack
**Language:** Java 17+ , Python
**GUI:** Java Swing and AWT
**Cryptography:** AES-256(javax.crypto) + SHA-256 Hashing
**Algorith:** Least Significant Bit (LSB) Manipulation

##  How to Run
1.  Open the project in VS Code.
2.  Run `src/main/java/com/securevent/App.java`.
3.  **To Hide:** Load a PNG -> Type Text -> Set Password -> Click "Hide & Save".
4.  **To Reveal:** Load the saved PNG -> Type Password -> Click "Reveal".

##  The Math (Steganography)
We treat the image as a matrix of pixels. We manipulate the binary vector of the Blue channel:
`New_Pixel = (Old_Pixel & 0xFFFFFFFE) | Secret_Bit`
This changes the color value by 1/255th, which is invisible to the human eye.

## Project Layout

```
securevent_v1/
├── frontend/
│   └── src/
│       └── main/
│           └── java/
│               └── com/securevent/
│                   ├── App.java                       # Entry point
│                   ├── core/
│                   │   ├── AESCrypto.java             # AES-256 encryption/decryption
│                   │   ├── ImageAnalyzer.java         # Python entropy analysis bridge
│                   │   └── Steganography.java         # LSB embedding and extraction
│                   └── ui/
│                       ├── GalleryPanel.java          # Image selection interface
│                       ├── JournalPanel.java          # Text editor for entries
│                       ├── LoginPanel.java            # Password input with duress protocol
│                       └── MainFrame.java             # Main application window
├── backend/
│   └── src/
│       ├── analyze_image.py                   # Shannon entropy calculator
│       ├── requirements.txt                   # Python dependencies
│       └── tests/
│           └── test_analyze_image.py          # Unit tests for entropy analysis
├── docs/
│   ├── API_SPEC.md                           # Java-Python interface specification
│   ├── ARCHITECTURE.md                       # System design and component details
│   ├── DEVELOPMENT.md                        # Python backend setup guide
│   ├── SECURITY.md                           # Security posture and threat model
│   └── schema.sql                            # SQLite audit logging schema
├── .gitignore                                 # Git exclusions
├── CONTRIBUTING.md                           # Development guidelines and branching rules
└── README.md                                  # This file
```

## File Descriptions

### Frontend (Java Swing)
- **App.java**: Entry point, initializes the main frame on the EDT
- **core/AESCrypto.java**: Handles AES-256-CBC encryption/decryption with SHA-256 key derivation
- **core/Steganography.java**: Implements LSB (Least Significant Bit) embedding and extraction on blue channel
- **core/ImageAnalyzer.java**: Bridges to Python backend for Shannon entropy analysis via ProcessBuilder
- **ui/MainFrame.java**: Primary window with tabbed interface (Journal, Gallery, Login)
- **ui/JournalPanel.java**: Text editor with character/word count tracking
- **ui/GalleryPanel.java**: Image browser and selection with entropy display
- **ui/LoginPanel.java**: Password input with duress protocol (panic code "1234")

### Backend (Python)
- **analyze_image.py**: Calculates Shannon entropy using PIL and NumPy to validate image complexity
- **requirements.txt**: Dependencies (Pillow, NumPy, SciPy)
- **tests/**: Unit tests for entropy calculations and edge cases

### Documentation
- **ARCHITECTURE.md**: System design, component hierarchy, IPC specification, data flows
- **SECURITY.md**: Cryptography details, threat model, memory safety, audit logging
- **API_SPEC.md**: JSON request/response format, encryption specs, error codes
- **DEVELOPMENT.md**: Python backend setup, entropy calculation theory, performance benchmarks
- **schema.sql**: SQLite database schema for audit logging


                
