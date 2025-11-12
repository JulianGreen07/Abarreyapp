package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import com.abarreyapp.dao.UserDAO;
import com.abarreyapp.model.User;
import java.sql.SQLException;

public class UsuariosPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private final Set<Integer> highlightedModelRows = new HashSet<>();
    private String lastSearchText = "";
    private Color highlightColor = null;
    private JFrame parentFrame;

    public UsuariosPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabecera
        setBackground(new Color(245, 246, 248));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Usuarios");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0,6)));
        headerPanel.add(titlePanel, BorderLayout.WEST);


        FlatLightLaf.setup();
        RoundedButton newBtn = new RoundedButton("+ Nuevo", new Color(40,167,69), Color.WHITE); // green
        JPanel rightControls = new JPanel();
        rightControls.setOpaque(false);
        rightControls.add(Box.createRigidArea(new Dimension(8,0)));
        rightControls.add(newBtn);

        // Acción para añadir un nuevo usuario
        newBtn.addActionListener(e -> {
            AddUserDialog dialog = new AddUserDialog(UsuariosPanel.this.parentFrame, "Agregar Usuario", null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Object[] userData = dialog.getUserData();
                String name = userData.length > 0 ? userData[0].toString() : "";
                String role = userData.length > 1 ? userData[1].toString() : "";
                String email = userData.length > 2 ? userData[2].toString() : "";
                String phone = userData.length > 3 ? userData[3].toString() : "";

                UserDAO dao = new UserDAO();
                try {
                    User u = new User();
                    u.setName(name);
                    u.setRole(role);
                    u.setEmail(email);
                    u.setPhone(phone);
                    int newId = dao.insert(u);
                    reload();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        Object idObj = model.getValueAt(i, 0);
                        if (idObj != null) {
                            try {
                                int id = Integer.parseInt(idObj.toString());
                                if (id == newId) {
                                    int viewRow = table.convertRowIndexToView(i);
                                    table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                                    table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                                    break;
                                }
                            } catch (NumberFormatException nfe) {}
                        }
                    }
                } catch (SQLException ex) {
                    model.addRow(new Object[]{-1, name, role, email, phone, ""});
                    int viewRow = table.convertRowIndexToView(model.getRowCount() - 1);
                    table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                    table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                }
            }
        });
        headerPanel.add(rightControls, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        JPanel topActionPanel = new JPanel(new BorderLayout());
        topActionPanel.setOpaque(false);
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, 8));
        topActionPanel.add(spacer, BorderLayout.CENTER);

        String[] columnNames = {"ID","Nombre", "Rol", "Correo", "Teléfono", "Acciones"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        // Cargar usuarios desde la base de datos
        UserDAO userDAO = new UserDAO();
        try {
            for (User u : userDAO.findAll()) {
                model.addRow(new Object[]{u.getId(), u.getName(), u.getRole(), u.getEmail(), u.getPhone(), ""});
            }
        } catch (SQLException ex) {
            // datos de ejemplo como fallback
            Object[][] data = {
                    {-1, "Nancy Daniela Arce Jiménez", "Administrador", "nancyataniela@gmail.com", "662139401", ""},
                    {-1, "Darío Ruíz Álvarez", "Gerente", "darioruizalv@gmail.com", "6622384950", ""},
                    {-1, "Laura Barragán Montalvo", "Usuario", "lauritabarrag@gmail.com", "6621483374", ""},
                    {-1, "Miguel Montaño Carrera", "Usuario", "miguelmonta@gmail.com", "6621394919", ""},
                    {-1, "Ximena Contreras Juárez", "Usuario", "ximenacontri@gmail.com", "6621392339", ""}
            };
            for (Object[] r : data) model.addRow(r);
        }

        this.table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Color paleGreen = new Color(230, 245, 235);
        table.setSelectionBackground(paleGreen);
        table.setSelectionForeground(UIManager.getColor("Label.foreground"));


        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int viewRow = table.rowAtPoint(e.getPoint());
                int viewCol = table.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol < 0) return;

                if (viewCol == 3) { // Columna Email
                    table.setColumnSelectionAllowed(true);
                    table.setRowSelectionInterval(viewRow, viewRow);
                    table.setColumnSelectionInterval(viewCol, viewCol);
                } else {
                    table.setColumnSelectionAllowed(false);
                    table.setRowSelectionInterval(viewRow, viewRow);
                }
            }
        });

        // Ocultar columna ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);


        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        table.getColumnModel().getColumn(2).setCellRenderer(new RoleBadgeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new EmailRenderer());

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248,248,248));
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


        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setDefaultRenderer(Object.class, new HighlightRenderer());


        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Rol
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Correo
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Teléfono
        table.getColumnModel().getColumn(5).setPreferredWidth(180); // Acciones

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        table.setDefaultRenderer(Object.class, new HighlightRenderer());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());


        RoundedPanel card = new RoundedPanel(12, new Color(255,255,255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.add(topActionPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }


    public void reload() {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) model.removeRow(i);

        UserDAO userDAO = new UserDAO();
        try {
            for (User u : userDAO.findAll()) {
                model.addRow(new Object[]{u.getId(), u.getName(), u.getRole(), u.getEmail(), u.getPhone(), ""});
            }
        } catch (SQLException ex) {
            // ...
        }
    }


    static class RoundedButton extends JButton {
        // ... (Tu código de RoundedButton se queda igual)
        private Color bg;
        private Color fg;
        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.fg = fg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            setForeground(fg);
            super.paintComponent(g);
        }
    }


    class RoleBadgeRenderer extends DefaultTableCellRenderer {
        // ... (Tu código de RoleBadgeRenderer se queda igual)
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setOpaque(true);
            String role = value == null ? "" : value.toString();
            lbl.setText(role);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            boolean dark = isDarkMode();
            Color bg;
            Color fg = dark ? Color.WHITE : new Color(44,44,44);
            if (role.toLowerCase().contains("admin")) {
                bg = dark ? new Color(155, 38, 34) : new Color(255, 205, 210); // red shades
            } else if (role.toLowerCase().contains("gerente")) {
                bg = dark ? new Color(204, 120, 20) : new Color(255, 224, 178); // orange shades
            } else {
                bg = dark ? new Color(10, 120, 210) : new Color(187, 222, 251); // blue shades
            }
            lbl.setForeground(fg);
            lbl.setBackground(bg);
            lbl.setOpaque(true);
            return lbl;
        }
    }


    class EmailRenderer extends DefaultTableCellRenderer {
        // ... (Tu código de EmailRenderer se queda igual)
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText("<html><a href=\"#\">" + escapeHtml(value == null ? "" : value.toString()) + "</a></html>");
            Color link = UIManager.getColor("Link.foreground");
            if (link == null) link = new Color(10,132,255);
            lbl.setForeground(link);

            boolean cellSelected = table.isCellSelected(row, column);
            lbl.setOpaque(cellSelected);
            if (cellSelected) {
                lbl.setBackground(table.getSelectionBackground());
            } else {
                lbl.setBackground(null);
            }
            return lbl;
        }
    }


    static class RoundedPanel extends JPanel {
        // ... (Tu código de RoundedPanel se queda igual)
        private int radius;
        private Color backgroundColor;
        public RoundedPanel(int radius, Color bg) {
            super();
            this.radius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowSize = 8;
            for (int i = shadowSize; i >= 1; i--) {
                float alpha = 0.08f * (shadowSize - i + 1);
                g2.setColor(new Color(0,0,0, Math.min(1.0f, alpha)));
                int offset = i/2;
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(offset, offset, getWidth()-offset*2, getHeight()-offset*2, radius, radius);
                g2.fill(rr);
            }

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }


    class HighlightRenderer extends DefaultTableCellRenderer {
        // ... (Tu código de HighlightRenderer se queda igual)
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            String text = value == null ? "" : value.toString();
            int modelRow = table.convertRowIndexToModel(row);


            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            setOpaque(true);

            boolean isHighlightedRow = highlightedModelRows.contains(modelRow);

            if (isHighlightedRow && lastSearchText != null && !lastSearchText.isEmpty()) {

                try {
                    Pattern p = Pattern.compile(Pattern.quote(lastSearchText), Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher m = p.matcher(text);
                    StringBuilder sb = new StringBuilder();
                    int last = 0;
                    while (m.find()) {
                        sb.append(escapeHtml(text.substring(last, m.start())));
                        sb.append("<span style=\"background-color:#FFF9C4;\">");
                        sb.append(escapeHtml(m.group()));
                        sb.append("</span>");
                        last = m.end();
                    }
                    sb.append(escapeHtml(text.substring(last)));
                    setText("<html>" + sb.toString() + "</html>");
                } catch (Throwable ex) {
                    setText(text);
                    setBackground(new Color(255, 249, 196));
                }
            } else {
                setText(text);
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            }

            setHorizontalAlignment(JLabel.LEFT);
            return this;
        }
    }


    private String escapeHtml(String s) {
        // ... (Tu código de escapeHtml se queda igual)
        if (s == null) return "";
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': out.append("&amp;"); break;
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#39;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    private boolean isDarkMode() {
        // ... (Tu código de isDarkMode se queda igual)
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) return false;

        double lum = 0.2126 * bg.getRed() + 0.7152 * bg.getGreen() + 0.0722 * bg.getBlue();
        return lum < 128;
    }


    class ButtonRenderer extends JPanel implements TableCellRenderer {
        // ... (Tu código de ButtonRenderer se queda igual)
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

    // --- (AQUÍ ESTÁ TODO EL CÓDIGO CORREGIDO) ---
    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton editButton;
        protected JButton deleteButton;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Editar");
            deleteButton = new JButton("Eliminar");

            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
            deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));

            panel.add(editButton);
            panel.add(deleteButton);

            // --- LÓGICA DE EDITAR (CORREGIDA) ---
            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = UsuariosPanel.this.table.convertRowIndexToModel(row);

                // Cargar datos existentes para el diálogo
                // Col 0=ID, 1=Nombre, 2=Rol, 3=Email, 4=Teléfono
                Object[] rowData = new Object[model.getColumnCount() - 1]; // Pre-cargar 5 columnas
                for (int i = 0; i < rowData.length; i++) {
                    rowData[i] = model.getValueAt(modelRow, i);
                }

                AddUserDialog dialog = new AddUserDialog(UsuariosPanel.this.parentFrame, "Editar Usuario", rowData);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    // 1. Obtener los datos actualizados del diálogo
                    Object[] updatedData = dialog.getUserData(); // [Name, Role, Email, Phone]

                    // 2. Obtener el ID del usuario
                    int userId = -1;
                    try {
                        // El ID está en la columna 0 del modelo
                        userId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "Error: No se pudo encontrar el ID del usuario.");
                        return; // Salir si no hay ID
                    }

                    // 3. Crear el objeto User con los datos NUEVOS y el ID
                    User userToUpdate = new User();
                    userToUpdate.setId(userId);
                    userToUpdate.setName(updatedData[0].toString());
                    userToUpdate.setRole(updatedData[1].toString());
                    userToUpdate.setEmail(updatedData[2].toString());
                    userToUpdate.setPhone(updatedData[3].toString());

                    // 4. Guardar en la base de datos
                    UserDAO dao = new UserDAO();
                    try {
                        // (Esto usa el método 'update' que ya tenías en UserDAO)
                        dao.update(userToUpdate);

                        // 5. SI SE GUARDÓ, actualizar la tabla visual (correctamente)
                        model.setValueAt(updatedData[0], modelRow, 1); // Columna 1 es Nombre
                        model.setValueAt(updatedData[1], modelRow, 2); // Columna 2 es Rol
                        model.setValueAt(updatedData[2], modelRow, 3); // Columna 3 es Correo
                        model.setValueAt(updatedData[3], modelRow, 4); // Columna 4 es Teléfono

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "Error al guardar en la base de datos: " + ex.getMessage());
                    }
                }
            });

            // --- LÓGICA DE ELIMINAR (CORREGIDA) ---
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                int response = JOptionPane.showConfirmDialog(
                        UsuariosPanel.this.parentFrame,
                        "¿Estás seguro de que quieres eliminar a este usuario?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {

                    int modelRow = UsuariosPanel.this.table.convertRowIndexToModel(row);

                    try {
                        // 1. Obtener el ID de la columna 0
                        int userId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

                        // 2. Llamar al DAO para borrar de la base de datos
                        UserDAO dao = new UserDAO();
                        dao.delete(userId); // Esto usa el método que ya tenías en UserDAO

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame,
                                "Error al eliminar el usuario de la base de datos: " + ex.getMessage());
                        return; // No borres de la tabla si falló el borrado de la DB
                    }

                    // 3. Borrar de la tabla visual (esto ya lo tenías)
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