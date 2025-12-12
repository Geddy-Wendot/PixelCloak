package com.pixelcloak.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESCrypto {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int SALT_LENGTH = 16;

    // NIST recommended minimum for PBKDF2-HMAC-SHA256 is 600,000+
    private static final int ITERATION_COUNT = 600_000;

    public static String encrypt(String text, char[] password) throws Exception {
        // FIXED: Changed 'plainText' to 'text' to match the parameter name
        if (text == null || text.isEmpty()) return null;

        // 1. Generate Random Salt and IV
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        random.nextBytes(iv);

        // 2. Derive Key from Password
        SecretKey secretKey = deriveKey(password, salt);

        // 3. Encrypt
        // Removed empty try-finally block
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        // FIXED: Changed 'plainText' to 'text'
        byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        // 4. Combine Salt + IV + CipherText
        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        byteBuffer.put(salt);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    public static String decrypt(String encrypted, char[] password) throws Exception {
        // FIXED: Changed 'encryptedData' to 'encrypted' to match the parameter name
        if (encrypted == null || encrypted.isEmpty()) return null;

        // FIXED: Changed 'encryptedData' to 'encrypted'
        byte[] decode = Base64.getDecoder().decode(encrypted);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decode);

        // 1. Extract Salt and IV
        if (byteBuffer.remaining() < SALT_LENGTH + GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        byte[] salt = new byte[SALT_LENGTH];
        byteBuffer.get(salt);

        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);

        // 2. Extract CipherText
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        // 3. Derive Key
        SecretKey secretKey = deriveKey(password, salt);

        // 4. Decrypt
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, AES_KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}