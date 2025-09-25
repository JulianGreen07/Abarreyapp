package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

    // Cabecera (titulo a la izquierda, boton +Nuevo a la derecha)
    setBackground(new Color(245, 246, 248)); // fondo claro general
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setOpaque(false);
    JLabel titleLabel = new JLabel("Usuarios");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    // Placeholder for username, as it's not passed from MainFrame
    String username = "Admin";
    String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
    JLabel subtitleLabel = new JLabel(capitalizedUsername + ", ¡bienvenido!");
    subtitleLabel.setForeground(new Color(120, 120, 120));
    titlePanel.add(titleLabel);
    titlePanel.add(Box.createRigidArea(new Dimension(0,6)));
    titlePanel.add(subtitleLabel);
    headerPanel.add(titlePanel, BorderLayout.WEST);

        JToggleButton darkToggle = new JToggleButton("Modo Oscuro");
        darkToggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        darkToggle.setSelected(isDarkMode());
        darkToggle.addActionListener(ae -> {
            try {
                if (darkToggle.isSelected()) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
                }
                // update UI of the top-level window
                java.awt.Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) SwingUtilities.updateComponentTreeUI(w);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    RoundedButton newBtn = new RoundedButton("+ Nuevo", new Color(0,112,255), Color.WHITE);
    JPanel rightControls = new JPanel();
    rightControls.setOpaque(false);
    rightControls.add(darkToggle);
    rightControls.add(Box.createRigidArea(new Dimension(8,0)));
    rightControls.add(newBtn);
    headerPanel.add(rightControls, BorderLayout.EAST);
    add(headerPanel, BorderLayout.NORTH);

        // Top action area (search removed as requested). Keep an empty spacer for visual padding.
        JPanel topActionPanel = new JPanel(new BorderLayout());
        topActionPanel.setOpaque(false);
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, 8));
        topActionPanel.add(spacer, BorderLayout.CENTER);

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
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.setRowHeight(48);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
    table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
    // Role badge and email link renderers
    table.getColumnModel().getColumn(1).setCellRenderer(new RoleBadgeRenderer());
    table.getColumnModel().getColumn(2).setCellRenderer(new EmailRenderer());

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

    // Use a row sorter for better UX and performance on filtering/sorting
    javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
    table.setRowSorter(sorter);
    table.setDefaultRenderer(Object.class, new HighlightRenderer());

    // (Search removed) no keybinding for Ctrl+F remains

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    // subtle separator: set row renderer border
    table.setDefaultRenderer(Object.class, new HighlightRenderer());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Card panel (white rounded) that holds search + table
        RoundedPanel card = new RoundedPanel(12, new Color(255,255,255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.add(topActionPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    // Simple rounded button for consistent look
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
            lbl.setOpaque(false);
            String role = value == null ? "" : value.toString();
            lbl.setText(role);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            lbl.setForeground(new Color(80,80,80));
            // simple color per role
            Color bg = new Color(230, 244, 255);
            if (role.toLowerCase().contains("admin")) bg = new Color(255, 235, 238);
            else if (role.toLowerCase().contains("gerente")) bg = new Color(255, 243, 224);
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
            lbl.setForeground(new Color(0, 112, 255));
            lbl.setOpaque(false);
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
            // draw soft shadow
            int shadowSize = 8;
            for (int i = shadowSize; i >= 1; i--) {
                float alpha = 0.08f * (shadowSize - i + 1);
                g2.setColor(new Color(0,0,0, Math.min(1.0f, alpha)));
                int offset = i/2;
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(offset, offset, getWidth()-offset*2, getHeight()-offset*2, radius, radius);
                g2.fill(rr);
            }
            // draw card
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
            // We'll render text content possibly with inline highlight using HTML if needed
            String text = value == null ? "" : value.toString();
            int modelRow = table.convertRowIndexToModel(row);

            // base component styling
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            setOpaque(true);

            boolean isHighlightedRow = highlightedModelRows.contains(modelRow);

            if (isHighlightedRow && lastSearchText != null && !lastSearchText.isEmpty()) {
                // Simple case-insensitive replace with HTML span (escape basic chars)
                try {
                    // Build HTML with escaped segments and wrapped matches
                    Pattern p = Pattern.compile(Pattern.quote(lastSearchText), Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher m = p.matcher(text);
                    StringBuilder sb = new StringBuilder();
                    int last = 0;
                    while (m.find()) {
                        // append escaped segment before match
                        sb.append(escapeHtml(text.substring(last, m.start())));
                        // append escaped matched text with span
                        sb.append("<span style=\"background-color:#FFF9C4;\">");
                        sb.append(escapeHtml(m.group()));
                        sb.append("</span>");
                        last = m.end();
                    }
                    sb.append(escapeHtml(text.substring(last)));
                    setText("<html>" + sb.toString() + "</html>");
                } catch (Throwable ex) {
                    // Fallback: plain text with background color
                    setText(text);
                    setBackground(new Color(255, 249, 196));
                }
            } else {
                setText(text);
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            }
            // Align text
            setHorizontalAlignment(JLabel.LEFT);
            return this;
        }
    }

    // Escape HTML básico
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
        // simple heuristic: dark if background luminance low
        double lum = 0.2126 * bg.getRed() + 0.7152 * bg.getGreen() + 0.0722 * bg.getBlue();
        return lum < 128;
    }

    // (Removed unused methods related to search highlighting and field flashing)

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

            editButton.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = UsuariosPanel.this.table.convertRowIndexToModel(row);
                Object[] rowData = new Object[model.getColumnCount() - 1];
                for (int i = 0; i < rowData.length; i++) {
                    rowData[i] = model.getValueAt(modelRow, i);
                }

                AddUserDialog dialog = new AddUserDialog(UsuariosPanel.this.parentFrame, "Editar Usuario", rowData);
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
                    UsuariosPanel.this.parentFrame,
                    "¿Estás seguro de que quieres eliminar a este usuario?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    int modelRow = UsuariosPanel.this.table.convertRowIndexToModel(row);
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