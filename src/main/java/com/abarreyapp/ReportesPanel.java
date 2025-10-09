package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReportesPanel extends JPanel {

    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JLabel dateRangeLabel;
    private JPanel dateFieldsPanel;
    // hold references so we can refresh the checklist when needed
    private JPanel checkBoxesPanel;
    private java.util.List<JCheckBox> productChecks;
    private JLabel selectionLabel;

    public ReportesPanel(JFrame owner) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabecera
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        add(headerPanel, BorderLayout.NORTH);

        // Panel de Controles
        JPanel controlsContainer = new JPanel(new BorderLayout());
        controlsContainer.setBorder(BorderFactory.createTitledBorder("Generar Reporte"));
        
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topControls.add(new JLabel("Periodo:"));
        String[] periodos = {"Última semana", "Último mes", "Periodo personalizado"};
        JComboBox<String> periodoComboBox = new JComboBox<>(periodos);
        topControls.add(periodoComboBox);

        topControls.add(Box.createHorizontalStrut(20));

        dateRangeLabel = new JLabel();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        startDateField = new JFormattedTextField(sdf);
        startDateField.setColumns(10);
        endDateField = new JFormattedTextField(sdf);
        endDateField.setColumns(10);

        dateFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateFieldsPanel.add(new JLabel("Fecha de inicio:"));
        dateFieldsPanel.add(startDateField);
        dateFieldsPanel.add(new JLabel("Fecha de fin:"));
        dateFieldsPanel.add(endDateField);

        JPanel dateContainer = new JPanel(new CardLayout());
        dateContainer.add(dateRangeLabel, "label");
        dateContainer.add(dateFieldsPanel, "fields");

        topControls.add(dateContainer);

        updateDateRange(periodoComboBox.getSelectedItem().toString(), (CardLayout) dateContainer.getLayout(), dateContainer);

        periodoComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateDateRange(e.getItem().toString(), (CardLayout) dateContainer.getLayout(), dateContainer);
            }
        });


        JPanel middleControls = new JPanel(new BorderLayout());
        middleControls.setBorder(BorderFactory.createTitledBorder("Frutas y Verduras a incluir"));

        // initialize fields so other classes can request a reload
        this.checkBoxesPanel = new JPanel();
        this.checkBoxesPanel.setLayout(new BoxLayout(this.checkBoxesPanel, BoxLayout.Y_AXIS));
        this.productChecks = new java.util.ArrayList<>();
        // initial fill
        reloadProducts();
        
        JScrollPane scrollPane = new JScrollPane(checkBoxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        
        middleControls.add(scrollPane, BorderLayout.CENTER);
        
        JPanel selectionInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.selectionLabel = new JLabel(productChecks.size() + " de " + productChecks.size() + " productos seleccionados");
        selectionInfoPanel.add(this.selectionLabel);
        JButton deseleccionarBtn = new JButton("Deseleccionar todas");
        selectionInfoPanel.add(deseleccionarBtn, BorderLayout.EAST);
         middleControls.add(selectionInfoPanel, BorderLayout.SOUTH);

        deseleccionarBtn.addActionListener(e -> {
            for (JCheckBox cb : this.productChecks) cb.setSelected(false);
            this.selectionLabel.setText("0 de " + this.productChecks.size() + " productos seleccionados");
        });


        JButton generarReporteBtn = new JButton("Generar Reporte");
        generarReporteBtn.setBackground(new Color(40, 167, 69));
        generarReporteBtn.setForeground(Color.WHITE);

        JPanel mainControlsPanel = new JPanel();
        mainControlsPanel.setLayout(new BoxLayout(mainControlsPanel, BoxLayout.Y_AXIS));
        mainControlsPanel.add(topControls);
        mainControlsPanel.add(middleControls);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(generarReporteBtn);

        controlsContainer.add(mainControlsPanel, BorderLayout.CENTER);
        controlsContainer.add(bottomPanel, BorderLayout.SOUTH);

        add(controlsContainer, BorderLayout.CENTER);

        generarReporteBtn.addActionListener(e -> {
            try {
                // build set of selected product names
                java.util.Set<String> selected = new java.util.HashSet<>();
                java.util.List<String> allProductNames = new java.util.ArrayList<>();
                for (Component c : checkBoxesPanel.getComponents()) {
                    if (c instanceof JCheckBox) {
                        JCheckBox cb = (JCheckBox) c;
                        allProductNames.add(cb.getText());
                        if (cb.isSelected()) selected.add(cb.getText().trim().toLowerCase());
                    }
                }

                if (selected.isEmpty()) {
                    JOptionPane.showMessageDialog(owner, "Selecciona al menos un producto para generar el reporte.", "Generar Reporte", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Determine date range (from controls)
                java.util.Date startDate;
                java.util.Date endDate;
                if (dateFieldsPanel.isVisible()) {
                    startDate = (java.util.Date) startDateField.getValue();
                    endDate = (java.util.Date) endDateField.getValue();
                } else {
                    // read from dateRangeLabel text
                    // fallback: last month
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    endDate = cal.getTime(); cal.add(java.util.Calendar.MONTH, -1); startDate = cal.getTime();
                }

                // Query DB for mermas + arrivals in date range and filter by selected products
                com.abarreyapp.dao.MermaDAO mdao = new com.abarreyapp.dao.MermaDAO();
                com.abarreyapp.dao.ArrivalDAO adao = new com.abarreyapp.dao.ArrivalDAO();

                java.util.List<String[]> mermaRows = mdao.findByDateRange(startDate, endDate);
                java.util.List<String[]> arrivalRows = adao.findByDateRange(startDate, endDate);

                // Normalize and combine: each row will be {type, product, qty, recorded_at}
                java.util.List<String[]> combined = new java.util.ArrayList<>();
                for (String[] r : mermaRows) {
                    if (r.length >= 4) {
                        String prod = r[1] == null ? "" : r[1].trim().toLowerCase();
                        if (selected.contains(prod)) combined.add(new String[]{"Merma", r[1], r[2], r[3]});
                    }
                }
                for (String[] r : arrivalRows) {
                    if (r.length >= 4) {
                        String prod = r[1] == null ? "" : r[1].trim().toLowerCase();
                        if (selected.contains(prod)) combined.add(new String[]{"Llegada", r[1], r[2], r[3]});
                    }
                }

                // sort combined by recorded_at desc (string compare of timestamp works if DB format is yyyy-MM-dd...)
                combined.sort((a, b) -> b[3].compareTo(a[3]));

                // Build dialog
                JDialog dlg = new JDialog(owner, "Reporte de Inventario", true);
                dlg.setLayout(new BorderLayout());
                dlg.setSize(640, 360);
                dlg.setLocationRelativeTo(owner);

                JPanel header = new JPanel(new BorderLayout());
                header.setBorder(BorderFactory.createEmptyBorder(18,18,12,18));
                JLabel hTitle = new JLabel("Reporte de Inventario");
                hTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
                JLabel hSubtitle = new JLabel("Resumen de llegadas y merma para el período seleccionado (" + selected.size() + " productos incluidos)");
                hSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                hSubtitle.setForeground(new Color(120,120,120));
                JPanel headerText = new JPanel(); headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS)); headerText.setOpaque(false);
                headerText.add(hTitle); headerText.add(Box.createRigidArea(new Dimension(0,6))); headerText.add(hSubtitle);
                header.add(headerText, BorderLayout.WEST);
                dlg.add(header, BorderLayout.NORTH);

                JPanel content = new JPanel(new BorderLayout());
                content.setBorder(BorderFactory.createEmptyBorder(12,18,18,18));

                if (combined.isEmpty()) {
                    JPanel emptyPanel = new JPanel(); emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS)); emptyPanel.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));
                    JLabel msg = new JLabel("No hay datos para el período y productos seleccionados"); msg.setFont(new Font("Segoe UI", Font.PLAIN, 14)); msg.setForeground(new Color(120,120,120)); msg.setAlignmentX(Component.CENTER_ALIGNMENT);
                    JLabel hint = new JLabel("Verifica que existan registros de llegadas o merma para los productos y fechas elegidos"); hint.setFont(new Font("Segoe UI", Font.PLAIN, 12)); hint.setForeground(new Color(150,150,150)); hint.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyPanel.add(Box.createVerticalGlue()); emptyPanel.add(msg); emptyPanel.add(Box.createRigidArea(new Dimension(0,8))); emptyPanel.add(hint); emptyPanel.add(Box.createVerticalGlue());
                    content.add(emptyPanel, BorderLayout.CENTER);
                } else {
                    String[] cols = new String[]{"Tipo", "Fruta/Verdura", "Cantidad", "Fecha"};
                    Object[][] d = new Object[combined.size()][];
                    for (int i = 0; i < combined.size(); i++) {
                        String[] r = combined.get(i);
                        // {type, product, qty, recorded_at}
                        d[i] = new Object[]{r[0], r[1], r[2], r[3]};
                    }
                    JTable t = new JTable(d, cols);
                    t.setRowHeight(28);
                    JScrollPane sp = new JScrollPane(t);
                    content.add(sp, BorderLayout.CENTER);
                }

                dlg.add(content, BorderLayout.CENTER);
                JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton close = new JButton("Cerrar"); close.addActionListener(ae -> dlg.dispose()); footer.add(close); dlg.add(footer, BorderLayout.SOUTH);
                dlg.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, "No se pudo generar el reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Reload products into the checkbox panel from the DB (or fallback list)
    public void reloadProducts() {
        // clear existing
        this.checkBoxesPanel.removeAll();
        this.productChecks.clear();
        try {
            com.abarreyapp.dao.ProductDAO pdao = new com.abarreyapp.dao.ProductDAO();
            java.util.List<String[]> rows = pdao.findAll();
            if (rows.isEmpty()) throw new Exception("no rows");
            for (String[] r : rows) {
                String name = r[1];
                JCheckBox checkBox = new JCheckBox(name, true);
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                checkBox.addItemListener(evt -> updateSelectionLabel());
                this.checkBoxesPanel.add(checkBox);
                this.productChecks.add(checkBox);
            }
        } catch (Exception ex) {
            String[] products = {"Brócoli", "Lechuga", "Manzana", "Plátano", "Tomate", "Zanahoria"};
            for (String product : products) {
                JCheckBox checkBox = new JCheckBox(product, true);
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                checkBox.addItemListener(evt -> updateSelectionLabel());
                this.checkBoxesPanel.add(checkBox);
                this.productChecks.add(checkBox);
            }
        }
        // refresh UI
        this.checkBoxesPanel.revalidate();
        this.checkBoxesPanel.repaint();
        updateSelectionLabel();
    }

    private void updateSelectionLabel() {
        int total = this.productChecks.size();
        int sel = 0;
        for (JCheckBox cb : this.productChecks) if (cb.isSelected()) sel++;
        if (this.selectionLabel != null) this.selectionLabel.setText(sel + " de " + total + " productos seleccionados");
    }

    private void updateDateRange(String selectedPeriod, CardLayout cl, JPanel container) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        
        if ("Periodo personalizado".equals(selectedPeriod)) {
            cl.show(container, "fields");
        } else {
            String dateText;
            if ("Última semana".equals(selectedPeriod)) {
                cal.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = cal.getTime();
                dateText = "Rango de fechas: " + sdf.format(startDate) + " - " + sdf.format(endDate);
            } else { // Último mes
                cal.add(Calendar.MONTH, -1);
                Date startDate = cal.getTime();
                dateText = "Rango de fechas: " + sdf.format(startDate) + " - " + sdf.format(endDate);
            }
            dateRangeLabel.setText(dateText);
            cl.show(container, "label");
        }
    }
}
