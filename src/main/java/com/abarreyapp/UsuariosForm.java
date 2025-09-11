package com.abarreyapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class UsuariosForm extends JFrame {

    public UsuariosForm(String username) {
        setTitle("Gestión de Usuarios");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        // topPanel.setBackground(Color.WHITE); // El tema lo gestiona
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(40, 167, 69));
        logoPanel.setPreferredSize(new Dimension(180, 50));
        topPanel.add(logoPanel, BorderLayout.WEST);

        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
        JLabel welcomeLabel = new JLabel(capitalizedUsername + ", ¡bienvenido!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Panel para el interruptor de tema
        JPanel themeSwitcherPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JToggleButton themeToggleButton = new JToggleButton("Modo Oscuro");
        themeToggleButton.setSelected(UIManager.getLookAndFeel() instanceof FlatDarkLaf);
        themeToggleButton.addActionListener(e -> {
            try {
                if (themeToggleButton.isSelected()) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
                }
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        themeSwitcherPanel.add(themeToggleButton);
        topPanel.add(themeSwitcherPanel, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH);

        // Panel de navegación izquierdo
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(52, 58, 64));
        navPanel.setPreferredSize(new Dimension(180, getHeight()));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] navItems = {"Usuarios", "Frutas y Verduras", "Merma"};
        String[] iconNames = {"users.png", "fruits.png", "waste.png"};

        for (int i = 0; i < navItems.length; i++) {
            String item = navItems[i];
            String iconName = iconNames[i];

            JButton button = new JButton(item);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(52, 58, 64));
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setIconTextGap(15);

            try {
                URL iconUrl = getClass().getResource("/icons/" + iconName);
                if (iconUrl != null) {
                    Image icon = ImageIO.read(iconUrl);
                    Image scaledIcon = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaledIcon));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Podrías poner un icono por defecto o simplemente no hacer nada
            }

            if (item.equals("Usuarios")) {
                button.setBackground(new Color(40, 167, 69));
            }
            navPanel.add(button);
            navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        add(navPanel, BorderLayout.WEST);

        // Panel de contenido principal
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        // mainContentPanel.setBackground(Color.WHITE); // El tema lo gestiona
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabecera del contenido
        JPanel headerPanel = new JPanel(new BorderLayout());
        // headerPanel.setBackground(Color.WHITE); // El tema lo gestiona
        JLabel titleLabel = new JLabel("Usuarios");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnNuevo = new JButton("+ Nuevo");
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 12));
        // Eliminamos los colores fijos para que el tema los gestione
        // btnNuevo.setBackground(new Color(0, 123, 255));
        // btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);

        // --- INICIO DE LA SECCIÓN DE LA TABLA ---

        // Datos y modelo de la tabla
        String[] columnNames = {"Nombre", "Rol", "Correo", "Teléfono"};
        Object[][] data = {
            {"Nancy Daniela Arce Jiménez", "Administrador", "nancyataniela@gmail.com", "662139401"},
            {"Darío Ruíz Álvarez", "Gerente", "darioruizalv@gmail.com", "6622384950"},
            {"Laura Barragán Montalvo", "Usuario", "lauritabarrag@gmail.com", "6621483374"},
            {"Miguel Montaño Carrera", "Usuario", "miguelmonta@gmail.com", "6621394919"},
            {"Ximena Contreras Juárez", "Usuario", "ximenacontri@gmail.com", "6621392339"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false; // Hace que las celdas no sean editables
            }
        };
        
        // Creación y configuración de la tabla
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        // table.setGridColor(new Color(221, 221, 221)); // El tema lo gestiona
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Estilo de la cabecera de la tabla (gestionado por FlatLaf)
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        // header.setBackground(new Color(242, 242, 242));
        // header.setForeground(Color.BLACK);
        // header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(221, 221, 221)));
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

        // FlatLaf gestiona el "zebra-striping" y la selección, por lo que el renderizador personalizado ya no es necesario para eso.
        // Si se quisiera un padding específico, se podría usar un renderizador más simple.
        // Por ahora, dejamos que FlatLaf haga su magia para el modo oscuro.
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
             @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        });


        // Ajustar ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(250); // Nombre
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Rol
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Correo
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Teléfono

        // Panel con scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(table);
        // scrollPane.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221))); // El tema lo gestiona
        
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // --- INICIO DE LA SECCIÓN DEL BOTÓN DE SALIR ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalir = new JButton("Cerrar Sesión");

        btnSalir.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                UsuariosForm.this, 
                "¿Estás seguro de que quieres cerrar la sesión?", 
                "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                dispose(); // Cierra la ventana actual
                new LoginForm().setVisible(true); // Abre una nueva ventana de login
            }
        });

        bottomPanel.add(btnSalir);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
        // --- FIN DE LA SECCIÓN DEL BOTÓN DE SALIR ---

        // --- FIN DE LA SECCIÓN DE LA TABLA ---

        add(mainContentPanel, BorderLayout.CENTER);
    }
}
