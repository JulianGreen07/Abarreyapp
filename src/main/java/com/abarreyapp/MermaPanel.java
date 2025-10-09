package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import com.abarreyapp.dao.MermaDAO;
import java.sql.SQLException;

public class MermaPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;

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

    com.abarreyapp.ui.RoundedButton btnNuevo = new com.abarreyapp.ui.RoundedButton("+ Nuevo");
    btnNuevo.setBackground(new Color(40,167,69));
    btnNuevo.setForeground(Color.WHITE);
    
    // right controls (only + Nuevo)
    JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    rightControls.setOpaque(false);
    rightControls.add(btnNuevo);
    headerPanel.add(rightControls, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Tabla (id hidden, product, weight, date, actions)
        String[] columnNames = {"ID","Fruta/Verdura", "Peso a Mermar (kg)", "Fecha", "Acciones"};

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return column == 4;
            }
        };

        MermaDAO mermaDAO = new MermaDAO();
        try {
            for (String[] r : mermaDAO.findAll()) {
                model.addRow(new Object[]{Integer.parseInt(r[0]), r[1], r[2] + " kg", r[3], ""});
            }
        } catch (SQLException ex) {
            Object[][] data = {
                {-1, "Manzana", "0.5 kg", "14/01/2025", ""},
                {-1, "Plátano", "0.3 kg", "14/01/2025", ""},
                {-1, "Lechuga", "0.2 kg", "13/01/2025", ""},
                {-1, "Tomate", "0.4 kg", "13/01/2025", ""}
            };
            for (Object[] r : data) model.addRow(r);
        }
        
    this.table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // Use the same pale green full-row selection used across the app
    Color paleGreen = new Color(230, 245, 235);
    table.setSelectionBackground(paleGreen);
    table.setSelectionForeground(UIManager.getColor("Label.foreground"));

    // hide id
    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
    table.getColumnModel().getColumn(0).setWidth(0);

    table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
    table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), owner));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> {
            AddMermaDialog dialog = new AddMermaDialog(owner);
            dialog.setTitle("Registrar Merma");
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Object[] rowData = dialog.getMermaData();
                String productName = rowData[0].toString();
                double weight = Double.parseDouble(rowData[1].toString());
                int insertedId = -1;
                // Try to find product id - simple approach: assume product id matches first product name index
                // Ideally ProductDAO should provide a findByName method; for now skip DB insert if no mapping
                try {
                    // attempt to insert merma if DB available (lookup product id by name)
                    com.abarreyapp.dao.ProductDAO pdao = new com.abarreyapp.dao.ProductDAO();
                    Integer pid = pdao.findByName(productName);
                    if (pid != null) {
                        MermaDAO dao = new MermaDAO();
                        insertedId = dao.insert(pid, weight);
                    }
                } catch (SQLException ex) {
                    // ignore DB errors and fallback to in-memory row
                }
                // If insert succeeded and we have an id, reload from DB so the recorded_at (with time) is shown
                if (insertedId > 0) {
                    reload();
                } else {
                    // rowData layout from AddMermaDialog.getMermaData(): {producto, cantidad, motivo, fecha, ""}
                    // Our table expects: {ID, Fruta/Verdura, Peso, Fecha, Acciones}
                    model.addRow(new Object[]{-1, productName, weight + " kg", rowData.length > 3 ? rowData[3] : "", ""});
                }
            }
        });

        // report button removed per user request
    }

    // Reload merma entries from DB
    public void reload() {
        if (model == null) return;
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) model.removeRow(i);
        MermaDAO mermaDAO = new MermaDAO();
        try {
            for (String[] r : mermaDAO.findAll()) {
                model.addRow(new Object[]{Integer.parseInt(r[0]), r[1], r[2] + " kg", r[3], ""});
            }
        } catch (SQLException ex) {
            // ignore
        }
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
    // use a method-local row variable; field removed to avoid unused-field warnings

        public ButtonEditor(JCheckBox checkBox, JFrame frame) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton editButton = new JButton("Editar");
            panel.add(editButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                // Open edit dialog similar to UsuariosPanel behavior
                Integer viewRow = (Integer) panel.getClientProperty("editingRow");
                if (viewRow == null) return;
                int modelRow = table.convertRowIndexToModel(viewRow);
                Object idObj = model.getValueAt(modelRow, 0);
                int id = -1;
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj != null) {
                    try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; }
                }

                // prepare row data expected by AddMermaDialog: {producto, cantidad, motivo, fecha, ""}
                Object producto = model.getValueAt(modelRow, 1);
                Object pesoCell = model.getValueAt(modelRow, 2);
                String pesoStr = pesoCell == null ? "" : pesoCell.toString().replace(" kg", "");
                Object fecha = model.getValueAt(modelRow, 3);
                Object[] rowData = new Object[]{producto, pesoStr, "Dañado", fecha, ""};

                AddMermaDialog dialog = new AddMermaDialog(frame, rowData);
                dialog.setVisible(true);
                System.out.println("MermaPanel: dialog closed. isDeleted=" + dialog.isDeleted() + " isConfirmed=" + dialog.isConfirmed());

                if (dialog.isDeleted()) {
                    if (id > 0) {
                        MermaDAO dao = new MermaDAO();
                        try {
                            boolean del = dao.delete(id);
                            if (!del) JOptionPane.showMessageDialog(frame, "No se pudo eliminar la merma en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(frame, "Error al eliminar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    model.removeRow(modelRow);
                    return;
                }

                if (dialog.isConfirmed()) {
                    Object[] updated = dialog.getMermaData();
                    String prod = updated.length > 0 ? (String) updated[0] : "";
                    String cantStr = updated.length > 1 ? updated[1].toString() : "0";
                    double newWeight = 0;
                    try { newWeight = Double.parseDouble(cantStr); } catch (NumberFormatException ex) {}
                    String newFecha = updated.length > 3 ? (String) updated[3] : "";

                    if (id > 0) {
                        MermaDAO dao = new MermaDAO();
                        try {
                            boolean ok = dao.update(id, newWeight, newFecha);
                            if (!ok) JOptionPane.showMessageDialog(frame, "No se pudo actualizar la merma en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                            else {
                                // reload to get DB formatting
                                reload();
                                return;
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(frame, "Error al actualizar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    // fallback: update model row in-memory
                    model.setValueAt(prod, modelRow, 1);
                    model.setValueAt(newWeight + " kg", modelRow, 2);
                    model.setValueAt(newFecha, modelRow, 3);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // store row as a client property on the panel so listeners can access when needed
            panel.putClientProperty("editingRow", row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
