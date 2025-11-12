package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
// --- ¡NUEVAS IMPORTACIONES! ---
import com.abarreyapp.dao.MermaDAO;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
// --- FIN DE IMPORTACIONES ---

public class MermaPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JFrame frame; // Frame padre
    private MermaDAO mermaDAO; // El DAO para la base de datos

    public MermaPanel(JFrame owner) {
        this.frame = owner;
        this.mermaDAO = new MermaDAO(); // Inicializamos el DAO

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
        // --- COLUMNAS MODIFICADAS (AÑADIMOS "ID") ---
        String[] columnNames = {"ID", "Fruta/Verdura", "Peso a Mermar (kg)", "Fecha", "Acciones"};

        model = new DefaultTableModel(columnNames, 0) { // Empezamos con 0 filas
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // La columna de Acciones es la 4 ahora
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- OCULTAR LA NUEVA COLUMNA "ID" (COLUMNA 0) ---
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // --- LAS ACCIONES AHORA ESTÁN EN LA COLUMNA 4 ---
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), owner));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- ACCIÓN DE BOTÓN NUEVO (MODIFICADA) ---
        btnNuevo.addActionListener(e -> {
            AddMermaDialog dialog = new AddMermaDialog(owner);
            dialog.setTitle("Registrar Merma");
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Object[] rowData = dialog.getMermaData();
                // rowData[0] = Producto (String)
                // rowData[1] = Cantidad (String)
                // rowData[3] = Fecha (String yyyy-MM-dd)

                try {
                    // Convertimos la fecha String a java.sql.Date
                    Date sqlDate = Date.valueOf(rowData[3].toString());

                    // Llamamos al DAO para insertar
                    mermaDAO.insert(rowData[0].toString(), rowData[1].toString(), sqlDate);

                    // Recargamos la tabla desde la BD
                    loadMermas();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al guardar en la base de datos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Error en el formato de la fecha.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- CARGAMOS LOS DATOS DE LA BD AL INICIAR ---
        loadMermas();
    }

    /**
     * (NUEVO MÉTODO)
     * Carga/Recarga todos los registros de mermas desde la BD.
     */
    private void loadMermas() {
        // Limpiar la tabla
        model.setRowCount(0);

        try {
            List<Object[]> mermas = mermaDAO.findAll();
            for (Object[] merma : mermas) {
                // El DAO devuelve [ID, Producto, Cantidad, Fecha]
                // Añadimos "" al final para la columna de Acciones
                model.addRow(new Object[]{
                        merma[0], // ID
                        merma[1], // Producto
                        merma[2], // Cantidad
                        merma[3], // Fecha (String)
                        ""        // Columna de Acciones
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar los datos de mermas: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Editar");
        private final JButton deleteButton = new JButton("Eliminar");

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

    // --- EDITOR (MODIFICADO PARA USAR LA BD) ---
    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        private int row;
        private JFrame frame;

        public ButtonEditor(JCheckBox checkBox, JFrame frame) {
            super(checkBox);
            this.frame = frame;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            JButton editButton = new JButton("Editar");
            JButton deleteButton = new JButton("Eliminar");

            panel.add(editButton);
            panel.add(deleteButton);

            // --- ACCIÓN DE EDITAR (MODIFICADA) ---
            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = table.convertRowIndexToModel(this.row);

                // Obtenemos los datos de la fila (col 0=ID, 1=Prod, 2=Peso, 3=Fecha)
                int id = (Integer) model.getValueAt(modelRow, 0);
                String currentProduct = model.getValueAt(modelRow, 1).toString();
                String currentWeight = model.getValueAt(modelRow, 2).toString();
                String currentDate = model.getValueAt(modelRow, 3).toString(); // Fecha es yyyy-MM-dd

                // Abrimos el diálogo con los datos
                AddMermaDialog dialog = new AddMermaDialog(frame, currentProduct, currentWeight, currentDate);
                dialog.setTitle("Editar Merma");
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Object[] updatedData = dialog.getMermaData();
                    // updatedData[0] = Producto
                    // updatedData[1] = Cantidad
                    // updatedData[3] = Fecha (String yyyy-MM-dd)

                    try {
                        // Convertimos la fecha String a java.sql.Date
                        Date sqlDate = Date.valueOf(updatedData[3].toString());

                        // Llamamos al DAO para actualizar
                        mermaDAO.update(id, updatedData[0].toString(), updatedData[1].toString(), sqlDate);

                        // Recargamos la tabla
                        loadMermas();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error al actualizar en la base de datos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame, "Error en el formato de la fecha.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // --- ACCIÓN DE ELIMINAR (MODIFICADA) ---
            deleteButton.addActionListener(e -> {
                fireEditingStopped();

                int response = JOptionPane.showConfirmDialog(
                        frame,
                        "¿Estás seguro de que quieres eliminar este registro?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    int modelRow = table.convertRowIndexToModel(this.row);
                    // Obtenemos el ID de la columna 0
                    int id = (Integer) model.getValueAt(modelRow, 0);

                    try {
                        // Llamamos al DAO para eliminar
                        mermaDAO.delete(id);

                        // Recargamos la tabla (o podemos solo quitar la fila)
                        // Es más seguro recargar por si acaso
                        loadMermas();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error al eliminar de la base de datos: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
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
        public Object getCellEditorValue() {
            return "";
        }
    }
}