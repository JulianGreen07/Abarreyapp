package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class UsuariosPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JFrame parentFrame;

    public UsuariosPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabecera
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Usuarios");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Placeholder for username, as it's not passed from MainFrame
        String username = "Admin"; 
        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
        JLabel subtitleLabel = new JLabel(capitalizedUsername + ", ¡bienvenido!");
        subtitleLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel de búsqueda y botones
        JPanel topActionPanel = new JPanel(new BorderLayout(10, 10));
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topActionPanel.add(searchField, BorderLayout.CENTER);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> {
            String searchText = searchField.getText().trim().toLowerCase();
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean matches = true;
                for (int j = 0; j < model.getColumnCount() - 1; j++) {
                    String cellValue = model.getValueAt(i, j).toString().toLowerCase();
                    if (!cellValue.contains(searchText)) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    // Seleccionar y desplazar a la fila coincidente
                    if (table != null) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(parentFrame, "No se encontraron resultados para: " + searchText,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        });
        topActionPanel.add(btnBuscar, BorderLayout.EAST);

        add(topActionPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Nombre", "Rol", "Correo", "Teléfono", "Acciones"};
        Object[][] data = {
            {"Nancy Daniela Arce Jiménez", "Administrador", "nancyataniela@gmail.com", "662139401", ""},
            {"Darío Ruíz Álvarez", "Gerente", "darioruizalv@gmail.com", "6622384950", ""},
            {"Laura Barragán Montalvo", "Usuario", "lauritabarrag@gmail.com", "6621483374", ""},
            {"Miguel Montaño Carrera", "Usuario", "miguelmonta@gmail.com", "6621394919", ""},
            {"Ximena Contreras Juárez", "Usuario", "ximenacontri@gmail.com", "6621392339", ""}
        };

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return column == 4;
            }
        };
        
    this.table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 14));
    table.setRowHeight(40);
    table.setShowGrid(true);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
    table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), table, parentFrame));

    JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
             @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // Clases internas para los botones en la tabla
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton = new JButton("Editar");
        private JButton deleteButton = new JButton("Eliminar");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
            add(deleteButton);
            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
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
        protected JButton editButton;
        protected JButton deleteButton;
        private JTable table;
        private int row;
        private JFrame frame;

        public ButtonEditor(JCheckBox checkBox, JTable table, JFrame frame) {
            super(checkBox);
            this.table = table;
            this.frame = frame;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Editar");
            deleteButton = new JButton("Eliminar");

            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = table.convertRowIndexToModel(row);
                Object[] rowData = new Object[model.getColumnCount() - 1];
                for (int i = 0; i < rowData.length; i++) {
                    rowData[i] = model.getValueAt(modelRow, i);
                }

                AddUserDialog dialog = new AddUserDialog(frame, "Editar Usuario", rowData);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Object[] updatedData = dialog.getUserData();
                    for (int i = 0; i < updatedData.length; i++) {
                        model.setValueAt(updatedData[i], modelRow, i);
                    }
                }
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                int response = JOptionPane.showConfirmDialog(
                    frame,
                    "¿Estás seguro de que quieres eliminar a este usuario?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    int modelRow = table.convertRowIndexToModel(row);
                    model.removeRow(modelRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
