package com.pixelcloak.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ImageAnalyzer {

    public static boolean isImageSafe(File imageFile) {
        try {
            File scriptFile = new File("scripts", "analyze_image.py");
            if (!scriptFile.exists()) {
                System.err.println("JAVA ERROR: Script not found at " + scriptFile.getAbsolutePath());
                return false;
            }

            // --- CRITICAL FIX: Use the full path you found earlier ---
            // Replace this string with YOUR specific path: "C:\\Python313\\python.exe"
            String pythonPath = "C:\\Python313\\python.exe";

            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath(), imageFile.getAbsolutePath());
            pb.redirectErrorStream(true); // Merges errors so we can read them

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            // Read all lines if there are multiple (useful for debugging errors)
            StringBuilder fullOutput = new StringBuilder();
            if (line != null) fullOutput.append(line);

            while (reader.ready()) {
                fullOutput.append("\n").append(reader.readLine());
            }

            process.waitFor();

            // DEBUGGING: Print exactly what Python said
            System.out.println("PYTHON SAID: " + fullOutput.toString());

            if (line != null && line.startsWith("SAFE")) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

        // to return entropy score
        public static double getEntropyScore(File imageFile) {
            try{
                File scriptFile = new File( "scripts",  "analyze_image.py");
                
                if (!scriptFile.exists()) {
                    System.err.println("JAVA ERROR: Script not found at " + scriptFile.getAbsolutePath());
                    return -1.0;
                }

                String pythonPath = "C:\\Python313\\python.exe";

                ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath(), imageFile.getAbsolutePath());
                pb.redirectErrorStream(true);

                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine(); 
                process.waitFor();      
                
                
                if (line != null && line.contains("|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length > 1) {
                        return Double.parseDouble(parts[1]);
                    }
                }
                return -1.0;            
            }catch (Exception e){
                e.printStackTrace();
                return -1.0;
            }   
        }

        
}