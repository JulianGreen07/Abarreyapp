package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private java.util.List<JButton> navButtons = new java.util.ArrayList<>();
    private final Color activeColor = new Color(40, 167, 69);
    private final Color defaultColor = new Color(52, 58, 64);
    private JLabel sectionSubtitleLabel;
    private java.util.Map<String, String> subtitles;
    private String username;

    public MainFrame(String username) {
        this.username = username;
        setTitle("Abarrey - Sistema de Gestión");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(activeColor);
        logoPanel.setPreferredSize(new Dimension(180, 50));
        JLabel logoLabel = new JLabel("Abarrey");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        topPanel.add(logoPanel, BorderLayout.WEST);

    sectionSubtitleLabel = new JLabel();
        sectionSubtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sectionSubtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    // Show welcome message with username on the top center
    String displayUser = (username != null && !username.isEmpty()) ? (Character.toUpperCase(username.charAt(0)) + username.substring(1)) : "Usuario";
    JLabel userLabel = new JLabel(displayUser + ", ¡bienvenido!");
    userLabel.setForeground(Color.WHITE);
    userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    JPanel centerTop = new JPanel(new BorderLayout());
    centerTop.setOpaque(false);
    centerTop.add(sectionSubtitleLabel, BorderLayout.NORTH);
    centerTop.add(userLabel, BorderLayout.SOUTH);
    topPanel.add(centerTop, BorderLayout.CENTER);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
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
        rightHeaderPanel.add(themeToggleButton);

        topPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Panel de navegación izquierdo
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(defaultColor);
        navPanel.setPreferredSize(new Dimension(180, getHeight()));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Panel principal con CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new UsuariosPanel(this), "Usuarios");
        mainPanel.add(new FrutasVerdurasPanel(this), "Frutas y Verduras");
        mainPanel.add(new MermaPanel(this), "Merma");
        mainPanel.add(new ReportesPanel(this), "Reportes");

        String[] navItems = {"Usuarios", "Frutas y Verduras", "Merma", "Reportes"};
        String[] iconNames = {"users.png", "fruits.png", "waste.png", "reports.png"};

        subtitles = new java.util.HashMap<>();
        subtitles.put("Usuarios", "Gestión de usuarios");
        subtitles.put("Frutas y Verduras", "Gestiona el inventario de frutas y verduras");
        subtitles.put("Merma", "Registra y gestiona la merma de productos");
        subtitles.put("Reportes", "Ver los reportes de inventario");

        for (int i = 0; i < navItems.length; i++) {
            final String item = navItems[i];
            String iconName = iconNames[i];

            JButton button = new JButton(item);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(defaultColor);
            button.setOpaque(true);
            button.setBorderPainted(false);
            
            try {
                URL iconUrl = getClass().getResource("/icons/" + iconName);
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                        button.setIcon(new ImageIcon(img));
                    }
                }
            } catch (Exception e) {
                // silently ignore
            }

            button.addActionListener(e -> {
                cardLayout.show(mainPanel, item);
                setActiveButton((JButton) e.getSource());
                sectionSubtitleLabel.setText(subtitles.get(item));
            });

            navPanel.add(button);
            navButtons.add(button);
            navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Set initial active button and subtitle
        if (!navButtons.isEmpty()) {
            setActiveButton(navButtons.get(0));
            sectionSubtitleLabel.setText(subtitles.get(navItems[0]));
        }

        add(navPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Panel inferior para el botón de cerrar sesión
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                MainFrame.this, 
                "¿Estás seguro de que quieres cerrar la sesión?", 
                "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
        bottomPanel.add(btnSalir);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setActiveButton(JButton activeButton) {
        for (JButton button : navButtons) {
            button.setBackground(defaultColor);
        }
        activeButton.setBackground(activeColor);
    }
}
