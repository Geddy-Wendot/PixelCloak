package com.pixelcloak.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class LoginPanel extends JPanel {

    private final JPasswordField passField;

    // Theme Colors 
    private final Color TEXT_COLOR = new Color(40,40,40);      // Off-white
    private final Color INPUT_BG_COLOR = new Color(33, 37, 43);     // Darker Input BG
    private final Color BORDER_COLOR = new Color(80, 80, 80);       // Subtle Gray
    private final Color ACCENT_COLOR = new Color(97, 175, 239);     // Soft Blue (Focus)
    private final Color PASS_TXT_COLOR = new Color(200, 200, 200); // Light Gray for Password Text
    public LoginPanel(Runnable onLogin) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
        setOpaque(false);

        // 1. The Label
        JLabel passLabel = new JLabel("Encryption Password:");
        passLabel.setForeground(TEXT_COLOR);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); 

        // 2. The Password Field
        passField = new JPasswordField(20);
        passField.setBackground(INPUT_BG_COLOR);
        passField.setForeground(PASS_TXT_COLOR);
        passField.setCaretColor(ACCENT_COLOR);
        passField.setFont(new Font("Monospaced", Font.PLAIN, 14));

        
        passField.setBorder(createModernBorder(BORDER_COLOR));

        // 4. Add Focus Effect (Glow)
        passField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passField.setBorder(createModernBorder(ACCENT_COLOR));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passField.setBorder(createModernBorder(BORDER_COLOR));
            }
        });

        // 5. Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(ACCENT_COLOR);
        loginBtn.setForeground(INPUT_BG_COLOR); // Dark text on light blue
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Trigger login on button click or Enter key in password field
        loginBtn.addActionListener(e -> onLogin.run());
        passField.addActionListener(e -> onLogin.run());

        add(passLabel);
        add(passField);
        add(loginBtn);
    }

    // Helper to create a consistent padded border
    private CompoundBorder createModernBorder(Color color) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(color, 1),
            new EmptyBorder(8, 10, 8, 10) // Internal padding (Top, Left, Bottom, Right)
        );
    }

    public char[] getPassword() {
        return passField.getPassword();
    }

    public void clearPassword() {
        passField.setText("");
    }

    /**
     * Checks if the provided password matches the panic code "1234".
     * @param password The password characters to check
     * @return true if it matches the panic code
     */
    public boolean isDuress(char[] password) {
        return Arrays.equals(password, new char[]{'1', '2', '3', '4'});
    }
}