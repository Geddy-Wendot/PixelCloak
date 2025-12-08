package java.com.securevent.core;

import java.awt.image.BufferedImage;

public class Steganography {

    // Hides the secret text inside the image
    public static BufferedImage embed(BufferedImage image, String text) {
        // We embed the length of the text first (32 bits), then the text itself.
        byte[] textBytes = text.getBytes();
        int len = textBytes.length;
        
        // Combine Length + Data
        byte[] dataToHide = new byte[4 + len];
        
        // Encode length in first 4 bytes
        dataToHide[0] = (byte) ((len >> 24) & 0xFF);
        dataToHide[1] = (byte) ((len >> 16) & 0xFF);
        dataToHide[2] = (byte) ((len >> 8) & 0xFF);
        dataToHide[3] = (byte) ((len) & 0xFF);
        
        System.arraycopy(textBytes, 0, dataToHide, 4, len);
        
        return embedBytes(image, dataToHide);
    }

    // Extracts hidden text from the image
    public static String extract(BufferedImage image) {
        // First, extract the length (first 32 bits / 32 pixels)
        int length = extractLength(image);
        
        if (length <= 0 || length > 1_000_000) { 
            return ""; // Invalid or empty
        }

        // Extract the actual data
        byte[] data = extractBytes(image, length, 32); // skip first 32 pixels used for length
        return new String(data);
    }

    // --- Low Level Bit Manipulation ---

    private static BufferedImage embedBytes(BufferedImage image, byte[] data) {
        int width = image.getWidth();
        int height = image.getHeight();
        int dataIndex = 0;
        int bitIndex = 7; // Start from MSB of the byte

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (dataIndex >= data.length) return image;

                int rgb = image.getRGB(x, y);
                
                // Get the current bit to hide (0 or 1)
                int bitToHide = (data[dataIndex] >> bitIndex) & 1;

                // Modify the Blue channel's LSB
                int blue = rgb & 0xFF;
                blue = (blue & 0xFE) | bitToHide; // Clear LSB, then OR with new bit

                // Reconstruct RGB
                int newRgb = (rgb & 0xFFFFFF00) | blue;
                image.setRGB(x, y, newRgb);

                // Move to next bit
                bitIndex--;
                if (bitIndex < 0) {
                    bitIndex = 7;
                    dataIndex++;
                }
            }
        }
        return image;
    }

    private static int extractLength(BufferedImage image) {
        int length = 0;
        for (int i = 0; i < 32; i++) { // Read first 32 pixels
            int x = i % image.getWidth();
            int y = i / image.getWidth();
            int rgb = image.getRGB(x, y);
            int bit = rgb & 1; // Get LSB
            length = (length << 1) | bit;
        }
        return length;
    }

    private static byte[] extractBytes(BufferedImage image, int length, int offsetPixels) {
        byte[] data = new byte[length];
        int pixelIndex = offsetPixels;
        
        for (int i = 0; i < length; i++) {
            for (int b = 7; b >= 0; b--) {
                int x = pixelIndex % image.getWidth();
                int y = pixelIndex / image.getWidth();
                int rgb = image.getRGB(x, y);
                int bit = rgb & 1;
                
                data[i] = (byte) ((data[i] << 1) | bit); // Shift and add bit
                pixelIndex++;
            }
        }
        return data;
    }
}