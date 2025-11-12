package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; // <-- Esto es java.util.Date (para el calendario y los campos)
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
// --- (MODIFICACIÓN: NO IMPORTAMOS java.sql.Date para evitar conflicto) ---

// --- ¡NUEVAS IMPORTACIONES PARA PDF (iText)! ---
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.ColorConstants;
// --- FIN DE IMPORTACIONES PDF ---

import com.abarreyapp.dao.MermaDAO; // Importar el DAO
import com.abarreyapp.dao.LlegadaDAO; // Importar el DAO

import java.io.File; // Para el JFileChooser

public class ReportesPanel extends JPanel {

    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JLabel dateRangeLabel;
    private JPanel dateFieldsPanel;

    private JComboBox<String> periodoComboBox;
    private java.util.List<JCheckBox> productChecks;
    private MermaDAO mermaDAO;
    private LlegadaDAO llegadaDAO;
    private JFrame ownerFrame;


    public ReportesPanel(JFrame owner) {
        this.ownerFrame = owner;
        this.mermaDAO = new MermaDAO();
        this.llegadaDAO = new LlegadaDAO();

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

        String[] periodos = {"Hoy", "Última semana", "Último mes", "Periodo personalizado"};
        periodoComboBox = new JComboBox<>(periodos);
        topControls.add(periodoComboBox);

        topControls.add(Box.createHorizontalStrut(20));

        dateRangeLabel = new JLabel();

        // Usamos el formato dd/MM/yyyy como lo tenías
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
                // --- (MODIFICACIÓN: CORREGIDO EL TYPO 'CardDayout') ---
                updateDateRange(e.getItem().toString(), (CardLayout) dateContainer.getLayout(), dateContainer);
            }
        });


        JPanel middleControls = new JPanel(new BorderLayout());
        middleControls.setBorder(BorderFactory.createTitledBorder("Frutas y Verduras a incluir"));

        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));

        String[] products = {
                "Manzana", "Plátano", "Lechuga", "Tomate", "Zanahoria", "Brócoli",
                "Naranja", "Apio", "Pepino", "Pimiento", "Cebolla", "Papa",
                "Limón", "Aguacate", "Espinaca", "Coliflor"
        };

        productChecks = new ArrayList<>();

        JLabel selectionLabel = new JLabel("");

        for (String product : products) {
            JCheckBox checkBox = new JCheckBox(product, true);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            checkBox.addItemListener(e -> {
                int count = 0;
                for (JCheckBox cb : productChecks) {
                    if (cb.isSelected()) {
                        count++;
                    }
                }
                selectionLabel.setText(count + " de " + productChecks.size() + " productos seleccionados");
            });

            checkBoxesPanel.add(checkBox);
            productChecks.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        middleControls.add(scrollPane, BorderLayout.CENTER);

        JPanel selectionInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        selectionLabel.setText(productChecks.size() + " de " + productChecks.size() + " productos seleccionados");
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
            try {
                // 1. Obtener Fechas
                // --- (MODIFICACIÓN: Usamos el nombre completo java.sql.Date) ---
                java.sql.Date[] dates = getSqlDates();
                if (dates == null) {
                    return;
                }
                java.sql.Date startDate = dates[0];
                java.sql.Date endDate = dates[1];

                // 2. Obtener Productos seleccionados
                List<String> selectedProducts = new ArrayList<>();
                for (JCheckBox cb : productChecks) {
                    if (cb.isSelected()) {
                        selectedProducts.add(cb.getText());
                    }
                }
                if (selectedProducts.isEmpty()) {
                    JOptionPane.showMessageDialog(ownerFrame, "Debe seleccionar al menos un producto.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 3. Preguntar dónde guardar
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Reporte PDF");
                fileChooser.setSelectedFile(new File("Reporte_Abarrey.pdf"));
                int userSelection = fileChooser.showSaveDialog(ownerFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();

                    // 4. Consultar la BD y Generar el PDF
                    generatePdf(fileToSave.getAbsolutePath(), startDate, endDate, selectedProducts);

                    JOptionPane.showMessageDialog(ownerFrame, "¡Reporte PDF generado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ownerFrame, "Error al generar el reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprime el error en la consola de IntelliJ
            }
        });
    }

    /**
     * Genera el archivo PDF con los datos.
     */
    // --- (MODIFICACIÓN: Usamos el nombre completo java.sql.Date) ---
    private void generatePdf(String dest, java.sql.Date startDate, java.sql.Date endDate, List<String> products) throws Exception {

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Mermas y Llegadas")
                .setFontSize(18).setBold());

        document.add(new Paragraph("Periodo del " + startDate.toString() + " al " + endDate.toString())
                .setFontSize(12));

        document.add(new Paragraph("\n"));

        // Consultar DAOs
        List<Object[]> llegadas = llegadaDAO.findForReport(startDate, endDate);
        List<Object[]> mermas = mermaDAO.findForReport(startDate, endDate);

        for (String productName : products) {

            document.add(new Paragraph(productName)
                    .setFontSize(14).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // --- Tabla de Llegadas ---
            document.add(new Paragraph("Llegadas:").setFontSize(10));
            Table llegadasTable = new Table(UnitValue.createPercentArray(new float[]{2, 2}));
            llegadasTable.setWidth(UnitValue.createPercentValue(50));
            llegadasTable.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());
            llegadasTable.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold());

            int llegadasCount = 0;
            for (Object[] row : llegadas) {
                if (row[0].toString().equals(productName)) {
                    llegadasTable.addCell(new Cell().add(new Paragraph(row[2].toString()))); // Fecha
                    llegadasTable.addCell(new Cell().add(new Paragraph(row[1].toString()))); // Cantidad
                    llegadasCount++;
                }
            }
            if (llegadasCount == 0) {
                llegadasTable.addCell(new Cell(1, 2).add(new Paragraph("No hay registros de llegadas.")).setItalic());
            }
            document.add(llegadasTable);


            // --- Tabla de Mermas ---
            document.add(new Paragraph("Mermas:").setFontSize(10));
            Table mermasTable = new Table(UnitValue.createPercentArray(new float[]{2, 2}));
            mermasTable.setWidth(UnitValue.createPercentValue(50));
            mermasTable.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());
            mermasTable.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold());

            int mermasCount = 0;
            for (Object[] row : mermas) {
                if (row[0].toString().equals(productName)) {
                    mermasTable.addCell(new Cell().add(new Paragraph(row[2].toString()))); // Fecha
                    mermasTable.addCell(new Cell().add(new Paragraph(row[1].toString()))); // Cantidad
                    mermasCount++;
                }
            }
            if (mermasCount == 0) {
                mermasTable.addCell(new Cell(1, 2).add(new Paragraph("No hay registros de mermas.")).setItalic());
            }
            document.add(mermasTable);

            document.add(new Paragraph("\n"));
        }

        document.close();
    }


    /**
     * Obtiene las fechas de inicio y fin en formato SQL.
     */
    // --- (MODIFICACIÓN: Usamos el nombre completo java.sql.Date) ---
    private java.sql.Date[] getSqlDates() {
        // Formato para PARSEAR los campos de texto
        // SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy"); // <-- Variable no usada eliminada
        Calendar cal = Calendar.getInstance();

        java.util.Date utilEndDate = cal.getTime(); // Fecha de hoy (java.util.Date)
        java.util.Date utilStartDate = cal.getTime();

        String selectedPeriod = periodoComboBox.getSelectedItem().toString();

        try {
            if ("Periodo personalizado".equals(selectedPeriod)) {
                // Leer fechas de los campos de texto (que están en formato dd/MM/yyyy)
                utilStartDate = (java.util.Date) startDateField.getValue();
                utilEndDate = (java.util.Date) endDateField.getValue();

                if (utilStartDate == null || utilEndDate == null) {
                    JOptionPane.showMessageDialog(ownerFrame, "Debe seleccionar una fecha de inicio y fin.", "Error", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            } else {
                if ("Hoy".equals(selectedPeriod)) {
                    // StartDate y EndDate son hoy
                } else if ("Última semana".equals(selectedPeriod)) {
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    utilStartDate = cal.getTime();
                } else { // "Último mes"
                    cal.add(Calendar.MONTH, -1);
                    utilStartDate = cal.getTime();
                }
            }

            // Convertir java.util.Date a java.sql.Date
            java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(utilEndDate.getTime());

            return new java.sql.Date[]{sqlStartDate, sqlEndDate};

        } catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Formato de fecha inválido. Use dd/MM/yyyy.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    private void updateDateRange(String selectedPeriod, CardLayout cl, JPanel container) {
        // Formato para MOSTRAR en la etiqueta
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        java.util.Date endDate = cal.getTime();

        if ("Periodo personalizado".equals(selectedPeriod)) {
            cl.show(container, "fields");
        } else {
            String dateText;

            if ("Hoy".equals(selectedPeriod)) {
                dateText = "Rango de fechas: " + sdfDisplay.format(endDate);
            }
            else if ("Última semana".equals(selectedPeriod)) {
                cal.add(Calendar.DAY_OF_YEAR, -7);
                java.util.Date startDate = cal.getTime();
                dateText = "Rango de fechas: " + sdfDisplay.format(startDate) + " - " + sdfDisplay.format(endDate);
            } else { // Último mes
                cal.add(Calendar.MONTH, -1);
                java.util.Date startDate = cal.getTime();
                dateText = "Rango de fechas: " + sdfDisplay.format(startDate) + " - " + sdfDisplay.format(endDate);
            }
            dateRangeLabel.setText(dateText);
            cl.show(container, "label");
        }
    }
}