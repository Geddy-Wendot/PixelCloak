# PixelCloak Security Summary

## Executive Summary

PixelCloak is a local-first desktop application. Key security characteristics:
- No network communication by design
- No cloud dependencies
- Encryption uses AES-256-GCM (authenticated encryption)

## Cryptographic Foundation

### AES-256-GCM (current implementation)

Key implementation parameters (as in `backend/src/main/java/com/pixelcloak/core/AESCrypto.java`):
- Key derivation: `PBKDF2WithHmacSHA256`, iterations = 600_000
- Salt: 16 bytes (random per-encryption)
- Symmetric cipher: `AES/GCM/NoPadding` (AES-256-GCM)
- IV length: 12 bytes (random per-encryption)
- Authentication tag: 128 bits

Notes:
- The `encrypt()` method returns a Base64 string containing `Salt || IV || Ciphertext` which is what the UI embeds into images.
- High PBKDF2 iteration counts slow brute-force attacks but increase CPU cost on low-power devices.

### Steganography (current implementation)

Implementation details (see `backend/src/main/java/com/pixelcloak/core/Steganography.java`):
- Payload format: 4-byte big-endian length header followed by payload bytes.
- Payload content: UTF-8 bytes of the Base64-encoded encrypted blob returned by `AESCrypto.encrypt(...)`.
- Embedding: LSBs across channels in RGB order (red, green, blue), effectively 3 bits per pixel.
- The code uses a copied `BufferedImage` (`TYPE_INT_RGB`) before modifying pixels to avoid mutating UI references.

Capacity:
- Available bits = `width * height * 3`
- Practical payload bytes ≈ `(width * height * 3) / 8 - 4` (accounting for the 4-byte header)

## Analyzer & IPC

- The image entropy helper is `scripts/analyze_image.py`. It converts images to grayscale and computes Shannon entropy over 256 intensity bins.
- The helper prints a single pipe-delimited result line, e.g. `SAFE|5.87` or `UNSAFE|3.22`. The Java `ImageAnalyzer` reads the first line and treats lines starting with `SAFE` as acceptable.
- `ImageAnalyzer` currently references a hardcoded Python executable path (`C:\\Python313\\python.exe`) — this may need to be configured per system.

## Threat Model & Mitigations

Unauthorized access / brute force:
- Mitigation: AES-256-GCM with PBKDF2-derived keys; recommend long, high-entropy passwords (12+ characters).

Tampering / integrity:
- Mitigation: GCM authentication tag detects modifications; decryption will fail with an AEADBadTagException if data or associated parameters are tampered with.

Local compromise (malware/keylogger):
- Mitigation: minimize password lifetime in memory, overwrite `char[]` after use, and avoid logging secrets.
- Limitation: an attacker with administrative access can read process memory or intercept keystrokes.

Forensic detection of hidden data:
- Mitigation: entropy checks block embedding into low-entropy images; however, skilled forensic analysis may still detect steganographic payloads. Do not assume perfect undetectability.

Duress feature:
- The UI implements a panic/duress behavior: entering the configured panic code (currently the char sequence `{'1','2','3','4'}`) returns a decoy text instead of attempting decryption. Review and configure this behavior to match your threat model.

## Memory Safety & Handling Secrets

Current practices and recommendations:
- Use `char[]` for password input (avoids Java String immutability exposing secrets).
- Clear password arrays immediately after use (e.g., `Arrays.fill(passwordChars, '\0')`). The UI currently overwrites password arrays in places; ensure all code paths clear sensitive buffers.
- Avoid converting secret `char[]` to `String` or logging any intermediate values.
- Use `SecureRandom` for salt and IV generation (code uses `SecureRandom`).

## Operational Recommendations

- Make the Python executable path configurable in `ImageAnalyzer` or detect `python` on PATH to avoid hardcoded absolute paths.
- Consider reducing PBKDF2 iterations for constrained environments or using adaptive iteration counts with a configurable parameter.
- Add a secure wipe function for any byte[] or char[] buffers that hold plaintext or intermediate values.
- Add optional HMAC or external metadata if you need tamper-evidence beyond GCM (careful with key management).

## Limitations

- PixelCloak does not provide full-disk encryption or protection against a fully-compromised host.
- Steganography reduces detectability but is not guaranteed to be undetectable by advanced forensic tools.

## Incident Response & Audits

- Perform regular code audits and cryptographic reviews when changing crypto primitives.
- If a vulnerability is found, rotate any affected guidance and document mitigation steps.

---

**Last Updated:** December 18, 2025
