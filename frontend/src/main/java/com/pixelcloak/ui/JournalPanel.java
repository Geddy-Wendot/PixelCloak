package com.pixelcloak.ui;

import com.pixelcloak.core.AESCrypto; 
import com.pixelcloak.core.Steganography;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JournalPanel extends JPanel {
    // UI components
    private final JTextArea textArea;
    private final JPasswordField passField;
    private final JLabel statusLabel;
    private final ImagePanel ImagePreviewPanel;

    // Logic data
    private BufferedImage currentImage;
    private File currentFile;

    // Theme Colors
    final Color BG_COLOR = new Color(40, 44, 52); // Dark Slate
    final Color TEXT_COLOR = new Color(220, 223, 228); // Off-white
    final Color ACCENT_COLOR = new Color(97, 175, 239); // Soft Blue
    final Color SUCCESS_COLOR = new Color(152, 195, 121); // Soft Green
    final Color ERROR_COLOR = new Color(224, 108, 117); // Soft Red

    @SuppressWarnings("unused")
    public JournalPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);

        // Header
        JPanel PanelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        PanelHeader.setOpaque(false);
        JLabel title = new JLabel("PixelCloak");
        title.setFont(new Font("Segue UI", Font.BOLD, 24));
        title.setForeground(ACCENT_COLOR);
        PanelHeader.add(title);
        add(PanelHeader, BorderLayout.NORTH);

        // Body: Split pane
        // Left (Text Area) -> FIXED: Assigned to class field 'textArea', not local 'TextArea'
        textArea = new JTextArea("Write your thoughts here....");
        textArea.setBackground(new Color(33, 37, 43));
        textArea.setForeground(TEXT_COLOR); // Changed to TEXT_COLOR for better readability
        textArea.setCaretColor(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        // Image preview section
        ImagePreviewPanel = new ImagePanel();
        ImagePreviewPanel.setBackground(new Color(33, 37, 43));
        ImagePreviewPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScroll, ImagePreviewPanel);
        splitPane.setBackground(BG_COLOR);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        // Controls
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);

        // Password section
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("Encryption Password:");
        passLabel.setForeground(TEXT_COLOR);
        passField = new JPasswordField(20);
        passPanel.add(passLabel);
        passPanel.add(passField);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);

        JButton loadBtn = createStyledButton("Load Image");
        JButton saveBtn = createStyledButton("Hide and Save");
        JButton revealBtn = createStyledButton("Reveal Text");

        btnPanel.add(loadBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(revealBtn);

        // Status bar
        statusLabel = new JLabel("Ready to load an Image and start");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        bottomContainer.add(passPanel);
        bottomContainer.add(btnPanel);
        bottomContainer.add(statusLabel);

        add(bottomContainer, BorderLayout.SOUTH);

        // Event listeners
        loadBtn.addActionListener(e -> loadImage());
        saveBtn.addActionListener(e -> hideAndSave());
        revealBtn.addActionListener(e -> revealText());
    }

    // Helper to update status
    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // Load Image Logic
    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = chooser.getSelectedFile();
                currentImage = ImageIO.read(currentFile);

                if (currentImage == null) {
                    throw new Exception("File is not a valid image.");
                }

                // Update the image preview section
                ImagePreviewPanel.setImage(currentImage);
                ImagePreviewPanel.repaint();

                setStatus("Image Loaded: " + currentFile.getName(), SUCCESS_COLOR);
            } catch (Exception e) {
                setStatus("Error Loading Image", ERROR_COLOR);
            }
        }
    }

    // Hide and Save Logic
    private void hideAndSave() {
        new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                if (currentImage == null) {
                    throw new IllegalStateException("Please load an image first.");
                }
                String text = textArea.getText();
                char[] passwordChar = passField.getPassword();

                if (text.isEmpty() || passwordChar.length == 0) {
                    throw new IllegalStateException("Text and Password needed.");
                }

                setStatus("Analyzing Image Complexity...", ACCENT_COLOR);

                // call the python analyzer
                boolean isSafe = com.pixelcloak.core.ImageAnalyzer.isImageSafe(currentFile);

                if (!isSafe){
                    throw new IllegalStateException("Image too simple. Hiding data here is risky");
                }

                // Prevent data overflow
                long maxBytes = ((long) currentImage.getWidth() * currentImage.getHeight() * 3 / 8) - 4;
                if (text.getBytes(StandardCharsets.UTF_8).length > maxBytes) {
                    throw new IllegalStateException("Text too long for image uploaded.");
                }

                //check score

                // 1. Get the actual score
                double score = com.pixelcloak.core.ImageAnalyzer.getEntropyScore(JournalPanel.this.currentFile);

                // 2. Check logic (Threshold 4.5)
                if (score < 4.5) {
                    throw new IllegalStateException("Image too simple (Score: " + String.format("%.2f", score) + "). Needs > 4.5");
                }

                // 3. Show the score to the user!
                setStatus("Analysis Passed! Score: " + String.format("%.2f", score), SUCCESS_COLOR);
                
                // Slight delay so user sees the score before encryption starts
                Thread.sleep(1000); 

            
                setStatus("Encrypting and Embedding...", ACCENT_COLOR);

                // FIXED: Passed both 'text' and 'passwordChar'
                String encrypted = AESCrypto.encrypt(text, passwordChar);

                // Clear password from memory
                Arrays.fill(passwordChar, ' ');

                BufferedImage stegImage = Steganography.embed(currentImage, encrypted);

                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(JournalPanel.this) == JFileChooser.APPROVE_OPTION) {
                    File output = chooser.getSelectedFile();
                    if (!output.getName().toLowerCase().endsWith(".png")) {
                        output = new File(output.getParent(), output.getName() + ".png");
                    }
                    assert stegImage != null;
                    ImageIO.write(stegImage, "png", output);
                    return output;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    File savedFile = get();
                    if (savedFile != null) {
                        setStatus("Success! Saved to: " + savedFile.getName(), SUCCESS_COLOR);
                    } else {
                        setStatus("Save Canceled", Color.GRAY);
                    }
                } catch (Exception e) {
                    String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    setStatus("Error: " + message, ERROR_COLOR);
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Reveal Logic
    private void revealText() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                if (currentImage == null) {
                    throw new IllegalStateException("Please load an image first.");
                }
                char[] passwordChars = passField.getPassword();
                if (passwordChars.length == 0) {
                    throw new IllegalStateException("Password is required.");
                }

                // Duress Protocol Implementation
                if (Arrays.equals(passwordChars, new char[]{'1', '2', '3', '4'})) {
                    Arrays.fill(passwordChars, ' '); // Clear password from memory
                    return "TODO LIST:\n1. Buy Groceries\n2. Call Dentist\n3. Pick up dry cleaning\n4. Email boss about report\n5. Water the plants";
                }

                setStatus("Extracting and decrypting...", ACCENT_COLOR);

                String encrypted = Steganography.extract(currentImage);
                if (encrypted == null) {
                    throw new IllegalStateException("No hidden data found or image is corrupt.");
                }

                // FIXED: Passed both 'encrypted' data and 'passwordChars'
                String decrypted = AESCrypto.decrypt(encrypted, passwordChars);

                Arrays.fill(passwordChars, ' '); // Clear password
                return decrypted;
            }

            @Override
            protected void done() {
                try {
                    String decryptedText = get();
                    textArea.setText(decryptedText);
                    setStatus("Decryption Successful!", SUCCESS_COLOR);
                } catch (Exception e) {
                    if (e.getCause() instanceof javax.crypto.AEADBadTagException) {
                        setStatus("Access Denied: Wrong Password.", ERROR_COLOR);
                    } else {
                        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                        setStatus("Error: " + message, ERROR_COLOR);
                    }
                    textArea.setText("");
                }
            }
        }.execute();
    }

    // Helper for styled buttons
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segue UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }

    // Custom Image Panel
    static class ImagePanel extends JPanel {
        private BufferedImage img;

        public void setImage(BufferedImage img) {
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int panelW = getWidth();
                int panelH = getHeight();
                int imgW = img.getWidth();
                int imgH = img.getHeight();

                double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);
                int newW = (int) (imgW * scale);
                int newH = (int) (imgH * scale);

                int x = (panelW - newW) / 2;
                int y = (panelH - newH) / 2;

                g2.drawImage(img, x, y, newW, newH, null);
            } else {
                g.setColor(Color.GRAY);
                String msg = "No Image Loaded";
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(msg);
                g.drawString(msg, (getWidth() - textWidth) / 2, getHeight() / 2);
            }
        }
    }
}