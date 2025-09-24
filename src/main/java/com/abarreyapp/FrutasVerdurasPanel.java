package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FrutasVerdurasPanel extends JPanel {
    private DefaultTableModel model;
    private JFrame parentFrame;

    public FrutasVerdurasPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Frutas y Verduras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnNuevo = new JButton("+ Nuevo");
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 12));
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> {
            AddFrutaVerduraDialog dialog = new AddFrutaVerduraDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // Lógica para agregar la nueva fruta/verdura a la tabla de productos
                // Esto podría implicar añadir una fila a un modelo de tabla
            }
        });
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Productos", createProductosPanel());
        tabbedPane.addTab("Llegadas", createLlegadasPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductosPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[] columnNames = {"Producto", "Categoría", "Precio", "Stock", "Acciones"};
        Object[][] data = {
            {"Manzana", "Fruta", "25.50", "100 kg", ""},
            {"Plátano", "Fruta", "15.00", "150 kg", ""},
            {"Tomate", "Verdura", "30.00", "80 kg", ""},
            {"Lechuga", "Verdura", "12.00", "50 pz", ""}
        };

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return column == 4;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLlegadasPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Prepare model for llegadas so it can be updated by the registrar dialog
        String[] columnNames = {"Fruta/Verdura", "Cantidad Recibida (kg)", "Fecha de Recibido", "Acciones"};
        Object[][] data = {
            {"Manzana", "10 kg", "14/01/2025", ""},
            {"Plátano", "5 kg", "14/01/2025", ""},
            {"Lechuga", "2 kg", "13/01/2025", ""},
            {"Tomate", "4 kg", "13/01/2025", ""}
        };

        DefaultTableModel llegadasModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        JButton btnRegistrarLlegada = new JButton("+ Registrar Llegada");
        btnRegistrarLlegada.setBackground(new Color(40, 167, 69));
        btnRegistrarLlegada.setForeground(Color.WHITE);
        btnRegistrarLlegada.setFocusPainted(false);
        btnRegistrarLlegada.addActionListener(e -> {
            RegistrarLlegadaDialog dialog = new RegistrarLlegadaDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // Añadir la fila devuelta por el diálogo al modelo
                llegadasModel.addRow(dialog.getLlegadaData());
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnRegistrarLlegada);
        panel.add(buttonPanel, BorderLayout.NORTH);

        JTable llegadasTable = new JTable(llegadasModel);
        llegadasTable.setFont(new Font("Arial", Font.PLAIN, 14));
        llegadasTable.setRowHeight(40);
        llegadasTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        llegadasTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(llegadasTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editButton = new JButton("Editar");
        private JButton deleteButton = new JButton("Eliminar");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton editButton = new JButton("Editar");
            JButton deleteButton = new JButton("Eliminar");
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(parentFrame, "Funcionalidad para editar no implementada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                int response = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "¿Estás seguro de que quieres eliminar este producto?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION) {
                    // La eliminación de la fila se manejaría aquí, pero el modelo no es accesible directamente.
                    // Esto requeriría pasar el modelo a ButtonEditor o usar un enfoque de listener.
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
