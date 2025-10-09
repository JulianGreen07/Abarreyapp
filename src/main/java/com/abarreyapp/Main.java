package com.abarreyapp;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Debug: write startup marker
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("app_start.log", true))) {
            pw.println("Main started at: " + java.time.LocalDateTime.now());
        } catch (Exception ex) {
            // ignore
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginForm lf = new LoginForm();
                lf.setVisible(true);
                lf.toFront();
                lf.requestFocus();
            }
        });
    }
}
