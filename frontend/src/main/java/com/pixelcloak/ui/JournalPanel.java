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

public class JournalPanel extends JPanel{
    // UI components
    private JTextArea textArea;
    private final JPasswordField passField;
    private final JLabel statusLabel;
    private final JButton loadBtn, saveBtn, revealBtn;
    private final ImagePanel ImagePreviewPanel;

    //logic data
    private BufferedImage currentImage;
    private File currentFile;

    // Theme Colors
    final Color BG_COLOR = new Color(40, 44, 52); // Dark Slate
    final Color TEXT_COLOR = new Color(220, 223, 228); // Off-white
    final Color ACCENT_COLOR = new Color(97, 175, 239); // Soft Blue
    final Color SUCCESS_COLOR = new Color(152, 195, 121); // Soft Green
    final Color ERROR_COLOR = new Color(224, 108, 117); // Soft Red

    public JournalPanel(){
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        //setBorder(new EmptyBorder(20,20,20,20));

        //header
        JPanel PanelHeader= new JPanel(new FlowLayout(FlowLayout.LEFT));
        PanelHeader.setOpaque(false);
        JLabel title = new JLabel("PixelCloak");
        title.setFont(new Font("Segue UI",Font.BOLD, 24));
        title.setForeground(ACCENT_COLOR);
        PanelHeader.add(title);
        add(PanelHeader, BorderLayout.NORTH);

        //body. split it into two
        //left(text)
        JTextArea TextArea = new JTextArea("Write you thoughts here....");
        TextArea.setBackground(new Color(33, 37, 43) );
        TextArea.setForeground(ACCENT_COLOR);
        TextArea.setLineWrap(true);
        TextArea.setWrapStyleWord(true);
        TextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));

        //image preview section
        ImagePreviewPanel = new ImagePanel();
        ImagePreviewPanel.setBackground(new Color(33,37,43));
        ImagePreviewPanel.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));

        //code for the horizontal split that separates the panels.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScroll, ImagePreviewPanel);
        splitPane.setBackground(BG_COLOR);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        //controls
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);

        // password section
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("Encrypting Pass cord");
        passLabel.setForeground(TEXT_COLOR);
        passField = new JPasswordField(20);
        passPanel.add(passLabel);
        passPanel.add(passField);

        //buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,10));
        btnPanel.setOpaque(false);

        loadBtn = createStyledButton("Load Image");
        saveBtn = createStyledButton("Hide and Save");
        revealBtn = createStyledButton("Reveal Text");

        btnPanel.add(loadBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(revealBtn);

        //status bar
        statusLabel = new JLabel("Ready to load an Image and start");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10,0,0,0));

        bottomContainer.add(passPanel);
        bottomContainer.add(btnPanel);
        bottomContainer.add(statusLabel);

        add(bottomContainer, BorderLayout.SOUTH);

        //event listeners
        loadBtn.addActionListener(e -> loadImage());
        saveBtn.addActionListener(e -> hideAndSave());
        revealBtn.addActionListener(e -> revealText());

    }
    // the main logic
    private void setStatus(String msg,Color color ){
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
    // loadImage logic
    private void loadImage(){
        JFileChooser chooser= new JFileChooser();
        if(chooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION){
            try {
                currentFile = chooser.getSelectedFile();
                currentImage = ImageIO.read(currentFile);

                //to update the image preview section
                ImagePreviewPanel.setImage(currentImage);
                ImagePreviewPanel.repaint();

                setStatus("Image Loaded: "+ currentFile.getName(), SUCCESS_COLOR);
            }catch (Exception e){
                setStatus("Error Loading Image ", ERROR_COLOR);
            }
        }
    }

    //hide and save logic
    private void hideAndSave(){
        //this action makes it run in the background to avoid it from stopping the UI
        new SwingWorker<File, Void>(){
            protected File doInBackground() throws Exception{
                if(currentImage == null){
                    throw new IllegalStateException("Please load and image");
                }
                String text = textArea.getText();
                char[] passwordChar = passField.getPassword();

                if(text.isEmpty()|| passwordChar.length==0){
                    throw new IllegalStateException("Text and Password needed");
                }
                //to prevent data from being lost due to overflow
                long maxBytes = ((long) currentImage.getWidth()*currentImage.getHeight()*3/8)-4;
                if(text.getBytes(StandardCharsets.UTF_8).length>maxBytes){
                    throw new IllegalStateException("Text too long for image uploaded");
                }
                setStatus("Encryption and Embedding.", ACCENT_COLOR);
                String encrypted = AESCrypto.encrypt(text, new String(passwordChar));
                java.util.Arrays.fill(passwordChar, '');

                BufferedImage stegImage = Steganography.embed(currentImage, encrypted);
                JFileChooser chooser = new JFileChooser();
                if(chooser.showSaveDialog(JournalPanel.this)== JFileChooser.APPROVE_OPTION){
                    File output = chooser.getSelectedFile();
                    if(!output.getName().toLowerCase().endsWith("png")){
                        output = new File(output.getParent(),output.getName() + ".png");
                    }
                    ImageIO.write(stegImage, "png", output);
                    return output;
                }
                return null;
            }

            protected  void done(){
                try{
                    File savedFile = get();
                    if(savedFile != null){
                        setStatus("Success, file saved to: "+ savedFile.getName(), SUCCESS_COLOR);

                    }else {
                        setStatus("Save Canceled", Color.GRAY);
                    }
                }catch (Exception e){
                    String message = e.getCause()!=null ? e.getCause().getMessage(): e.getMessage();
                    setStatus("Error "+message, ERROR_COLOR);
                }
            }
        }.execute();
    }

    // reveal logic
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

                setStatus("Extracting and decrypting...", ACCENT_COLOR);
                String encrypted = Steganography.extract(currentImage);
                if (encrypted == null) {
                    throw new IllegalStateException("No hidden data found or image is corrupt.");
                }

                String decrypted = AESCrypto.decrypt(encrypted, new String(passwordChars));
                java.util.Arrays.fill(passwordChars, ' '); // Clear password from memory
                return decrypted;
            }

            @Override
            protected void done() {
                try {
                    String decryptedText = get();
                    textArea.setText(decryptedText);
                    setStatus("Decryption Successful!", SUCCESS_COLOR);
                } catch (Exception e) {
                    // GCM authentication failure (wrong password) throws AEADBadTagException
                    if (e.getCause() instanceof javax.crypto.AEADBadTagException) {
                        setStatus("Access Denied: Wrong Password or corrupt data.", ERROR_COLOR);
                    } else {
                        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                        setStatus("Error: " + message, ERROR_COLOR);
                    }
                    textArea.setText(""); // Clear text area on failure
                }
            }
        }.execute();
    }

    //method helper for the createStyledButton method
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segue UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }

    class ImagePanel extends JPanel {
        private BufferedImage img;

        public void setImage(BufferedImage img) {
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                // Calculate scaling logic to fit image within panel (Aspect Ratio preserved)
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int panelW = getWidth();
                int panelH = getHeight();
                int imgW = img.getWidth();
                int imgH = img.getHeight();

                double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);
                int newW = (int) (imgW * scale);
                int newH = (int) (imgH * scale);

                // Center the image
                int x = (panelW - newW) / 2;
                int y = (panelH - newH) / 2;

                g2.drawImage(img, x, y, newW, newH, null);
            } else {
                // Draw placeholder text if no image
                g.setColor(Color.GRAY);
                g.drawString("No Image Loaded", getWidth() / 2 - 50, getHeight() / 2);
            }
        }
    }
}

