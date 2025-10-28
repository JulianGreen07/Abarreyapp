package com.abarreyapp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** UI updated to match the second screenshot: header + subtitle, placeholders,
 * soft input styling and styled Cancel/Add buttons.
 */
public class AddUserDialog extends JDialog {
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JComboBox<String> cmbRol = new JComboBox<>(new String[]{"Seleccione un rol", "Administrador", "Gerente", "Usuario"});
    // Inline error labels (initially blank)
    private final JLabel errNombre = new JLabel("");
    private final JLabel errRol = new JLabel("");
    private final JLabel errCorreo = new JLabel("");
    private final JLabel errTelefono = new JLabel("");
    private boolean confirmed = false;
    private boolean deleted = false;

    // Placeholder strings (used to detect placeholder vs real text)
    private final String phNombre = "Ingrese el nombre completo";
    private final String phCorreo = "ejemplo@gmail.com";
    private final String phTelefono = "662123456";

    public AddUserDialog(Frame owner, String title, Object[] data) {
    super(owner, title, true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // Remove native title bar (no system X button)
    setUndecorated(true);
    setSize(400, 440);
    setResizable(false);
    // Center on screen (not relative to the app window) so it appears perfectly centered
    setLocationRelativeTo(null);

        // We'll draw a rounded white panel in the center to emulate the modal card
    // Use a BorderLayout so we can place the modal in the center and buttons at the bottom
    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);

    RoundedPanel modal = new RoundedPanel(18, Color.WHITE, new Color(230,230,230));
    modal.setLayout(new BorderLayout(10,10));
    modal.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(230,230,230), 1, true),
        // Increase left padding to shift content to the right for visual alignment
        BorderFactory.createEmptyBorder(12,32,12,16)
    ));

    // Header inside modal (use the dialog title so it works for both Add and Edit)
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    JPanel titleWrap = new JPanel(new BorderLayout());
    titleWrap.setOpaque(false);
    JLabel titleLbl = new JLabel(getTitle());
    titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLbl.setHorizontalAlignment(SwingConstants.LEFT);
    titleWrap.add(titleLbl, BorderLayout.WEST);
    JLabel sub = new JLabel("Complete los campos para agregar un nuevo usuario al sistema.");
    sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    sub.setForeground(new Color(120,120,120));
    sub.setHorizontalAlignment(SwingConstants.LEFT);
    titleWrap.add(sub, BorderLayout.SOUTH);
    titleWrap.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));

    // No close button here; let the window manager provide the close affordance
    header.add(titleWrap, BorderLayout.CENTER);
    modal.add(header, BorderLayout.NORTH);

    // Fields area (vertical)
    JPanel fields = new JPanel();
    fields.setOpaque(false);
    fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
    // add slight left padding to ensure consistent alignment with buttons row
    fields.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

    styleField(txtNombre);
    styleField(txtCorreo);
    styleField(txtTelefono);
    cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

    fields.add(makeLabel("Nombre Completo"));
    addPlaceholder(txtNombre, phNombre);
    fields.add(txtNombre);
    // inline error label
    errNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    errNombre.setForeground(new Color(200,50,50));
    errNombre.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    errNombre.setVisible(false);
    fields.add(errNombre);
    fields.add(Box.createRigidArea(new Dimension(0,6)));

    fields.add(makeLabel("Rol"));
    // wrap combo to align width
    cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    fields.add(cmbRol);
    errRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    errRol.setForeground(new Color(200,50,50));
    errRol.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    errRol.setVisible(false);
    fields.add(errRol);
    fields.add(Box.createRigidArea(new Dimension(0,6)));

    fields.add(makeLabel("Correo Electrónico"));
    addPlaceholder(txtCorreo, phCorreo);
    fields.add(txtCorreo);
    errCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    errCorreo.setForeground(new Color(200,50,50));
    errCorreo.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    errCorreo.setVisible(false);
    fields.add(errCorreo);
    fields.add(Box.createRigidArea(new Dimension(0,6)));

    fields.add(makeLabel("Teléfono"));
    addPlaceholder(txtTelefono, phTelefono);
    fields.add(txtTelefono);
    errTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    errTelefono.setForeground(new Color(200,50,50));
    errTelefono.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    errTelefono.setVisible(false);
    fields.add(errTelefono);

    modal.add(fields, BorderLayout.CENTER);

        // Buttons area with subtle divider and rounded buttons
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(235,235,235));
        sep.setPreferredSize(new Dimension(0, 10));
        buttons.add(sep, BorderLayout.NORTH);

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btnRow.setOpaque(false);

        RoundedModalButton btnCancel = new RoundedModalButton("Cancelar", false);
        btnCancel.setPreferredSize(new Dimension(140,40));
        btnCancel.addActionListener(e -> dispose());

        RoundedModalButton btnAdd = new RoundedModalButton("Agregar Usuario", true);
        btnAdd.setPreferredSize(new Dimension(160,40));
        btnAdd.addActionListener(e -> onConfirm());

        btnRow.add(btnCancel);
        btnRow.add(btnAdd);
        buttons.add(btnRow, BorderLayout.CENTER);
        // Put buttons inside the modal so they appear attached to the white card
        modal.add(buttons, BorderLayout.SOUTH);
    // Add the modal card to the dialog content
    content.add(modal, BorderLayout.CENTER);

        // Populate data if editing
        if (data != null) {
            if (data.length > 0 && data[0] != null && !data[0].toString().isEmpty()) txtNombre.setText(data[0].toString());
            if (data.length > 1 && data[1] != null) cmbRol.setSelectedItem(data[1].toString());
            if (data.length > 2 && data[2] != null && !data[2].toString().isEmpty()) txtCorreo.setText(data[2].toString());
            if (data.length > 3 && data[3] != null && !data[3].toString().isEmpty()) txtTelefono.setText(data[3].toString());
        }

        setContentPane(content);
        // Request focus on first field
        SwingUtilities.invokeLater(() -> txtNombre.requestFocusInWindow());
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBorder(BorderFactory.createEmptyBorder(6,0,8,0));
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleField(JTextField f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(250,250,251));
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(225,225,225),1,true), BorderFactory.createEmptyBorder(8,12,8,12)));
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        // initialize placeholder only if empty
        if (field.getText().trim().isEmpty()) {
            field.setText(placeholder);
            field.setForeground(new Color(140,140,140));
        }
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(140,140,140));
                }
            }
        });
    }

    private void onConfirm() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String rol = cmbRol.getSelectedItem() == null ? "" : cmbRol.getSelectedItem().toString();

        // Treat placeholders as empty
        if (nombre.equals(phNombre)) nombre = "";
        if (correo.equals(phCorreo)) correo = "";
        if (telefono.equals(phTelefono)) telefono = "";

    // Clear previous inline errors
    errNombre.setText(""); errNombre.setVisible(false);
    errRol.setText(""); errRol.setVisible(false);
    errCorreo.setText(""); errCorreo.setVisible(false);
    errTelefono.setText(""); errTelefono.setVisible(false);

        boolean hasError = false;
        if (nombre.isEmpty()) {
            errNombre.setText("El nombre es obligatorio."); errNombre.setVisible(true);
            if (!hasError) { txtNombre.requestFocusInWindow(); hasError = true; }
        }
        if (rol.isEmpty() || rol.equals("Seleccione un rol")) {
            errRol.setText("Selecciona un rol."); errRol.setVisible(true);
            if (!hasError) { cmbRol.requestFocusInWindow(); hasError = true; }
        }
        if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errCorreo.setText("Introduce un correo válido."); errCorreo.setVisible(true);
            if (!hasError) { txtCorreo.requestFocusInWindow(); hasError = true; }
        }
        if (!telefono.isEmpty()) {
            String digitsOnly = telefono.replaceAll("\\D", "");
            if (digitsOnly.length() < 7) {
                errTelefono.setText("Introduce un teléfono válido (mínimo 7 dígitos)."); errTelefono.setVisible(true);
                if (!hasError) { txtTelefono.requestFocusInWindow(); hasError = true; }
            }
        }

        if (hasError) return;

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public boolean isDeleted() { return deleted; }

    // Test helper: programmatically trigger confirm (useful for unit testing)
    public void submitProgrammatically() {
        onConfirm();
    }

    // Test-friendly setters (package API) to allow programmatic tests to populate fields
    public void setNameField(String name) { txtNombre.setText(name); }
    public void setRoleSelection(String role) { cmbRol.setSelectedItem(role); }
    public void setEmailField(String email) { txtCorreo.setText(email); }
    public void setPhoneField(String phone) { txtTelefono.setText(phone); }

    public Object[] getUserData() {
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String telefono = txtTelefono.getText();
        if (nombre.equals(phNombre)) nombre = "";
        if (correo.equals(phCorreo)) correo = "";
        if (telefono.equals(phTelefono)) telefono = "";
        String telNorm = telefono.trim().isEmpty() ? "" : telefono.replaceAll("\\D", "");
        return new Object[]{ nombre.trim(), cmbRol.getSelectedItem() == null ? "" : cmbRol.getSelectedItem().toString(), correo.trim(), telNorm };
    }
}

    // Rounded button used inside the modal for a cleaner look
    class RoundedModalButton extends JButton {
        private final boolean primary;
        public RoundedModalButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int arc = 10;
            if (primary) {
                g2.setColor(new Color(0, 153, 76));
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(new Color(0, 130, 65));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
                setForeground(Color.WHITE);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(new Color(220,220,220));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
                setForeground(new Color(44,44,44));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

// Small rounded panel helper used to draw a white modal card with optional border
class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bg;
    private final Color borderColor;

    public RoundedPanel(int radius, Color background, Color borderColor) {
        super();
        this.radius = radius;
        this.bg = background;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // draw a soft outer shadow
        int shadowSize = 8;
        for (int i = shadowSize; i >= 1; i--) {
            float alpha = 0.03f * (shadowSize - i + 1);
            g2.setColor(new Color(0,0,0, Math.min(0.2f, alpha)));
            int offset = i/2;
            g2.fillRoundRect(offset, offset, w - offset*2, h - offset*2, radius, radius);
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, radius, radius);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w-1, h-1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
