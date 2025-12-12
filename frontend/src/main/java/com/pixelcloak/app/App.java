package com.pixelcloak.app;

import javax.swing.SwingUtilities;
import com.pixelcloak.ui.MainFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
