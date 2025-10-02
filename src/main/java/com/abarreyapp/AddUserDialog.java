package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AddUserDialog extends JDialog {
    private JTextField txtNombre, txtCorreo, txtTelefono;
    private JComboBox<String> cmbRol;
    private boolean confirmed = false;

    public AddUserDialog(Frame owner, String title, Object[] data) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 420);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

    // Encabezado: título + subtítulo
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = new JLabel("Agregar Nuevo Usuario");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(titleLbl, BorderLayout.NORTH);
        JLabel sub = new JLabel("Complete los campos para agregar un nuevo usuario al sistema.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.DARK_GRAY);
        header.add(sub, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

    // Área de campos
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 6, 6, 6);
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST;
        JLabel lblNombre = new JLabel("Nombre Completo");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fields.add(lblNombre, g);

        g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        txtNombre = new JTextField();
        addPlaceholder(txtNombre, "Ingrese el nombre completo");
        fields.add(txtNombre, g);

        g.gridy++; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        JLabel lblRol = new JLabel("Rol");
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fields.add(lblRol, g);

        g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        package com.abarreyapp;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.FocusAdapter;
        import java.awt.event.FocusEvent;

        public class AddUserDialog extends JDialog {
            private JTextField txtNombre, txtCorreo, txtTelefono;
            private JComboBox<String> cmbRol;
            private boolean confirmed = false;

            public AddUserDialog(Frame owner, String title, Object[] data) {
                super(owner, title, true);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                setSize(460, 420);
                setResizable(false);
                setLocationRelativeTo(owner);

                JPanel content = new JPanel(new BorderLayout());
                content.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

                // Encabezado: título + subtítulo
                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                JLabel titleLbl = new JLabel("Agregar Nuevo Usuario");
                titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
                header.add(titleLbl, BorderLayout.NORTH);
                JLabel sub = new JLabel("Complete los campos para agregar un nuevo usuario al sistema.");
                sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                sub.setForeground(Color.DARK_GRAY);
                header.add(sub, BorderLayout.SOUTH);
                content.add(header, BorderLayout.NORTH);

                // Área de campos
                JPanel fields = new JPanel(new GridBagLayout());
                fields.setOpaque(false);
                GridBagConstraints g = new GridBagConstraints();
                g.insets = new Insets(10, 6, 6, 6);
                g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST;
                JLabel lblNombre = new JLabel("Nombre Completo");
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fields.add(lblNombre, g);

                g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
                txtNombre = new JTextField();
                addPlaceholder(txtNombre, "Ingrese el nombre completo");
                fields.add(txtNombre, g);

                g.gridy++; g.fill = GridBagConstraints.NONE; g.weightx = 0;
                JLabel lblRol = new JLabel("Rol");
                lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fields.add(lblRol, g);

                g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
                cmbRol = new JComboBox<>(new String[]{"Seleccione un rol", "Administrador", "Gerente", "Usuario"});
                fields.add(cmbRol, g);

                g.gridy++; g.fill = GridBagConstraints.NONE; g.weightx = 0;
                JLabel lblCorreo = new JLabel("Correo Electrónico");
                lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fields.add(lblCorreo, g);

                g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
                txtCorreo = new JTextField();
                addPlaceholder(txtCorreo, "ejemplo@gmail.com");
                fields.add(txtCorreo, g);

                g.gridy++; g.fill = GridBagConstraints.NONE; g.weightx = 0;
                JLabel lblTel = new JLabel("Teléfono");
                lblTel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fields.add(lblTel, g);

                g.gridy++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
                txtTelefono = new JTextField();
                addPlaceholder(txtTelefono, "662123456");
                fields.add(txtTelefono, g);

                content.add(fields, BorderLayout.CENTER);

                // Botones
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
                JButton btnCancel = new JButton("Cancelar");
                btnCancel.setBackground(Color.WHITE);
                btnCancel.addActionListener(e -> dispose());

                JButton btnConfirm = new JButton("Agregar Usuario");
                btnConfirm.setBackground(new Color(33, 150, 83));
                btnConfirm.setForeground(Color.WHITE);
                btnConfirm.setFocusPainted(false);
                btnConfirm.addActionListener(e -> onConfirm());

                btns.add(btnCancel);
                btns.add(btnConfirm);
                content.add(btns, BorderLayout.SOUTH);

                // poblar datos al editar
                if (data != null) {
                    txtNombre.setText((String) data[0]);
                    cmbRol.setSelectedItem(data[1] != null ? data[1].toString() : "Seleccione un rol");
                    txtCorreo.setText((String) data[2]);
                    txtTelefono.setText((String) data[3]);
                }

                setContentPane(content);
            }

            private void onConfirm() {
                String nombre = txtNombre.getText().trim();
                String rol = cmbRol.getSelectedItem() == null ? "" : cmbRol.getSelectedItem().toString();
                String correo = txtCorreo.getText().trim();
                String telefono = txtTelefono.getText().trim();

                if (nombre.isEmpty() || rol.equals("Seleccione un rol") || rol.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y Rol son obligatorios.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    JOptionPane.showMessageDialog(this, "Introduce un correo válido.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!telefono.isEmpty() && !telefono.matches("^\\d{7,}$")) {
                    JOptionPane.showMessageDialog(this, "Introduce un teléfono válido (solo dígitos, mínimo 7).", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                confirmed = true;
                dispose();
            }

            private void addPlaceholder(JTextField field, String placeholder) {
                field.setForeground(Color.GRAY);
                field.setText(placeholder);
                field.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (field.getText().equals(placeholder)) {
                            field.setText("");
                            field.setForeground(Color.BLACK);
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (field.getText().trim().isEmpty()) {
                            field.setForeground(Color.GRAY);
                            field.setText(placeholder);
                        }
                    }
                });
            }

            public boolean isConfirmed() {
                package com.abarreyapp;

                import javax.swing.*;
                import java.awt.*;

                public class AddUserDialog extends JDialog {
                    private JTextField usernameField;
                    private JPasswordField passwordField;
                    private boolean confirmed = false;

                    public AddUserDialog(JFrame parent) {
                        super(parent, "Añadir Usuario", true);
                        setLayout(new BorderLayout(10,10));
                        JPanel form = new JPanel(new GridLayout(0,2,10,10));
                        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                        form.add(new JLabel("Nombre de usuario:"));
                        usernameField = new JTextField();
                        form.add(usernameField);
                        form.add(new JLabel("Contraseña:"));
                        passwordField = new JPasswordField();
                        form.add(passwordField);
                        add(form, BorderLayout.CENTER);

                        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        JButton ok = new JButton("Guardar");
                        ok.addActionListener(e -> {
                            if (usernameField.getText().trim().isEmpty()) {
                                JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            if (passwordField.getPassword().length == 0) {
                                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            confirmed = true;
                            setVisible(false);
                        });
                        buttons.add(ok);
                        JButton cancel = new JButton("Cancelar");
                        cancel.addActionListener(e -> { confirmed = false; setVisible(false); });
                        buttons.add(cancel);
                        add(buttons, BorderLayout.SOUTH);
                        pack();
                        setLocationRelativeTo(parent);
                    }

                    public boolean isConfirmed() { return confirmed; }

                    public String getUsername() { return usernameField.getText().trim(); }

                    public char[] getPassword() { return passwordField.getPassword(); }
                }

