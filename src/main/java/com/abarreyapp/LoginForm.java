package com.abarreyapp;

import com.abarreyapp.ui.RoundedButton;
import com.abarreyapp.ui.RoundedPasswordField;
import com.abarreyapp.ui.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.imageio.ImageIO;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class LoginForm extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbSucursal;
    private JButton btnIngresar;

    public LoginForm() {
        // Debug: log creation
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("app_start.log", true))) {
            pw.println("LoginForm created at: " + java.time.LocalDateTime.now());
        } catch (Exception ex) {}
        setTitle("Inicio de Sesión - Abarrey");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL imageUrl = getClass().getResource("/login_background.png");
                    if (imageUrl != null) {
                        Image bg = ImageIO.read(imageUrl);
                        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        leftPanel.setPreferredSize(new Dimension(400, 500));
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel headerPanel = new JPanel(new BorderLayout());

        JToggleButton themeToggleButton = new JToggleButton("Modo Oscuro");
        themeToggleButton.setSelected(UIManager.getLookAndFeel() instanceof FlatDarkLaf);
        themeToggleButton.addActionListener(e -> {
            try {
                if (themeToggleButton.isSelected()) {
                    FlatDarkLaf.setup();
                    themeToggleButton.setText("Modo Claro");
                } else {
                    FlatLightLaf.setup();
                    themeToggleButton.setText("Modo Oscuro");
                }
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        togglePanel.add(themeToggleButton);
        headerPanel.add(togglePanel, BorderLayout.NORTH);


        JLabel lblBienvenido = new JLabel("BIENVENIDO");
        lblBienvenido.setFont(new Font("Arial", Font.BOLD, 24));
        lblBienvenido.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblBienvenido, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 40, 20);
        rightPanel.add(headerPanel, gbc);


        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        rightPanel.add(lblUsuario, gbc);

        txtUsuario = new RoundedTextField(20);
        gbc.gridy = 2;
        rightPanel.add(txtUsuario, gbc);

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        rightPanel.add(lblContrasena, gbc);

        txtContrasena = new RoundedPasswordField(20);
        gbc.gridy = 4;
        rightPanel.add(txtContrasena, gbc);

        JLabel lblSucursal = new JLabel("Sucursal");
        lblSucursal.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 5;
        rightPanel.add(lblSucursal, gbc);

        String[] sucursales = {"Sucursal Central", "Sucursal Norte", "Sucursal Sur"};
        cmbSucursal = new JComboBox<>(sucursales);
        cmbSucursal.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 6;
        rightPanel.add(cmbSucursal, gbc);

        btnIngresar = new RoundedButton("Ingresar");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 16));
        btnIngresar.setBackground(new Color(0, 123, 255));
        btnIngresar.setForeground(Color.WHITE);
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 20, 20, 20);
        rightPanel.add(btnIngresar, gbc);
        
        add(rightPanel, BorderLayout.CENTER);

        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String contrasena = new String(txtContrasena.getPassword());
                String sucursal = (String) cmbSucursal.getSelectedItem();

                if (sucursal == null || sucursal.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Por favor, seleccione una sucursal.", "Entrada no válida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (usuario.equalsIgnoreCase("admin") && contrasena.equals("1234") && sucursal.equalsIgnoreCase("Sucursal Central")) {
                    dispose();
                    MainFrame mf = new MainFrame(usuario);
                    mf.setVisible(true);
                    mf.toFront();
                    mf.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Force window to front briefly in case it opens behind other windows
        SwingUtilities.invokeLater(() -> {
            try {
                // ensure visible and front
                setVisible(true);
                toFront();
                setAlwaysOnTop(true);
                // log
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("app_start.log", true))) {
                    pw.println("LoginForm shown at: " + java.time.LocalDateTime.now());
                } catch (Exception ex) {}
                // remove always on top shortly after
                new javax.swing.Timer(300, ev -> setAlwaysOnTop(false)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
