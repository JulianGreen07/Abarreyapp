package com.abarreyapp;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Instala el Look and Feel FlatLaf Light por defecto
            FlatLightLaf.setup();
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
