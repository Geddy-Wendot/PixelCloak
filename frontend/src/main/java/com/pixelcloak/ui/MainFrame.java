package com.pixelcloak.ui;

import javax.swing.*;

public class MainFrame extends JFrame{
    public MainFrame(){
        setTitle("PixelCloak -My little secret");
        setLocationRelativeTo(null);
        setSize(700,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JournalPanel());
    }

}