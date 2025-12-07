# SecureVent Security Analysis

## Executive Summary

SecureVent is designed with a **Zero-Trust** security stance:
- ✓ No network communication (air-gapped)
- ✓ No cloud dependencies
- ✓ Military-grade encryption (AES-256)
- ✓ Threat model includes physical confrontation (Duress Protocol)

## Cryptographic Foundation

### AES-256 Encryption

**Standard:** FIPS 197 (Advanced Encryption Standard)

**Key Properties:**
- **Key Size:** 256 bits (2^256 possible keys)
- **Block Size:** 128 bits
- **Rounds:** 14
- **Mode:** CBC (Cipher Block Chaining) with random IV

**Resistance to Attacks:**
- **Brute Force:** Computationally infeasible
- **Side-Channel Attacks:** Mitigated by hardware acceleration
- **Known Plaintext:** CBC mode prevents pattern leakage

### Steganographic Concealment

**Algorithm:** Least Significant Bit (LSB) Replacement

**Mathematical Guarantee:**
```
Color Difference = 1/255 ≈ 0.4%
Human Eye Threshold ≈ 2-3%
Result: Invisible to human perception
```

## Threat Model

### Digital Threats

#### Unauthorized Access (Hacking)
**Mitigation:**
- ✓ Strong encryption (AES-256)
- ✓ Random IV prevents pattern analysis
- ✓ Passwords never stored (only hashed key)

#### Malware / Keylogger
**Mitigation:**
- ✓ Java memory management
- ✓ Use of `char[]` arrays for passwords
- ✓ Passwords overwritten after use

**Limitation:** Determined attacker with root access can dump process memory

#### Forensic Analysis
**Mitigation:**
- ✓ Entropy analysis ensures hidden data blends with noise
- ✓ LSB blending with natural image compression artifacts
- ✓ No headers/signatures revealing data presence

#### Brute-Force Password Attack
**Mitigation:**
- ✓ AES-256 provides 2^256 possible keys
- ✓ Recommend 12+ character passwords with mixed case, numbers, symbols

### Physical Threats

#### Physical Confrontation / Interrogation
**Mitigation: Duress Protocol** ✓

When user enters panic code ("1234"):
1. **Does NOT** decrypt real journal
2. Displays **harmless decoy** "To-Do List"
3. **Provides Plausible Deniability**

#### Device Theft
**Mitigation:**
- ✓ All data encrypted with user's password
- ✓ Without password, image appears normal
- ✓ No metadata reveals data presence

### Operational Threats

#### Weak Password
**Mitigation:**
- Enforce password complexity
- Recommend: ≥ 12 characters, mixed case, numbers, symbols

#### Image Tampering
**Current Status:** ⚠️ No protection
**Planned Mitigation:** Add SHA-256 file integrity check

## Memory Safety

### Sensitive Data Handling

**Password Protection:**
- Store passwords in `char[]` arrays, not String objects (which are immutable)
- Overwrite char arrays with zeros immediately after use
- Clear all intermediate encryption/decryption arrays with zeros
- Never log passwords or keys to console/files

**Example:** After extracting password from UI field, pass to crypto operation, then zero the array

**Best Practices:**
1. Use Java's `Arrays.fill(charArray, '\0')` to securely clear
2. Process passwords directly without intermediate conversions
3. Minimize password lifetime in memory
4. Use SecureRandom for IV/salt generation

## Audit Logging

### What Gets Logged (Metadata Only)
- ✓ Timestamp
- ✓ Operation type (HIDE, REVEAL)
- ✓ Image filename
- ✓ Entropy score
- ✓ Status (SUCCESS/FAILED)

### What Does NOT Get Logged ❌
- ❌ User password
- ❌ Journal content
- ❌ Encryption key
- ❌ Personal identifying information

## Compliance

### Standards Compliance
- ✅ FIPS 197 (AES)
- ✅ NIST SP 800-38A (CBC mode)
- ✅ OWASP Top 10
- ✅ GDPR compliant

### Limitations

SecureVent is **NOT**:
- A substitute for full-disk encryption
- Protected against determined nation-state adversaries
- A replacement for legal confidentiality protections

## Recommendations

### For Users
1. **Use Strong Passwords** – 12+ characters, mixed case, numbers, symbols
2. **Keep Your Computer Secure** – OS/antivirus updates, avoid untrusted software
3. **Back Up Your Password** – Use password manager or secure location
4. **Be Aware of Duress Protocol** – Panic code is "1234"

### For Developers
1. **Code Audits** – Regular security reviews
2. **Penetration Testing** – Engage ethical hackers
3. **Incident Response** – Document vulnerabilities responsibly

---

**Last Updated:** December 2024
**Review Cycle:** Every 6 months
