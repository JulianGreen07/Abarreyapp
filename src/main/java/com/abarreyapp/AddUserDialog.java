package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AddUserDialog extends JDialog {
    private JTextField txtNombre, txtCorreo, txtTelefono;
    private JComboBox<String> cmbRol;
    private boolean confirmed = false;
    private boolean deleted = false;

    public AddUserDialog(Frame owner, String title, Object[] data) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // slightly wider dialog and fixed height for better spacing
    setSize(540, 420);
        setResizable(false);
        setLocationRelativeTo(owner);

        boolean isEdit = data != null;
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

    // Encabezado: título + subtítulo
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = new JLabel(isEdit ? "Editar Usuario" : "Agregar Nuevo Usuario");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(titleLbl, BorderLayout.NORTH);
    JLabel sub = new JLabel(isEdit ? "Modifique los campos del usuario y pulse Guardar Cambios." : "Complete los campos para agregar un nuevo usuario al sistema.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.DARK_GRAY);
    header.add(sub, BorderLayout.SOUTH);
    // añadir espacio debajo del encabezado para separar visualmente de los campos
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
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
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        // Left: delete (only when editing)
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        JButton btnDelete = new JButton("Eliminar");
        // load trash icon from resources
        try {
            java.net.URL iconUrl = getClass().getResource("/icons/waste.png");
            if (iconUrl != null) {
                ImageIcon ic = new ImageIcon(iconUrl);
                Image scaled = ic.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btnDelete.setIcon(new ImageIcon(scaled));
                btnDelete.setHorizontalTextPosition(SwingConstants.RIGHT);
                btnDelete.setIconTextGap(8);
            }
        } catch (Throwable ex) { /* ignore icon errors */ }
    btnDelete.setBackground(new Color(220, 53, 69)); // red
    btnDelete.setForeground(Color.WHITE);
    btnDelete.setFocusPainted(false);
    btnDelete.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
    // ensure the delete button is wide enough so text doesn't truncate
    btnDelete.setPreferredSize(new Dimension(140, 36));
    btnDelete.setOpaque(true);
    btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    btnDelete.setIconTextGap(10);
        btnDelete.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Eliminar este usuario? Esta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                deleted = true;
                dispose();
            }
        });
        if (isEdit) leftPanel.add(btnDelete);

        // Right: confirm button only. Place Cancel in the center to match visual layout
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());
    btnCancel.setPreferredSize(new Dimension(110, 36));
    btnCancel.setOpaque(true);
    btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        rightPanel.setOpaque(false);
        JButton btnConfirm = new JButton(isEdit ? "Guardar Cambios" : "Agregar Usuario");
        btnConfirm.setBackground(new Color(33, 150, 83));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.addActionListener(e -> onConfirm());
    btnConfirm.setPreferredSize(new Dimension(150, 36));
    btnConfirm.setOpaque(true);
    btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnCancel.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        btnConfirm.setBorder(BorderFactory.createEmptyBorder(8,14,8,14));

        // Install hover/shadow effect
        installHoverEffect(btnCancel, Color.WHITE, Color.LIGHT_GRAY);
        installHoverEffect(btnConfirm, new Color(33,150,83), new Color(28,130,70));
        installHoverEffect(btnDelete, new Color(220,53,69), new Color(200,40,60));
        centerPanel.add(btnCancel);
        rightPanel.add(btnConfirm);

        bottom.add(leftPanel, BorderLayout.WEST);
        bottom.add(centerPanel, BorderLayout.CENTER);
        bottom.add(rightPanel, BorderLayout.EAST);
    bottom.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        content.add(bottom, BorderLayout.SOUTH);

        // poblar datos al editar
        if (data != null) {
            try {
                String v = data[0] == null ? "" : data[0].toString();
                txtNombre.setText(v);
                if (!v.trim().isEmpty()) {
                    txtNombre.setForeground(Color.BLACK);
                    txtNombre.setCaretPosition(txtNombre.getText().length());
                    txtNombre.select(0, 0);
                }
            } catch (Exception ex) { txtNombre.setText(""); }
            try {
                String r = data[1] == null ? "Seleccione un rol" : data[1].toString();
                cmbRol.setSelectedItem(r);
            } catch (Exception ex) { cmbRol.setSelectedItem("Seleccione un rol"); }
            try {
                String e = data[2] == null ? "" : data[2].toString();
                txtCorreo.setText(e);
                if (!e.trim().isEmpty()) {
                    txtCorreo.setForeground(Color.BLACK);
                    txtCorreo.setCaretPosition(txtCorreo.getText().length());
                    txtCorreo.select(0, 0);
                }
            } catch (Exception ex) { txtCorreo.setText(""); }
            try {
                String t = data[3] == null ? "" : data[3].toString();
                txtTelefono.setText(t);
                if (!t.trim().isEmpty()) {
                    txtTelefono.setForeground(Color.BLACK);
                    txtTelefono.setCaretPosition(txtTelefono.getText().length());
                    txtTelefono.select(0, 0);
                }
            } catch (Exception ex) { txtTelefono.setText(""); }
        }

        System.out.println("AddUserDialog: opened (isEdit=" + isEdit + ") with name='" + txtNombre.getText() + "'");

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
        return confirmed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    private void installHoverEffect(final JButton b, final Color base, final Color hover) {
        final Color origBg = b.getBackground();
    final javax.swing.border.Border origBorder = b.getBorder();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                try {
                    b.setBackground(hover);
                    b.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.EmptyBorder(2,2,6,2), origBorder));
                    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } catch (Throwable ex) {}
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                try {
                    b.setBackground(origBg);
                    b.setBorder(origBorder);
                    b.setCursor(Cursor.getDefaultCursor());
                } catch (Throwable ex) {}
            }
        });
    }

    public Object[] getUserData() {
        return new Object[] {
            txtNombre.getText().equals("Ingrese el nombre completo") ? "" : txtNombre.getText(),
            cmbRol.getSelectedItem() == null ? "" : cmbRol.getSelectedItem().toString(),
            txtCorreo.getText().equals("ejemplo@gmail.com") ? "" : txtCorreo.getText(),
            txtTelefono.getText().equals("662123456") ? "" : txtTelefono.getText()
        };
    }
}
