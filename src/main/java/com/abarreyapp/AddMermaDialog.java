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

    private String[] availableFruitsVegetables = {
        "Manzana", "Plátano", "Lechuga", "Tomate", "Zanahoria", "Brócoli",
        "Naranja", "Apio", "Pepino", "Pimiento", "Cebolla", "Papa",
        "Limón", "Aguacate", "Espinaca", "Coliflor"
    };

    public AddMermaDialog(Frame owner) {
        super(owner, "Registrar Merma", true);
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
        productoComboBox = new JComboBox<>(availableFruitsVegetables);
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

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                setVisible(false);
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
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

    public Object[] getMermaData() {
        String producto = (String) productoComboBox.getSelectedItem();
        String cantidad = cantidadField.getText();
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format((Date) fechaField.getValue());
        return new Object[]{producto, cantidad, "Dañado", fecha, ""}; // Motivo hardcoded por ahora
    }
}
