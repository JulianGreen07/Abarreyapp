package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class FrutasVerdurasPanel extends JPanel {
    private DefaultTableModel model;
    private JTable productosTable;
    private JFrame parentFrame;
    private com.abarreyapp.dao.ProductDAO productDAO = new com.abarreyapp.dao.ProductDAO();

    public FrutasVerdurasPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Frutas y Verduras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

    // Usar estilo de botón redondeado compartido
    com.abarreyapp.ui.RoundedButton btnNuevo = new com.abarreyapp.ui.RoundedButton("+ Nuevo");
    btnNuevo.setBackground(new Color(40,167,69));
    btnNuevo.setForeground(Color.WHITE);
    // usar el campo productDAO (ya disponible)
        btnNuevo.addActionListener(e -> {
            AddFrutaVerduraDialog dialog = new AddFrutaVerduraDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Object[] d = dialog.getProductData();
                String name = d.length>0?d[0].toString():"";
                String cat = d.length>1?d[1].toString():"";
                double price = d.length>2?Double.parseDouble(d[2].toString()):0.0;
                String stock = d.length>3?d[3].toString():"";
                try {
                    int newId = this.productDAO.insert(name, cat, price, stock);
                    if (newId > 0) {
                        // recargar desde la BD para obtener IDs canónicos y el orden
                        reload();
                        // seleccionar el id recién insertado
                        for (int i = 0; i < model.getRowCount(); i++) {
                            Object idObj = model.getValueAt(i, 0);
                            if (idObj != null) {
                                try {
                                    int id = Integer.parseInt(idObj.toString());
                                    if (id == newId) {
                                        int viewRow = productosTable.convertRowIndexToView(i);
                                        productosTable.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                                        productosTable.scrollRectToVisible(productosTable.getCellRect(viewRow, 0, true));
                                        break;
                                    }
                                } catch (NumberFormatException nfe) {}
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "No se pudo insertar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        model.addRow(new Object[]{-1, name, ""});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Error al insertar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    model.addRow(new Object[]{-1, name, ""});
                }
            }
        });
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Productos", createProductosPanel());
        tabbedPane.addTab("Llegadas", createLlegadasPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Recargar productos desde la BD
    public void reload() {
        if (model == null) return;
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) model.removeRow(i);
        try {
            for (String[] r : productDAO.findAll()) {
                model.addRow(new Object[]{Integer.parseInt(r[0]), r[1], ""});
            }
        } catch (SQLException ex) {
            // Mostrar un mensaje para que el usuario sepa que no se pudo leer la BD.
            JOptionPane.showMessageDialog(parentFrame, "No se pudo cargar la lista de productos desde la base de datos:\n" + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createProductosPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    // Columnas simplificadas: id oculto (0), Nombre (1), Acciones (2)
        String[] columnNames = {"ID","Nombre", "Acciones"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return column == 2;
            }
        };

    // intentar cargar desde la BD
        try {
            for (String[] r : productDAO.findAll()) {
                model.addRow(new Object[]{Integer.parseInt(r[0]), r[1], ""});
            }
        } catch (SQLException ex) {
            Object[][] data = {
                {-1, "Manzana", "Fruta", "25.50", "100 kg", ""},
                {-1, "Plátano", "Fruta", "15.00", "150 kg", ""},
                {-1, "Tomate", "Verdura", "30.00", "80 kg", ""},
                {-1, "Lechuga", "Verdura", "12.00", "50 pz", ""}
            };
            for (Object[] r : data) model.addRow(new Object[]{r[0], r[1], ""});
        }
        
    JTable table = new JTable(model);
    this.productosTable = table;
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
    // Selección de fila completa verde pálido para coincidir con otras tablas
        Color paleGreen = new Color(230, 245, 235);
        table.setSelectionBackground(paleGreen);
        table.setSelectionForeground(UIManager.getColor("Label.foreground"));
        
    // ocultar columna id
    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
    table.getColumnModel().getColumn(0).setWidth(0);

    table.getColumnModel().getColumn(2).setCellRenderer(new EditOnlyRenderer());
    table.getColumnModel().getColumn(2).setCellEditor(new EditOnlyEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLlegadasPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[] columnNames = {"Fruta/Verdura", "Cantidad Recibida (kg)", "Fecha de Recibido", "Acciones"};
        Object[][] data = {
            {"Manzana", "10 kg", "14/01/2025", ""},
            {"Plátano", "5 kg", "14/01/2025", ""},
            {"Lechuga", "2 kg", "13/01/2025", ""},
            {"Tomate", "4 kg", "13/01/2025", ""}
        };

        final DefaultTableModel llegadasModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        // try loading real arrivals from DB
        try {
            com.abarreyapp.dao.ArrivalDAO adao = new com.abarreyapp.dao.ArrivalDAO();
            java.util.List<String[]> rows = adao.findAll();
            // rebuild model with actual rows
            while (llegadasModel.getRowCount() > 0) llegadasModel.removeRow(0);
            for (String[] r : rows) {
                // r: {id, product, quantity, recorded_at}
                llegadasModel.addRow(new Object[]{r[1], r[2] + " kg", r[3], ""});
            }
        } catch (Exception ex) {
            // fallback to hardcoded demo data already present in 'data'
        }

        JTable llegadasTable = new JTable(llegadasModel);
        llegadasTable.setFont(new Font("Arial", Font.PLAIN, 14));
        llegadasTable.setRowHeight(40);
    // pale green full-row selection
    Color paleGreen = new Color(230, 245, 235);
    llegadasTable.setSelectionBackground(paleGreen);
    llegadasTable.setSelectionForeground(UIManager.getColor("Label.foreground"));
    // Usar renderer/editor dedicado para llegadas para que no afecten productos
        llegadasTable.getColumnModel().getColumn(3).setCellRenderer(new ArrivalsRenderer());
        llegadasTable.getColumnModel().getColumn(3).setCellEditor(new ArrivalsEditor(new JCheckBox(), parentFrame, llegadasTable, llegadasModel));

        JScrollPane scrollPane = new JScrollPane(llegadasTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRegistrarLlegada = new JButton("+ Registrar Llegada");
        btnRegistrarLlegada.setBackground(new Color(40, 167, 69));
        btnRegistrarLlegada.setForeground(Color.WHITE);
        btnRegistrarLlegada.setFocusPainted(false);
        btnRegistrarLlegada.addActionListener(e -> {
            RegistrarLlegadaDialog dialog = new RegistrarLlegadaDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Object[] rowData = dialog.getLlegadaData();
                // try to persist was already attempted inside the dialog; if DB insert failed we still add the row to UI
                llegadasModel.addRow(new Object[]{rowData[0], rowData[1], rowData[2], ""});
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnRegistrarLlegada);
        panel.add(buttonPanel, BorderLayout.NORTH);
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
                int modelRow = productosTable.convertRowIndexToModel(row);
                Object idObj = model.getValueAt(modelRow, 0);
                int id = -1;
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj != null) { try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; } }

                AddFrutaVerduraDialog dlg = new AddFrutaVerduraDialog(parentFrame);
                // prellenar: el diálogo no soporta todos los campos, así que solo establecemos el nombre reflejando la UI (enfoque simple)
                dlg.setVisible(true);
                if (dlg.isConfirmed()) {
                    Object[] d = dlg.getProductData();
                    String name = d.length>0?d[0].toString():"";
                    if (id > 0) {
                        try {
                            // encontrar la fila existente en BD y actualizar todos los campos con el nuevo nombre
                            for (String[] r : FrutasVerdurasPanel.this.productDAO.findAll()) {
                                try {
                                    int rid = Integer.parseInt(r[0]);
                                    if (rid == id) {
                                        String existingCat = r[2];
                                        double existingPrice = 0.0;
                                        try { existingPrice = Double.parseDouble(r[3]); } catch (Exception ex) {}
                                        String existingStock = r[4];
                                        boolean ok = FrutasVerdurasPanel.this.productDAO.update(id, name, existingCat, existingPrice, existingStock);
                                        if (ok) {
                                            reload();
                                            // reseleccionar el elemento actualizado
                                            for (int i = 0; i < model.getRowCount(); i++) {
                                                Object idObj2 = model.getValueAt(i, 0);
                                                if (idObj2 != null) {
                                                    try {
                                                        int id2 = Integer.parseInt(idObj2.toString());
                                                        if (id2 == id) {
                                                            int viewRow = productosTable.convertRowIndexToView(i);
                                                            productosTable.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                                                            productosTable.scrollRectToVisible(productosTable.getCellRect(viewRow, 0, true));
                                                            break;
                                                        }
                                                    } catch (NumberFormatException nfe) {}
                                                }
                                            }
                                        }
                                        break;
                                    }
                                } catch (NumberFormatException nfe) {}
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(parentFrame, "Error al actualizar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        // fila solo local; actualizar directamente el nombre en el modelo
                        model.setValueAt(name, modelRow, 1);
                    }
                }
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
                    int modelRow = productosTable.convertRowIndexToModel(row);
                    Object idObj = model.getValueAt(modelRow, 0);
                    int id = -1;
                    if (idObj instanceof Number) id = ((Number) idObj).intValue();
                    else if (idObj != null) { try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; } }
                    if (id > 0) {
                        try {
                            boolean ok = productDAO.delete(id);
                            if (!ok) {
                                JOptionPane.showMessageDialog(parentFrame, "No se pudo eliminar el producto de la BD.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            // recargar desde la BD para asegurar que UI y BD estén sincronizados
                            reload();
                            return;
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(parentFrame, "Error al eliminar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    // fila solo local (sin id en BD) -> eliminar inmediatamente
                    model.removeRow(modelRow);
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

    // Renderer/editor para filas de llegadas (editar/eliminar solo afecta llegadas)
    class ArrivalsRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton editButton = new JButton("Editar");
        private final JButton deleteButton = new JButton("Eliminar");
        public ArrivalsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
            add(deleteButton);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) setBackground(table.getSelectionBackground()); else setBackground(UIManager.getColor("Button.background"));
            return this;
        }
    }

    class ArrivalsEditor extends DefaultCellEditor {
        protected JPanel panel;
        private int row;
        private JTable tableRef;
        private javax.swing.table.DefaultTableModel modelRef;
        private JFrame owner;

        public ArrivalsEditor(JCheckBox checkBox, JFrame owner, JTable tableRef, javax.swing.table.DefaultTableModel modelRef) {
            super(checkBox);
            this.owner = owner;
            this.tableRef = tableRef;
            this.modelRef = modelRef;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton editButton = new JButton("Editar");
            JButton deleteButton = new JButton("Eliminar");
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = tableRef.convertRowIndexToModel(row);
                Object product = modelRef.getValueAt(modelRow, 0);
                Object quantity = modelRef.getValueAt(modelRow, 1);
                Object date = modelRef.getValueAt(modelRow, 2);
                String newProduct = JOptionPane.showInputDialog(owner, "Producto:", product);
                if (newProduct == null) return;
                String newQty = JOptionPane.showInputDialog(owner, "Cantidad (ej. 5 kg):", quantity);
                if (newQty == null) return;
                String newDate = JOptionPane.showInputDialog(owner, "Fecha (dd/MM/yyyy):", date);
                if (newDate == null) return;
                modelRef.setValueAt(newProduct, modelRow, 0);
                modelRef.setValueAt(newQty, modelRow, 1);
                modelRef.setValueAt(newDate, modelRow, 2);
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                int response = JOptionPane.showConfirmDialog(owner, "¿Eliminar esta llegada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    int modelRow = tableRef.convertRowIndexToModel(row);
                    modelRef.removeRow(modelRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }

    // Renderer que muestra una sola acción "Editar" (para lista simplificada de productos)
    class EditOnlyRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editButton = new JButton("Editar");
        public EditOnlyRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) setBackground(table.getSelectionBackground());
            else setBackground(UIManager.getColor("Button.background"));
            return this;
        }
    }

    class EditOnlyEditor extends DefaultCellEditor {
        protected JPanel panel;
        private int row;
        public EditOnlyEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton editButton = new JButton("Editar");
            panel.add(editButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = productosTable.convertRowIndexToModel(row);
                Object idObj = model.getValueAt(modelRow, 0);
                int id = -1;
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj != null) { try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; } }

                // Abrir un diálogo simple para editar solo el nombre
                String currentName = model.getValueAt(modelRow, 1).toString();
                String newName = JOptionPane.showInputDialog(FrutasVerdurasPanel.this.parentFrame, "Editar nombre:", currentName);
                if (newName != null && !newName.trim().isEmpty()) {
                    model.setValueAt(newName.trim(), modelRow, 1);
                    if (id > 0) {
                        try {
                            // Preservar otros campos del producto leyendo los valores actuales en la BD
                            for (String[] r : productDAO.findAll()) {
                                try {
                                    int rid = Integer.parseInt(r[0]);
                                    if (rid == id) {
                                        String cat = r[2];
                                        double price = 0.0;
                                        try { price = Double.parseDouble(r[3]); } catch (Exception ex) {}
                                        String stock = r[4];
                                            boolean ok = productDAO.update(id, newName.trim(), cat, price, stock);
                                            if (ok) {
                                                // recargar desde la BD y reseleccionar
                                                reload();
                                                for (int j = 0; j < model.getRowCount(); j++) {
                                                    Object idObj2 = model.getValueAt(j, 0);
                                                    if (idObj2 != null) {
                                                        try {
                                                            int id2 = Integer.parseInt(idObj2.toString());
                                                            if (id2 == id) {
                                                                int viewRow = productosTable.convertRowIndexToView(j);
                                                                productosTable.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                                                                productosTable.scrollRectToVisible(productosTable.getCellRect(viewRow, 0, true));
                                                                break;
                                                            }
                                                        } catch (NumberFormatException nfe) {}
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                } catch (NumberFormatException nfe) {}
                            }
                        } catch (SQLException ex) {
                            // ignorar fallos de actualización por ahora
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }
}
