package java.com.securevent.core;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageAnalyzer {

    public static AnalysisResult analyze(BufferedImage image) {
        double entropy = calculateEntropy(image);
        // An entropy > 4.5 usually means the image is complex enough to hide noise
        boolean isSafe = entropy > 4.5;
        String msg = isSafe ? "Safe for steganography" : "Image too simple (Risk of detection)";
        
        return new AnalysisResult(entropy, isSafe, msg);
    }

    private static double calculateEntropy(BufferedImage image) {
        Map<Integer, Integer> counts = new HashMap<>();
        int w = image.getWidth();
        int h = image.getHeight();
        int total = w * h;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgb = image.getRGB(i, j);
                // Convert to Grayscale for simple complexity check
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                
                counts.put(gray, counts.getOrDefault(gray, 0) + 1);
            }
        }

        double entropy = 0.0;
        for (int count : counts.values()) {
            double p = (double) count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }

    public record AnalysisResult(double entropy, boolean isSafe, String message) {}
}