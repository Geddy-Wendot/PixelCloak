package java.com.securevent.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCrypto {

    private static final String ENGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // Authentication tag length
    private static final int IV_LENGTH_BYTE = 12;  // Standard GCM IV length
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BIT = 256;

    // Encrypts plain text using a password
    public static String encrypt(String plainText, String password) throws Exception {
        // 1. Generate Salt and IV
        byte[] salt = getRandomBytes(SALT_LENGTH_BYTE);
        byte[] iv = getRandomBytes(IV_LENGTH_BYTE);

        // 2. Derive Key from Password
        SecretKey secretKey = getSecretKey(password, salt);

        // 3. Encrypt
        Cipher cipher = Cipher.getInstance(ENGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] encryptedText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4. Combine Salt + IV + CipherText to store them together
        // Format: [Salt (16)] [IV (12)] [Encrypted Data (...)]
        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + encryptedText.length);
        byteBuffer.put(salt);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedText);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    // Decrypts text using a password
    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] decode = Base64.getDecoder().decode(encryptedData);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decode);

        // 1. Extract Salt and IV
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        // 2. Extract Encrypted Bytes
        byte[] encryptedContent = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedContent);

        // 3. Derive Key
        SecretKey secretKey = getSecretKey(password, salt);

        // 4. Decrypt
        Cipher cipher = Cipher.getInstance(ENGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(encryptedContent);

        return new String(plainText, StandardCharsets.UTF_8);
    }

    private static SecretKey getSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private static byte[] getRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}