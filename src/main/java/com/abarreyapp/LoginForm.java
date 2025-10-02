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
import com.formdev.flatlaf.FlatLightLaf;

public class LoginForm extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<com.abarreyapp.model.Branch> cmbSucursal;
    private JButton btnIngresar;

    public LoginForm() {
    // Depuración: registrar creación
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

    // Cambio de tema eliminado; la aplicación usa solo tema claro
        FlatLightLaf.setup();


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

    // poblar la lista de sucursales desde la BD (tabla branches)
        java.util.List<com.abarreyapp.model.Branch> branches = new java.util.ArrayList<>();
        try {
            com.abarreyapp.dao.BranchDAO bdao = new com.abarreyapp.dao.BranchDAO();
            branches = bdao.findAll();
        } catch (Exception ex) {
            // fallback a valores por defecto
            branches.add(new com.abarreyapp.model.Branch(1, "Sucursal Central"));
            branches.add(new com.abarreyapp.model.Branch(2, "Sucursal Norte"));
            branches.add(new com.abarreyapp.model.Branch(3, "Sucursal Sur"));
        }
    cmbSucursal = new JComboBox<com.abarreyapp.model.Branch>(branches.toArray(new com.abarreyapp.model.Branch[0]));
        cmbSucursal.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 6;
        rightPanel.add(cmbSucursal, gbc);

    btnIngresar = new RoundedButton("Ingresar");
    btnIngresar.setFont(new Font("Arial", Font.BOLD, 16));
    btnIngresar.setBackground(new Color(40,167,69)); // green
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
                Object sel = cmbSucursal.getSelectedItem();

                if (sel == null) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Por favor, seleccione una sucursal.", "Entrada no válida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Authenticate via UserDAO within the selected branch
                try {
                    if (sel instanceof com.abarreyapp.model.Branch) {
                        com.abarreyapp.model.Branch b = (com.abarreyapp.model.Branch) sel;
                        com.abarreyapp.db.BranchContext.setCurrentBranchId(b.getId());
                    }
                    com.abarreyapp.dao.UserDAO udao = new com.abarreyapp.dao.UserDAO();
                    com.abarreyapp.model.User u = udao.findByName(usuario);
                    if (u != null && u.getRole() != null && u.getRole().toLowerCase().contains("admin")) {
                        // verify password: if password_hash exists verify, otherwise allow legacy '1234' and migrate
                        String stored = u.getPasswordHash();
                        boolean ok = false;
                        if (stored != null && !stored.isEmpty()) {
                            ok = com.abarreyapp.util.PasswordUtil.verify(contrasena.toCharArray(), stored);
                        } else {
                            // legacy password acceptance for migration
                            if (contrasena.equals("1234")) {
                                ok = true;
                                try {
                                    String newHash = com.abarreyapp.util.PasswordUtil.hash(contrasena.toCharArray());
                                    udao.updatePasswordHash(u.getId(), newHash);
                                } catch (Exception ex) {
                                    // ignore migration failure
                                }
                            }
                        }

                        if (ok) {
                            dispose();
                            MainFrame mf = new MainFrame(u);
                            mf.setVisible(true);
                            mf.toFront();
                            mf.requestFocus();
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, "Credenciales incorrectas o usuario no administrador en esta sucursal.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this, "Credenciales incorrectas o usuario no administrador en esta sucursal.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Error al autenticar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    // Forzar la ventana al frente brevemente en caso de que se abra detrás de otras ventanas
        SwingUtilities.invokeLater(() -> {
            try {
                // asegurar visible y al frente
                setVisible(true);
                toFront();
                setAlwaysOnTop(true);
                // registrar en log
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("app_start.log", true))) {
                    pw.println("LoginForm shown at: " + java.time.LocalDateTime.now());
                } catch (Exception ex) {}
                // quitar always-on-top poco después
                new javax.swing.Timer(300, ev -> setAlwaysOnTop(false)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
