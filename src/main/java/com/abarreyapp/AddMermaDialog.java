package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMermaDialog extends JDialog {

    private JComboBox<String> productoComboBox;
    private JTextField cantidadField;
    private JFormattedTextField fechaField;
    private boolean confirmed = false;
    private boolean deleted = false;

    private String[] availableFruitsVegetables = {
        "Manzana", "Plátano", "Lechuga", "Tomate", "Zanahoria", "Brócoli",
        "Naranja", "Apio", "Pepino", "Pimiento", "Cebolla", "Papa",
        "Limón", "Aguacate", "Espinaca", "Coliflor"
    };

    public AddMermaDialog(Frame owner) {
        this(owner, (Object[]) null);
    }

    /**
     * Constructor que acepta datos para modo edición. rowData layout: {producto, cantidad, motivo, fecha, ""}
     */
    public AddMermaDialog(Frame owner, Object[] data) {
        super(owner, data == null ? "Registrar Merma" : "Editar Registro de Merma", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Producto:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        DefaultComboBoxModel<String> mm = new DefaultComboBoxModel<>();
        try {
            com.abarreyapp.dao.ProductDAO pdao = new com.abarreyapp.dao.ProductDAO();
            for (String[] r : pdao.findAll()) mm.addElement(r[1]);
            if (mm.getSize() == 0) for (String s : availableFruitsVegetables) mm.addElement(s);
        } catch (Exception ex) {
            for (String s : availableFruitsVegetables) mm.addElement(s);
        }
        productoComboBox = new JComboBox<>(mm);
        formPanel.add(productoComboBox, gbc);

        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Cantidad (kg):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cantidadField = new JTextField(10);
        formPanel.add(cantidadField, gbc);

        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Fecha:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fechaField = new JFormattedTextField(sdf);
        fechaField.setValue(new Date());
        formPanel.add(fechaField, gbc);

        add(formPanel, BorderLayout.CENTER);
        // Botones: left=Eliminar (solo en edición), center=Cancelar, right=Guardar
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(new Dimension(120, 36));
        btnDelete.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Eliminar este registro de merma? Esta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                deleted = true;
                setVisible(false);
            }
        });

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.addActionListener(e -> { confirmed = false; setVisible(false); });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        rightPanel.setOpaque(false);
        JButton btnGuardar = new JButton(data == null ? "Guardar" : "Guardar Cambios");
        btnGuardar.setPreferredSize(new Dimension(140, 36));
        btnGuardar.setBackground(new Color(33,150,83));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                setVisible(false);
            }
        });

        centerPanel.add(btnCancelar);
        rightPanel.add(btnGuardar);

        if (data != null) leftPanel.add(btnDelete);
        bottom.add(leftPanel, BorderLayout.WEST);
        bottom.add(centerPanel, BorderLayout.CENTER);
        bottom.add(rightPanel, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        // Si vienen datos, poblar campos (modo edición)
        if (data != null) {
            try {
                // producto no modificable
                String producto = data[0] == null ? "" : data[0].toString();
                productoComboBox.setSelectedItem(producto);
                productoComboBox.setEnabled(false);
            } catch (Exception ex) { /* ignore */ }
            try {
                String cantidad = data[1] == null ? "" : data[1].toString();
                cantidadField.setText(cantidad);
            } catch (Exception ex) { cantidadField.setText(""); }
            try {
                String fechaStr = data[3] == null ? null : data[3].toString();
                if (fechaStr != null && !fechaStr.isEmpty()) {
                    try {
                        fechaField.setValue(new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr));
                    } catch (Exception pe) {
                        // keep default
                    }
                }
            } catch (Exception ex) { /* ignore */ }
        }
    }

    private boolean validateInput() {
        if (productoComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String cantidadStr = cantidadField.getText().trim();
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo de cantidad es requerido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número mayor a 0.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (fechaField.getValue() == null) {
            JOptionPane.showMessageDialog(this, "La fecha es requerida.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Object[] getMermaData() {
        String producto = (String) productoComboBox.getSelectedItem();
        String cantidad = cantidadField.getText();
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format((Date) fechaField.getValue());
        return new Object[]{producto, cantidad, "Dañado", fecha, ""}; // Motivo hardcoded por ahora
    }
}
