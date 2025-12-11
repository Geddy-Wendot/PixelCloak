package com.pixelcloak.app;

import javax.swing.SwingUtilities;
import com.pixelcloak.ui.MainFrame;

public class App {
    static void main() {
        SwingUtilities.invokeLater(()->{
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
