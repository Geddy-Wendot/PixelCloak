package com.pixelcloak.core;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public class Steganography {

    // Embeds the message string into the image
    public static BufferedImage embed(BufferedImage image, String message) {
        if (message == null || image == null) return null;

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int len = messageBytes.length;

        // Check if image is big enough: 3 channels (RGB) * Width * Height = Total Bits Available
        // We need 32 bits (header) + (message length * 8) bits
        long requiredBits = 32L + (len * 8L);
        long availableBits = (long) image.getWidth() * image.getHeight() * 3;

        if (requiredBits > availableBits) {
            throw new IllegalArgumentException("Text is too long for this image. Need larger image or shorter text.");
        }

        // 4 bytes for length + message bytes
        byte[] dataToHide = new byte[4 + len];

        // Encode length in first 4 bytes
        dataToHide[0] = (byte) ((len >> 24) & 0xFF);
        dataToHide[1] = (byte) ((len >> 16) & 0xFF);
        dataToHide[2] = (byte) ((len >> 8) & 0xFF);
        dataToHide[3] = (byte) (len & 0xFF);

        System.arraycopy(messageBytes, 0, dataToHide, 4, len);

        return embedBytes(image, dataToHide);
    }

    // Extracts the message string from the image
    public static String extract(BufferedImage image) {
        if (image == null) return null;

        // 1. Extract the length header (first 32 bits / 4 bytes)
        byte[] lengthBytes = extractBytes(image, 4);
        if (lengthBytes == null) return null;

        int len = ((lengthBytes[0] & 0xFF) << 24) |
                ((lengthBytes[1] & 0xFF) << 16) |
                ((lengthBytes[2] & 0xFF) << 8) |
                (lengthBytes[3] & 0xFF);

        // Safety check: Don't try to allocate massive arrays if reading garbage
        if (len < 0 || len > image.getWidth() * image.getHeight() * 3 / 8) {
            // This usually happens if you try to "reveal" an image that has no secret data
            return null;
        }

        // 2. Extract the actual message content
        // We read (4 + len) bytes to keep offsets simple, then substring the result
        byte[] allData = extractBytes(image, 4 + len);
        if (allData == null) return null;

        return new String(allData, 4, len, StandardCharsets.UTF_8);
    }

    private static BufferedImage embedBytes(BufferedImage image, byte[] data) {
        int width = image.getWidth();
        int height = image.getHeight();

        int dataIndex = 0;
        int bitIndex = 0; // 0 to 7

        // Use a copy of the image to avoid modifying the original UI reference unexpectedly
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Note: For simplicity in this specific project, we can modify in place,
        // but creating a copy is often safer for "Undo" functionality.
        // If you prefer modifying the original, just remove the line above and 'Graphics' copy below.

        java.awt.Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (dataIndex >= data.length) return newImage;

                int pixel = newImage.getRGB(x, y);

                // Extract channels
                int red   = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue  = pixel & 0xFF;

                // Embed in Red, then Green, then Blue
                for (int i = 0; i < 3; i++) {
                    if (dataIndex < data.length) {
                        // Get the specific bit from our data byte (MSB first)
                        int bit = (data[dataIndex] >> (7 - bitIndex)) & 1;

                        // Modify the LSB of the color channel
                        if (i == 0) red   = (red   & 0xFE) | bit;
                        if (i == 1) green = (green & 0xFE) | bit;
                        if (i == 2) blue  = (blue  & 0xFE) | bit;

                        bitIndex++;
                        if (bitIndex == 8) {
                            bitIndex = 0;
                            dataIndex++;
                        }
                    }
                }

                // Pack colors back into pixel (Alpha is forced to 255/opaque for RGB images)
                int newPixel = (0xFF << 24) | (red << 16) | (green << 8) | blue;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }

    private static byte[] extractBytes(BufferedImage image, int lengthToRead) {
        byte[] data = new byte[lengthToRead];
        int width = image.getWidth();
        int height = image.getHeight();

        int dataIndex = 0;
        int bitIndex = 0;
        int currentByte = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (dataIndex >= lengthToRead) return data;

                int pixel = image.getRGB(x, y);
                int red   = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue  = pixel & 0xFF;

                int[] channels = {red, green, blue};

                for (int i = 0; i < 3; i++) {
                    if (dataIndex < lengthToRead) {
                        // Extract LSB
                        int bit = channels[i] & 1;

                        // Shift current byte and add new bit
                        currentByte = (currentByte << 1) | bit;
                        bitIndex++;

                        if (bitIndex == 8) {
                            data[dataIndex] = (byte) currentByte;
                            dataIndex++;
                            bitIndex = 0;
                            currentByte = 0;
                        }
                    }
                }
            }
        }
        return data;
    }
}