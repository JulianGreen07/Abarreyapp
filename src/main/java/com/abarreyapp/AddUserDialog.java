package com.abarreyapp;

import javax.swing.*;
import java.awt.*;

public class AddUserDialog extends JDialog {
    private JTextField txtNombre, txtRol, txtCorreo, txtTelefono;
    private boolean confirmed = false;

    public AddUserDialog(Frame owner, String title, Object[] data) {
        super(owner, title, true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        fieldsPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        fieldsPanel.add(txtNombre);

        fieldsPanel.add(new JLabel("Rol:"));
        txtRol = new JTextField();
        fieldsPanel.add(txtRol);

        fieldsPanel.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        fieldsPanel.add(txtCorreo);

        fieldsPanel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        fieldsPanel.add(txtTelefono);

        if (data != null) {
            txtNombre.setText((String) data[0]);
            txtRol.setText((String) data[1]);
            txtCorreo.setText((String) data[2]);
            txtTelefono.setText((String) data[3]);
        }

        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirm = new JButton("Confirmar");
        btnConfirm.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        buttonsPanel.add(btnConfirm);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> {
            dispose();
        });
        buttonsPanel.add(btnCancel);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Object[] getUserData() {
        return new Object[]{
            txtNombre.getText(),
            txtRol.getText(),
            txtCorreo.getText(),
            txtTelefono.getText()
        };
    }
}
