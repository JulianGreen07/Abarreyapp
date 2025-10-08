package com.abarreyapp;

import javax.swing.*;
import java.awt.*;

public class AddFrutaVerduraDialog extends JDialog {
    private JTextField nombreField;
    private boolean confirmed = false;

    public AddFrutaVerduraDialog(Frame owner) {
        super(owner, "Agregar Fruta o Verdura", true);
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("Agregar Fruta o Verdura");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Ingrese el nombre de la fruta o verdura que desea agregar al inventario.");
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nombreField = new JTextField(20);
        // Placeholder text can be handled by a custom component or a library, for now, we leave it empty.
        formPanel.add(nombreField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);

        JButton btnCancelar = new JButton("Cancelar");

        btnAgregar.addActionListener(e -> {
            if (!nombreField.getText().trim().isEmpty()) {
                confirmed = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnAgregar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNombre() {
        return nombreField.getText().trim();
    }

    /**
     * Returns an object array with product data: name, category, price, stock
     * Category/price/stock are placeholders when not provided by the dialog UI.
     */
    public Object[] getProductData() {
        String name = getNombre();
        // sensible defaults; UI doesn't capture these yet
        String category = "Fruta";
        String price = "0.00";
        String stock = "0";
        return new Object[]{name, category, price, stock};
    }
}
