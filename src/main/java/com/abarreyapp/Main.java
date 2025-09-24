package com.abarreyapp;

// FlatLaf removed to avoid compile-time dependency when building without Maven

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Use system look and feel as a safe default when FlatLaf isn't available
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
