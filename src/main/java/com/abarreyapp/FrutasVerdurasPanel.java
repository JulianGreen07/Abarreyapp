package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.abarreyapp.dao.ProductDAO;
import java.sql.SQLException;

// --- (MODIFICACIÓN: NUEVAS IMPORTACIONES) ---
import com.abarreyapp.dao.LlegadaDAO; // DAO para llegadas
import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date; // Para la base de datos
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
// --- (FIN DE NUEVAS IMPORTACIONES) ---

public class FrutasVerdurasPanel extends JPanel {
    private DefaultTableModel model; // Modelo para la tabla de Productos
    private JTable productosTable;
    private JFrame parentFrame;
    private com.abarreyapp.dao.ProductDAO productDAO = new com.abarreyapp.dao.ProductDAO();

    // --- (MODIFICACIÓN: NUEVOS MIEMBROS DE CLASE) ---
    private LlegadaDAO llegadaDAO;
    private DefaultTableModel llegadasModel; // Modelo para la tabla de Llegadas
    // --- (FIN DE MODIFICACIÓN) ---


    public FrutasVerdurasPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        // --- (MODIFICACIÓN: INICIALIZAR EL NUEVO DAO) ---
        this.llegadaDAO = new LlegadaDAO();
        // --- (FIN DE MODIFICACIÓN) ---

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Frutas y Verduras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Botón "+ Nuevo" (para Productos)
        com.abarreyapp.ui.RoundedButton btnNuevo = new com.abarreyapp.ui.RoundedButton("+ Nuevo");
        btnNuevo.setBackground(new Color(40,167,69));
        btnNuevo.setForeground(Color.WHITE);
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
                        reload(); // Recarga la tabla de productos
                        // (Lógica para seleccionar la fila nueva...)
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

    // Recargar productos (Pestaña 1)
    public void reload() {
        if (model == null) return;
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) model.removeRow(i);
        try {
            for (String[] r : productDAO.findAll()) {
                model.addRow(new Object[]{Integer.parseInt(r[0]), r[1], ""});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, "No se pudo cargar la lista de productos desde la base de datos:\n" + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- (NUEVO MÉTODO PARA RECARGAR LLEGADAS) ---
    public void loadLlegadas() {
        if (llegadasModel == null) return;
        llegadasModel.setRowCount(0); // Limpiar la tabla
        try {
            // El DAO devuelve [ID, Producto, Cantidad, Fecha (String)]
            for (Object[] r : llegadaDAO.findAll()) {
                llegadasModel.addRow(new Object[]{r[0], r[1], r[2], r[3], ""}); // Añadir columna "" para acciones
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, "No se pudo cargar la lista de llegadas desde la base de datos:\n" + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    // --- (FIN DE NUEVO MÉTODO) ---


    // Panel de "Productos" (Pestaña 1)
    private JPanel createProductosPanel() {
        // ... (Este método se queda igual que como lo tenías)
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[] columnNames = {"ID","Nombre", "Acciones"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

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
        Color paleGreen = new Color(230, 245, 235);
        table.setSelectionBackground(paleGreen);
        table.setSelectionForeground(UIManager.getColor("Label.foreground"));

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    // --- (PANEL DE LLEGADAS MODIFICADO) ---
    private JPanel createLlegadasPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // --- (MODIFICACIÓN: AÑADIDA COLUMNA ID) ---
        String[] columnNames = {"ID", "Fruta/Verdura", "Cantidad Recibida (kg)", "Fecha de Recibido", "Acciones"};

        // --- (MODIFICACIÓN: USAR CAMPO DE CLASE Y EMPEZAR VACÍO) ---
        llegadasModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Acciones es la columna 4
            }
        };

        JTable llegadasTable = new JTable(llegadasModel);
        llegadasTable.setFont(new Font("Arial", Font.PLAIN, 14));
        llegadasTable.setRowHeight(40);
        Color paleGreen = new Color(230, 245, 235);
        llegadasTable.setSelectionBackground(paleGreen);
        llegadasTable.setSelectionForeground(UIManager.getColor("Label.foreground"));

        // --- (MODIFICACIÓN: OCULTAR COLUMNA ID) ---
        llegadasTable.getColumnModel().getColumn(0).setMinWidth(0);
        llegadasTable.getColumnModel().getColumn(0).setMaxWidth(0);
        llegadasTable.getColumnModel().getColumn(0).setWidth(0);

        // --- (MODIFICACIÓN: ÍNDICE DE COLUMNA DE ACCIONES CAMBIADO A 4) ---
        llegadasTable.getColumnModel().getColumn(4).setCellRenderer(new ArrivalsRenderer());
        llegadasTable.getColumnModel().getColumn(4).setCellEditor(new ArrivalsEditor(new JCheckBox(), parentFrame, llegadasTable, llegadasModel));

        JScrollPane scrollPane = new JScrollPane(llegadasTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRegistrarLlegada = new JButton("+ Registrar Llegada");
        btnRegistrarLlegada.setBackground(new Color(40, 167, 69));
        btnRegistrarLlegada.setForeground(Color.WHITE);
        btnRegistrarLlegada.setFocusPainted(false);

        // --- (MODIFICACIÓN: BOTÓN "+ REGISTRAR LLEGADA" CONECTADO A BD) ---
        btnRegistrarLlegada.addActionListener(e -> {
            // (Asumimos que RegistrarLlegadaDialog es similar a AddMermaDialog)
            RegistrarLlegadaDialog dialog = new RegistrarLlegadaDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Object[] rowData = dialog.getLlegadaData();
                // rowData[0] = Producto (String)
                // rowData[1] = Cantidad (String, ej "10 kg")
                // rowData[2] = Fecha (String yyyy-MM-dd)

                try {
                    // Convertimos la fecha String a java.sql.Date
                    Date sqlDate = Date.valueOf(rowData[2].toString());

                    // Llamamos al DAO para insertar
                    llegadaDAO.insert(rowData[0].toString(), rowData[1].toString(), sqlDate);

                    // Recargamos la tabla desde la BD
                    loadLlegadas();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Error al guardar en la base de datos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Error en el formato de datos del diálogo. " + ex.getMessage(), "Error de Formato", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnRegistrarLlegada);
        panel.add(buttonPanel, BorderLayout.NORTH);

        // --- (MODIFICACIÓN: CARGAR DATOS AL ABRIR) ---
        loadLlegadas();
        return panel;
    }


    // --- (CLASES INTERNAS) ---

    // (ButtonRenderer para Productos - se queda igual)
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
            if (isSelected) { setBackground(table.getSelectionBackground()); }
            else { setBackground(UIManager.getColor("Button.background")); }
            return this;
        }
    }

    // (ButtonEditor para Productos - se queda igual)
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
                // ... (Lógica de editar producto que ya tenías)
                fireEditingStopped();
                int modelRow = productosTable.convertRowIndexToModel(row);
                Object idObj = model.getValueAt(modelRow, 0);
                int id = -1;
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj != null) { try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; } }

                String currentName = model.getValueAt(modelRow, 1).toString();
                String newName = JOptionPane.showInputDialog(FrutasVerdurasPanel.this.parentFrame, "Editar nombre:", currentName);

                if (newName != null && !newName.trim().isEmpty()) {
                    model.setValueAt(newName.trim(), modelRow, 1);
                    if (id > 0) {
                        try {
                            for (String[] r : FrutasVerdurasPanel.this.productDAO.findAll()) {
                                try {
                                    int rid = Integer.parseInt(r[0]);
                                    if (rid == id) {
                                        String existingCat = r[2];
                                        double existingPrice = 0.0;
                                        try { existingPrice = Double.parseDouble(r[3]); } catch (Exception ex) {}
                                        String existingStock = r[4];
                                        boolean ok = FrutasVerdurasPanel.this.productDAO.update(id, newName.trim(), existingCat, existingPrice, existingStock);
                                        if (ok) {
                                            reload();
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
                    }
                }
            });

            deleteButton.addActionListener(e -> {
                // ... (Lógica de eliminar producto que ya tenías)
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
                            reload();
                            return;
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(parentFrame, "Error al eliminar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
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
        public Object getCellEditorValue() { return ""; }
    }


    // --- (CLASES PARA LLEGADAS - MODIFICADAS PARA CONECTAR A BD) ---

    // (Renderer para Llegadas - se queda igual)
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

    // (Editor para Llegadas - MODIFICADO)
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

            // --- (ACCIÓN EDITAR - CONECTADA A BD) ---
            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = tableRef.convertRowIndexToModel(row);

                // (Col 0=ID, 1=Prod, 2=Cant, 3=Fecha)
                int id = (Integer) modelRef.getValueAt(modelRow, 0);
                Object product = modelRef.getValueAt(modelRow, 1);
                Object quantity = modelRef.getValueAt(modelRow, 2);
                Object date = modelRef.getValueAt(modelRow, 3);

                // --- (MODIFICACIÓN: USAR EL DIÁLOGO CONSISTENTE) ---
                // (Esto asume que tienes un RegistrarLlegadaDialog(owner, prod, quant, date))
                // (Si no, tendremos que crearlo después)
                RegistrarLlegadaDialog dialog = new RegistrarLlegadaDialog(owner, product.toString(), quantity.toString(), date.toString());
                dialog.setTitle("Editar Llegada");
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Object[] updatedData = dialog.getLlegadaData();
                    // updatedData[0] = Producto
                    // updatedData[1] = Cantidad
                    // updatedData[2] = Fecha (String yyyy-MM-dd)

                    try {
                        Date sqlDate = Date.valueOf(updatedData[2].toString());
                        llegadaDAO.update(id, updatedData[0].toString(), updatedData[1].toString(), sqlDate);
                        loadLlegadas(); // Recargar la tabla
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(owner, "Error al actualizar la llegada: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(owner, "Error en el formato de datos del diálogo. " + ex.getMessage(), "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // --- (ACCIÓN ELIMINAR - CONECTADA A BD) ---
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                int response = JOptionPane.showConfirmDialog(owner, "¿Eliminar esta llegada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    int modelRow = tableRef.convertRowIndexToModel(row);
                    int id = (Integer) modelRef.getValueAt(modelRow, 0);

                    try {
                        llegadaDAO.delete(id);
                        loadLlegadas(); // Recargar la tabla
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(owner, "Error al eliminar la llegada: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
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


    // --- (Estas clases ya no las usa la pestaña "Productos") ---

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
                // ... (Esta es la lógica antigua de editar productos, la dejamos)
                fireEditingStopped();
                int modelRow = productosTable.convertRowIndexToModel(row);
                Object idObj = model.getValueAt(modelRow, 0);
                int id = -1;
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj != null) { try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; } }

                String currentName = model.getValueAt(modelRow, 1).toString();
                String newName = JOptionPane.showInputDialog(FrutasVerdurasPanel.this.parentFrame, "Editar nombre:", currentName);
                if (newName != null && !newName.trim().isEmpty()) {
                    model.setValueAt(newName.trim(), modelRow, 1);
                    if (id > 0) {
                        try {
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
                            // ignorar
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