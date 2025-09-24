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
        for (String product : products) {
            JCheckBox checkBox = new JCheckBox(product, true);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkBoxesPanel.add(checkBox);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkBoxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        
        middleControls.add(scrollPane, BorderLayout.CENTER);
        
        JPanel selectionInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionInfoPanel.add(new JLabel("6 de 6 productos seleccionados"));
        JButton deseleccionarBtn = new JButton("Deseleccionar todas");
        selectionInfoPanel.add(deseleccionarBtn, BorderLayout.EAST);
         middleControls.add(selectionInfoPanel, BorderLayout.SOUTH);


    // El botón de "Generar Reporte" ha sido removido por petición del usuario.

        JPanel mainControlsPanel = new JPanel();
        mainControlsPanel.setLayout(new BoxLayout(mainControlsPanel, BoxLayout.Y_AXIS));
        mainControlsPanel.add(topControls);
        mainControlsPanel.add(middleControls);
        
    controlsContainer.add(mainControlsPanel, BorderLayout.CENTER);

        add(controlsContainer, BorderLayout.CENTER);

        // Generación de reportes deshabilitada/retirada.
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
