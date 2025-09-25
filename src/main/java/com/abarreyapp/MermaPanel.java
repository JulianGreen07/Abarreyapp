package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MermaPanel extends JPanel {

    private DefaultTableModel model;

    public MermaPanel(JFrame owner) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabecera
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Merma");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel subtitleLabel = new JLabel("Registra el peso de productos a mermar");
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlesPanel = new JPanel();
        titlesPanel.setLayout(new BoxLayout(titlesPanel, BoxLayout.Y_AXIS));
        titlesPanel.add(titleLabel);
        titlesPanel.add(subtitleLabel);
        
        headerPanel.add(titlesPanel, BorderLayout.WEST);

        JButton btnNuevo = new JButton("+ Nuevo");
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 12));
        btnNuevo.setFocusPainted(false);
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = {"Fruta/Verdura", "Peso a Mermar (kg)", "Fecha", "Acciones"};
        Object[][] data = {
            {"Manzana", "0.5 kg", "14/01/2025", ""},
            {"Plátano", "0.3 kg", "14/01/2025", ""},
            {"Lechuga", "0.2 kg", "13/01/2025", ""},
            {"Tomate", "0.4 kg", "13/01/2025", ""}
        };

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return column == 3;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), owner));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> {
            AddMermaDialog dialog = new AddMermaDialog(owner);
            dialog.setTitle("Registrar Merma");
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Object[] rowData = dialog.getMermaData();
                model.addRow(new Object[]{rowData[0], rowData[1], rowData[3], ""});
            }
        });
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Editar");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
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

        public ButtonEditor(JCheckBox checkBox, JFrame frame) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton editButton = new JButton("Editar");
            panel.add(editButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                // Here you would open an "Edit Merma" dialog
                JOptionPane.showMessageDialog(frame, "Funcionalidad para editar merma no implementada.");
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
