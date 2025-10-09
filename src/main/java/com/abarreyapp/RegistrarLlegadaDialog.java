package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrarLlegadaDialog extends JDialog {
    private JComboBox<String> productoComboBox;
    private JTextField cantidadField;
    private JFormattedTextField fechaField;
    private boolean confirmed = false;

    private String[] availableFruitsVegetables = {
        "Manzana", "Plátano", "Lechuga", "Tomate", "Zanahoria", "Brócoli",
        "Naranja", "Apio", "Pepino", "Pimiento", "Cebolla", "Papa",
        "Limón", "Aguacate", "Espinaca", "Coliflor"
    };

    public RegistrarLlegadaDialog(Frame owner) {
        super(owner, "Registrar Llegada", true);
        setSize(450, 280);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("Registrar Llegada");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Registre la cantidad de fruta o verdura que ha llegado al inventario.");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Fruta o Verdura:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        // Populate product list from DB (use ProductDAO). Fall back to the embedded list on error.
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try {
            com.abarreyapp.dao.ProductDAO pdao = new com.abarreyapp.dao.ProductDAO();
            for (String[] r : pdao.findAll()) {
                // r[1] is the product name
                model.addElement(r[1]);
            }
            if (model.getSize() == 0) {
                for (String s : availableFruitsVegetables) model.addElement(s);
            }
        } catch (Exception ex) {
            for (String s : availableFruitsVegetables) model.addElement(s);
        }
        productoComboBox = new JComboBox<>(model);
        formPanel.add(productoComboBox, gbc);

        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Cantidad Recibida (kg):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cantidadField = new JTextField(10);
        formPanel.add(cantidadField, gbc);

        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Fecha de Recibido:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaField = new JFormattedTextField(sdf);
        fechaField.setValue(new Date());
        formPanel.add(fechaField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar Llegada");
        btnRegistrar.setBackground(new Color(40, 167, 69));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);

        JButton btnCancelar = new JButton("Cancelar");

        btnRegistrar.addActionListener(e -> {
            if (validateInput()) {
                // attempt to persist arrival into DB
                try {
                    String producto = (String) productoComboBox.getSelectedItem();
                    double qty = Double.parseDouble(cantidadField.getText());
                    // find product id
                    com.abarreyapp.dao.ProductDAO pdao = new com.abarreyapp.dao.ProductDAO();
                    int productId = -1;
                    for (String[] r : pdao.findAll()) {
                        if (r[1].equals(producto)) { productId = Integer.parseInt(r[0]); break; }
                    }
                    if (productId > 0) {
                        com.abarreyapp.dao.ArrivalDAO adao = new com.abarreyapp.dao.ArrivalDAO();
                        int newId = adao.insert(productId, qty);
                        if (newId <= 0) {
                            JOptionPane.showMessageDialog(this, "No se pudo guardar la llegada en la base de datos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    // If persistence fails, show an informative message and continue (row will still be added locally)
                    JOptionPane.showMessageDialog(this, "No se pudo guardar la llegada en la base de datos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                }
                confirmed = true;
                setVisible(false);
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnRegistrar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateInput() {
        if (productoComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(cantidadField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Object[] getLlegadaData() {
        String producto = (String) productoComboBox.getSelectedItem();
        String cantidad = cantidadField.getText() + " kg";
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format((Date) fechaField.getValue());
        return new Object[]{producto, cantidad, fecha, ""};
    }
}
