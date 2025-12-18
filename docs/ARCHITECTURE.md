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
        # PixelCloak Architecture Documentation

        ## System Overview

        PixelCloak uses a hybrid architecture: a Java desktop UI + core logic, with a small Python helper used for image analysis. Java handles encryption, steganography, and the user experience; Python computes image entropy for safety checks.

        ### System Architecture (conceptual)

        ```
        USER (PixelCloak Desktop App)
          └─ Java Swing UI (MainFrame, JournalPanel, LoginPanel)
               ├─ Steganography (backend/src/main/java/com/pixelcloak/core/Steganography.java)
               ├─ Cryptography (backend/src/main/java/com/pixelcloak/core/AESCrypto.java)
               └─ ImageAnalyzer (calls scripts/analyze_image.py via ProcessBuilder)
                     └─ Python process: scripts/analyze_image.py (Shannon entropy + validation)
        ```

        ## Component Locations

        - Presentation (Java Swing): `frontend/src/main/java/com/pixelcloak/ui/` and `frontend/src/main/java/com/pixelcloak/app/`
        - Core Java library (encryption/steganography): `backend/src/main/java/com/pixelcloak/core/`
        - Python analysis helper: `scripts/analyze_image.py` (root-level `scripts/` folder)

        ## Component Details

        ### Presentation Layer (Java Swing)
        - `MainFrame` — application window and startup (`frontend/src/.../MainFrame.java`).
        - `LoginPanel` — collects password, implements duress check (`isDuress` for `{'1','2','3','4'}`).
        - `JournalPanel` — editor UI that loads/saves images, triggers analysis, encryption, embedding, and reveal flows.

        ### Core Logic (Java library)
        Location: `backend/src/main/java/com/pixelcloak/core/`

        - `Steganography.java`
          - Embeds/extracts bytes into/from a `BufferedImage`.
          - Prepends a 4-byte big-endian length header followed by payload bytes.
          - Embeds bits across channels in RGB order (red, green, blue).
          - Capacity (bytes) ≈ `(width * height * 3) / 8 - 4`.

        - `AESCrypto.java`
          - Key derivation: `PBKDF2WithHmacSHA256` (iterations = 600_000), Salt = 16 bytes.
          - Cipher: `AES/GCM/NoPadding` (AES-256-GCM), IV length = 12 bytes, tag length = 128 bits.
          - `encrypt(String text, char[] password)` → Base64(Salt || IV || Ciphertext).
          - `decrypt(String encryptedBase64, char[] password)` → plaintext string.

        - `ImageAnalyzer.java`
          - Spawns `scripts/analyze_image.py` via `ProcessBuilder` (the current reference implementation hardcodes `C:\\Python313\\python.exe` as `pythonPath`).
          - Reads the script stdout; `isImageSafe()` returns `true` when the first line starts with `SAFE`.
          - `getEntropyScore()` parses the entropy value from a pipe-delimited response (e.g., `SAFE|5.87`).

        ### Python Analysis Helper
        - Location: `scripts/analyze_image.py`.
        - Responsibility: validate image, compute Shannon entropy, print a one-line result like `SAFE|<entropy>` or `UNSAFE|<entropy>` (Java code expects a pipe-delimited first line).

        ## Inter-Process Communication (IPC)

        - Method: OS-level process spawn using `ProcessBuilder`.
        - Example (from `ImageAnalyzer`):
        ```java
        ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath(), imageFile.getAbsolutePath());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String firstLine = reader.readLine();
        ```
        - Notes:
          - `pythonPath` is not currently configurable in code and is set to `C:\\Python313\\python.exe` in the reference implementation.
          - The Java analyzer treats only the first line for a quick `SAFE` prefix check; additional diagnostic output is printed for inspection.

        ## Data Flows

        Hiding a secret (high-level):
        1. User loads a PNG into `JournalPanel` → `BufferedImage`.
        2. `JournalPanel` calls `ImageAnalyzer.isImageSafe(File)` (spawns Python helper).
        3. If the entropy check passes (UI threshold: 4.5), the user-provided text is encrypted via `AESCrypto.encrypt(...)`.
        4. The resulting Base64 string is converted to bytes and embedded into the image using `Steganography.embed(...)` (4-byte length header + payload bytes).
        5. Modified image is written to disk as PNG.

        Revealing a secret (high-level):
        1. User loads PNG; enters password.
        2. If the password equals the panic code (`{'1','2','3','4'}`), the UI returns a decoy todo list.
        3. Otherwise `Steganography.extract(...)` returns the embedded payload string (Base64 encoded encrypted blob).
        4. `AESCrypto.decrypt(...)` is invoked with the password to recover the original plaintext.

        ## Steganography specifics
        - Embedding order: RGB channels, using LSB per channel.
        - Encoding: a 4-byte big-endian length header (number of payload bytes) followed by payload bytes.
        - Implementation note: `Steganography.embed(...)` creates a new `BufferedImage` (`TYPE_INT_RGB`) and draws a copy before modifying pixels to avoid mutating UI references.

        ## Entropy Analysis
        - The Python helper computes Shannon entropy over pixel intensity distribution and returns a human-readable single-line result.
        - UI threshold: `JournalPanel.hideAndSave()` enforces a minimum entropy of `4.5` (not 5.0) before allowing embedding.

        ## Persistent Storage
        - The repository does not include an application database in the current tree. No SQLite `data/` persistence is present in the workspace; audit logging is handled in-memory or by the UI flow (none persisted by default).

        ## Security and Implementation Notes
        - Password handling: UI clears `char[]` password arrays after use where possible, but Java string copies (if made) can remain in memory — avoid logging secrets.
        - Crypto parameters: PBKDF2 iteration count is high (`600_000`) to harden password-derived keys; this increases CPU cost during encrypt/decrypt on low-power devices.
        - The image-analyzer subprocess needs a valid Python executable; consider making `pythonPath` configurable or bundling a lightweight analyzer.

        ---

        **Last Updated:** December 18, 2025
