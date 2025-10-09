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
    private Color highlightColor = null; // lazy theme-aware
    private JFrame parentFrame;

    public UsuariosPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Cabecera (título a la izquierda, botón +Nuevo a la derecha)
    setBackground(new Color(245, 246, 248)); // fondo claro general
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setOpaque(false);
    JLabel titleLabel = new JLabel("Usuarios");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    // Placeholder for username removed from header (we don't show the "admin, ¡bienvenido!" subtitle here)
    titlePanel.add(titleLabel);
    titlePanel.add(Box.createRigidArea(new Dimension(0,6)));
    headerPanel.add(titlePanel, BorderLayout.WEST);

    // Forzar tema claro
        FlatLightLaf.setup();
    RoundedButton newBtn = new RoundedButton("+ Nuevo", new Color(40,167,69), Color.WHITE); // green
    JPanel rightControls = new JPanel();
    rightControls.setOpaque(false);
    rightControls.add(Box.createRigidArea(new Dimension(8,0)));
    rightControls.add(newBtn);

    // Acción para añadir un nuevo usuario: abrir diálogo y, si confirma, agregar la fila
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
                // reload full model so row ordering and sorting is consistent
                reload();
                // select and scroll to the newly added row (match by id)
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
                // fallback a memoria: añadir fila y seleccionarla al final
                model.addRow(new Object[]{-1, name, role, email, phone, ""});
                int viewRow = table.convertRowIndexToView(model.getRowCount() - 1);
                table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
            }
        }
    });
    headerPanel.add(rightControls, BorderLayout.EAST);
    add(headerPanel, BorderLayout.NORTH);

    // Área superior de acciones (búsqueda eliminada según solicitud). Mantener un separador vacío para el espaciado visual.
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

    // Intentar cargar usuarios desde la base de datos, fallback a datos de ejemplo
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
            // no sobrescribir fondos de los renderers; permitir que el fondo de selección se pinte para celdas que no son badges
            return c;
        }
    };
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.setRowHeight(48);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // Selección de fila completa verde pálido para que los badges sigan visibles encima
    Color paleGreen = new Color(230, 245, 235);
    table.setSelectionBackground(paleGreen);
    table.setSelectionForeground(UIManager.getColor("Label.foreground"));

    // Mantener la selección por fila como predeterminada, pero permitir selección temporal de celda cuando
    // el usuario hace clic específicamente en la columna de correo para que la celda de correo muestre
    // el fondo de selección solo cuando esté explícitamente seleccionada.
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            int viewRow = table.rowAtPoint(e.getPoint());
            int viewCol = table.columnAtPoint(e.getPoint());
            if (viewRow < 0 || viewCol < 0) return;
            // Email column is index 3 (view index)
            if (viewCol == 3) {
                // enable column selection and select the specific cell
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionInterval(viewRow, viewRow);
                table.setColumnSelectionInterval(viewCol, viewCol);
            } else {
                // ensure we are in full-row selection mode
                table.setColumnSelectionAllowed(false);
                table.setRowSelectionInterval(viewRow, viewRow);
            }
        }
    });

    // Ocultar columna ID
    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
    table.getColumnModel().getColumn(0).setWidth(0);

    // La columna de acciones ahora está en el índice 5
    table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
    table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
    // Renderers para badge de rol y enlace de correo (índices desplazados)
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

    // Usar un row sorter para mejor UX y rendimiento en filtrado/ordenamiento
    javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
    table.setRowSorter(sorter);
    table.setDefaultRenderer(Object.class, new HighlightRenderer());

    // (Búsqueda eliminada) no se mantiene el atajo Ctrl+F

    // Anchos de columna (saltar columna id oculta en 0)
    table.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre
    table.getColumnModel().getColumn(2).setPreferredWidth(120); // Rol
    table.getColumnModel().getColumn(3).setPreferredWidth(250); // Correo
    table.getColumnModel().getColumn(4).setPreferredWidth(120); // Teléfono
    table.getColumnModel().getColumn(5).setPreferredWidth(180); // Acciones

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    // separador sutil: establecer borde al renderer de fila
    table.setDefaultRenderer(Object.class, new HighlightRenderer());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

    // Panel tipo tarjeta (blanco y redondeado) que contiene la tabla
        RoundedPanel card = new RoundedPanel(12, new Color(255,255,255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.add(topActionPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    // Recargar datos de la tabla desde la base de datos (considerando sucursal)
    public void reload() {
    // limpiar filas del modelo
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) model.removeRow(i);
    // repoblar
        UserDAO userDAO = new UserDAO();
        try {
            for (User u : userDAO.findAll()) {
                model.addRow(new Object[]{u.getId(), u.getName(), u.getRole(), u.getEmail(), u.getPhone(), ""});
            }
        } catch (SQLException ex) {
            // keep empty or fallback sample data omitted
        }
    }

    // Botón redondeado simple para apariencia consistente
    static class RoundedButton extends JButton {
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
            // subtle shadow
            int shadow = 6;
            for (int i = 0; i < shadow; i++) {
                float a = (float)(shadow - i) / (shadow * 6f);
                g2.setColor(new Color(0,0,0, Math.min(1.0f, a)));
                g2.fillRoundRect(i, i, getWidth()-i*2, getHeight()-i*2, 16, 16);
            }
            // draw button
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            setForeground(fg);
            super.paintComponent(g);
        }
    }

    // Renderer para mostrar el rol como una etiqueta (badge)
    class RoleBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setOpaque(true);
            String role = value == null ? "" : value.toString();
            lbl.setText(role);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            // mapeo explícito de colores: admin=rojo, gerente=naranja, usuario=azul
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

    // Renderer para mostrar el correo como enlace
    class EmailRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText("<html><a href=\"#\">" + escapeHtml(value == null ? "" : value.toString()) + "</a></html>");
            Color link = UIManager.getColor("Link.foreground");
            if (link == null) link = new Color(10,132,255);
            lbl.setForeground(link);
            // Hacer la etiqueta no opaca cuando la celda no está seleccionada para que el fondo
            // de selección (verde pálido) se vea y el enlace no aparezca como caja blanca. Pintar
            // fondo solo cuando la celda esté explícitamente seleccionada.
            boolean cellSelected = table.isCellSelected(row, column);
            lbl.setOpaque(cellSelected);
            if (cellSelected) {
                lbl.setBackground(table.getSelectionBackground());
            } else {
                // asegurar que no se pinte un fondo explícito para que el fondo de la fila
                // (selección o normal) subyacente permanezca visible
                lbl.setBackground(null);
            }
            return lbl;
        }
    }

    // Panel con fondo blanco y esquinas redondeadas
    static class RoundedPanel extends JPanel {
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
            // dibujar sombra suave
            int shadowSize = 8;
            for (int i = shadowSize; i >= 1; i--) {
                float alpha = 0.08f * (shadowSize - i + 1);
                g2.setColor(new Color(0,0,0, Math.min(1.0f, alpha)));
                int offset = i/2;
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(offset, offset, getWidth()-offset*2, getHeight()-offset*2, radius, radius);
                g2.fill(rr);
            }
            // dibujar la tarjeta
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class HighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Renderizaremos el contenido de texto, posiblemente con resaltado inline usando HTML si es necesario
            String text = value == null ? "" : value.toString();
            int modelRow = table.convertRowIndexToModel(row);

            // estilo base del componente
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            setOpaque(true);

            boolean isHighlightedRow = highlightedModelRows.contains(modelRow);

            if (isHighlightedRow && lastSearchText != null && !lastSearchText.isEmpty()) {
                // Reemplazo simple insensible a mayúsculas/minúsculas usando span HTML (escapar caracteres básicos)
                try {
                    // Construir HTML con segmentos escapados y coincidencias envueltas
                    Pattern p = Pattern.compile(Pattern.quote(lastSearchText), Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher m = p.matcher(text);
                    StringBuilder sb = new StringBuilder();
                    int last = 0;
                    while (m.find()) {
                        // añadir segmento escapado antes de la coincidencia
                        sb.append(escapeHtml(text.substring(last, m.start())));
                        // añadir texto coincidente escapado envuelto en span
                        sb.append("<span style=\"background-color:#FFF9C4;\">");
                        sb.append(escapeHtml(m.group()));
                        sb.append("</span>");
                        last = m.end();
                    }
                    sb.append(escapeHtml(text.substring(last)));
                    setText("<html>" + sb.toString() + "</html>");
                } catch (Throwable ex) {
                    // Fallback: texto plano con color de fondo
                    setText(text);
                    setBackground(new Color(255, 249, 196));
                }
            } else {
                setText(text);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(table.getBackground());
                }
            }
            // Alinear texto
            setHorizontalAlignment(JLabel.LEFT);
            return this;
        }
    }

    // Escapar HTML básico
    private String escapeHtml(String s) {
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
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) return false;
        double lum = 0.2126 * bg.getRed() + 0.7152 * bg.getGreen() + 0.0722 * bg.getBlue();
        return lum < 128;
    }

    // Indicador de selección eliminado; usar selección de fila completa verde pálido

    // (Métodos no usados relacionados con resaltado de búsqueda y parpadeo de campos eliminados)

    // Clases internas para los botones en la tabla
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton = new JButton("Editar");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
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
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Editar");
            editButton.setFont(new Font("Arial", Font.PLAIN, 12));
            panel.add(editButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = UsuariosPanel.this.table.convertRowIndexToModel(row);
                // Prepare data expected by AddUserDialog: {name, role, email, phone}
                Object[] rowData = new Object[4];
                rowData[0] = model.getValueAt(modelRow, 1); // Nombre
                rowData[1] = model.getValueAt(modelRow, 2); // Rol
                rowData[2] = model.getValueAt(modelRow, 3); // Correo
                rowData[3] = model.getValueAt(modelRow, 4); // Teléfono

                AddUserDialog dialog = new AddUserDialog(UsuariosPanel.this.parentFrame, "Editar Usuario", rowData);
                dialog.setVisible(true);
                System.out.println("UsuariosPanel: dialog closed. isDeleted=" + dialog.isDeleted() + " isConfirmed=" + dialog.isConfirmed());

                // If the dialog signaled deletion, perform delete
                if (dialog.isDeleted()) {
                    // ID is in column 0
                    Object idObj = model.getValueAt(modelRow, 0);
                    int id = -1;
                    if (idObj instanceof Number) id = ((Number) idObj).intValue();
                    else if (idObj != null) {
                        try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; }
                    }

                    if (id > 0) {
                        UserDAO dao = new UserDAO();
                        try {
                            boolean del = dao.delete(id);
                            if (!del) {
                                JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "No se pudo eliminar el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "Error al eliminar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    model.removeRow(modelRow);
                    return;
                }

                if (dialog.isConfirmed()) {
                    Object[] updatedData = dialog.getUserData();
                    // persist to DB if possible
                    Object idObj = model.getValueAt(modelRow, 0);
                    int id = -1;
                    if (idObj instanceof Number) id = ((Number) idObj).intValue();
                    else if (idObj != null) {
                        try { id = Integer.parseInt(idObj.toString()); } catch (NumberFormatException ex) { id = -1; }
                    }

                    String name = updatedData.length>0 ? (String)updatedData[0] : "";
                    String role = updatedData.length>1 ? (String)updatedData[1] : "";
                    String email = updatedData.length>2 ? (String)updatedData[2] : "";
                    String phone = updatedData.length>3 ? (String)updatedData[3] : "";

                    if (id > 0) {
                        User u = new User();
                        u.setId(id);
                        u.setName(name);
                        u.setRole(role);
                        u.setEmail(email);
                        u.setPhone(phone);
                        UserDAO dao = new UserDAO();
                        try {
                            System.out.println("UsuariosPanel: attempting update id=" + id + " name=" + name);
                            boolean ok = dao.update(u);
                            if (!ok) {
                                JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "No se pudo actualizar el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                // update model columns (skip ID at column 0). updatedData[0] -> col 1
                                for (int i = 0; i < updatedData.length; i++) {
                                    model.setValueAt(updatedData[i], modelRow, i + 1);
                                }
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(UsuariosPanel.this.parentFrame, "Error al actualizar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // fallback row (no DB id), update table columns 1..4
                        for (int i = 0; i < updatedData.length; i++) {
                            model.setValueAt(updatedData[i], modelRow, i + 1);
                        }
                    }
                }
            });

            // deletion moved inside edit dialog; no row-level delete here
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
