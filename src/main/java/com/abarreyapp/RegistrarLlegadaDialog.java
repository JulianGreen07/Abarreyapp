package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
// --- (MODIFICACIÓN 1: IMPORTACIONES AÑADIDAS) ---
import com.abarreyapp.dao.ProductDAO;
import java.sql.SQLException;
import java.text.ParseException;
// --- (FIN DE MODIFICACIÓN) ---

public class RegistrarLlegadaDialog extends JDialog {
    private JComboBox<String> productoComboBox;
    private JTextField cantidadField;
    private JFormattedTextField fechaField;
    private boolean confirmed = false;

    // --- (MODIFICACIÓN 2: LISTA HARDCODED ELIMINADA) ---
    // Ya no necesitamos este array, lo cargaremos desde la BD
    /*
    private String[] availableFruitsVegetables = {
        "Manzana", "Plátano", "Lechuga", "Tomate", "Zanahoria", "Brócoli",
        "Naranja", "Apio", "Pepino", "Pimiento", "Cebolla", "Papa",
        "Limón", "Aguacate", "Espinaca", "Coliflor"
    };
    */

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

        // --- (MODIFICACIÓN 3: CARGAR PRODUCTOS DESDE DAO) ---
        productoComboBox = new JComboBox<>(); // Inicializar vacío
        loadProductsIntoComboBox(); // Cargar desde la BD
        // --- (FIN DE MODIFICACIÓN) ---

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

        // --- (MODIFICACIÓN 4: ESTANDARIZAR FORMATO DE FECHA) ---
        // Usamos yyyy-MM-dd para que coincida con la BD y los DAOs
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // --- (FIN DE MODIFICACIÓN) ---

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

    // --- (MODIFICACIÓN 5: NUEVO CONSTRUCTOR PARA "EDITAR") ---
    public RegistrarLlegadaDialog(Frame owner, String currentProduct, String currentQuantity, String currentDate) {
        // 1. Llama al constructor original para construir la UI y cargar productos
        this(owner);

        setTitle("Editar Llegada"); // Cambia el título de la ventana

        // 2. Rellena los campos con los datos de la fila
        productoComboBox.setSelectedItem(currentProduct);

        // Limpia el " kg" del texto de la cantidad
        String qtyValue = currentQuantity.replace(" kg", "").trim();
        cantidadField.setText(qtyValue);

        // Convierte el String de fecha (yyyy-MM-dd) de vuelta a un objeto Date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            fechaField.setValue(sdf.parse(currentDate));
        } catch (ParseException e) {
            // Si algo falla, solo deja la fecha de hoy (que ya está por defecto)
        }
    }
    // --- (FIN DE MODIFICACIÓN) ---


    // --- (MODIFICACIÓN 6: NUEVO MÉTODO PARA CARGAR PRODUCTOS) ---
    private void loadProductsIntoComboBox() {
        try {
            ProductDAO productDAO = new ProductDAO();
            // productDAO.findAll() devuelve String[id, nombre, cat, precio, stock]
            for (String[] productData : productDAO.findAll()) {
                String productName = productData[1]; // El índice 1 es el nombre
                productoComboBox.addItem(productName);
            }
        } catch (SQLException ex) {
            productoComboBox.addItem("Error al cargar productos");
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    // --- (FIN DE MODIFICACIÓN) ---


    private boolean validateInput() {
        if (productoComboBox.getSelectedIndex() == -1 || productoComboBox.getSelectedItem().toString().startsWith("Error")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto válido.", "Error", JOptionPane.ERROR_MESSAGE);
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

        // --- (MODIFICACIÓN 7: ESTANDARIZAR FORMATO DE FECHA DE SALIDA) ---
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format((Date) fechaField.getValue());
        // --- (FIN DE MODIFICACIÓN) ---

        // Devolvemos la fecha como yyyy-MM-dd
        return new Object[]{producto, cantidad, fecha, ""};
    }
}