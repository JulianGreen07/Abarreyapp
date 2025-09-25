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

        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));
        String[] products = {"Brócoli", "Lechuga", "Manzana", "Plátano", "Tomate", "Zanahoria"};
        java.util.List<JCheckBox> productChecks = new java.util.ArrayList<>();
        for (String product : products) {
            JCheckBox checkBox = new JCheckBox(product, true);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkBoxesPanel.add(checkBox);
            productChecks.add(checkBox);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkBoxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        
        middleControls.add(scrollPane, BorderLayout.CENTER);
        
        JPanel selectionInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel selectionLabel = new JLabel(productChecks.size() + " de " + productChecks.size() + " productos seleccionados");
        selectionInfoPanel.add(selectionLabel);
        JButton deseleccionarBtn = new JButton("Deseleccionar todas");
        selectionInfoPanel.add(deseleccionarBtn, BorderLayout.EAST);
         middleControls.add(selectionInfoPanel, BorderLayout.SOUTH);

        deseleccionarBtn.addActionListener(e -> {
            for (JCheckBox cb : productChecks) cb.setSelected(false);
            selectionLabel.setText("0 de " + productChecks.size() + " productos seleccionados");
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
            // Lógica para generar el reporte
            JOptionPane.showMessageDialog(owner, "Generando reporte...", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
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
