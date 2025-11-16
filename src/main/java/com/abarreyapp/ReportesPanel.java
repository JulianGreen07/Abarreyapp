package com.abarreyapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; // <-- java.util.Date (para el calendario y los campos)
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
// (Importación de java.sql.Date eliminada para evitar conflictos)

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

// --- IMPORTACIONES RESTAURADAS PARA RESPALDO ---
import java.io.File; // Para el JFileChooser
import javax.swing.JFileChooser; // Para el JFileChooser
import java.io.IOException; // Para manejar errores del ProcessBuilder
// --- (FIN DE IMPORTACIÓN) ---

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

        JButton btnRespaldar = new JButton("Crear Respaldo DB");
        btnRespaldar.setBackground(new Color(23, 162, 184)); // Color azul
        btnRespaldar.setForeground(Color.WHITE);
        bottomPanel.add(btnRespaldar);

        controlsContainer.add(mainControlsPanel, BorderLayout.CENTER);
        controlsContainer.add(bottomPanel, BorderLayout.SOUTH);

        add(controlsContainer, BorderLayout.CENTER);


        generarReporteBtn.addActionListener(e -> {
            try {
                java.sql.Date[] dates = getSqlDates();
                if (dates == null) return;
                java.sql.Date startDate = dates[0];
                java.sql.Date endDate = dates[1];

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

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Reporte PDF");
                fileChooser.setSelectedFile(new File("Reporte_Abarrey.pdf"));
                int userSelection = fileChooser.showSaveDialog(ownerFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    generatePdf(fileToSave.getAbsolutePath(), startDate, endDate, selectedProducts);
                    JOptionPane.showMessageDialog(ownerFrame, "¡Reporte PDF generado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ownerFrame, "Error al generar el reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // --- (LÓGICA DE RESPALDO CORREGIDA) ---
        btnRespaldar.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                    ownerFrame,
                    "¿Deseas crear un respaldo de la base de datos ahora?\nEsto puede tardar unos segundos.",
                    "Confirmar Respaldo",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Respaldo SQL Como...");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
                fileChooser.setSelectedFile(new File("abarrey_db_backup_" + timeStamp + ".sql"));

                int userSelection = fileChooser.showSaveDialog(ownerFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File archivoDeRespaldo = fileChooser.getSelectedFile();

                    try {
                        String dbUser = "abarrey_user";
                        String dbPass = "ChangeMe123!";
                        String dbName = "abarrey_db";

                        // --- (MODIFICACIÓN: RUTA COMPLETA A mysqldump.exe) ---
                        // Asegúrate de que esta sea la ruta donde instalaste MySQL
                        String mysqlDumpPath = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";

                        ProcessBuilder pb = new ProcessBuilder(
                                mysqlDumpPath, // Usamos la ruta completa
                                "-u" + dbUser,
                                "-p" + dbPass,
                                dbName
                        );
                        // --- (FIN DE MODIFICACIÓN) ---

                        pb.redirectOutput(archivoDeRespaldo);

                        Process process = pb.start();
                        int exitCode = process.waitFor();

                        if (exitCode == 0) {
                            JOptionPane.showMessageDialog(ownerFrame,
                                    "¡Respaldo creado exitosamente!\nGuardado en: " + archivoDeRespaldo.getAbsolutePath());
                        } else {
                            String error = new String(process.getErrorStream().readAllBytes());
                            JOptionPane.showMessageDialog(ownerFrame,
                                    "Error al crear el respaldo. Código: " + exitCode + "\nError: " + error,
                                    "Error de Respaldo", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException ioEx) {
                        JOptionPane.showMessageDialog(ownerFrame,
                                "Error: No se pudo ejecutar 'mysqldump'.\n\nAsegúrate de que la ruta en el código es correcta.\n" + ioEx.getMessage(),
                                "Error de Ejecución", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException intEx) {
                        JOptionPane.showMessageDialog(ownerFrame,
                                "El proceso de respaldo fue interrumpido.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * Genera el archivo PDF con los datos.
     */
    private void generatePdf(String dest, java.sql.Date startDate, java.sql.Date endDate, List<String> products) throws Exception {

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Mermas y Llegadas")
                .setFontSize(18).setBold());

        document.add(new Paragraph("Periodo del " + startDate.toString() + " al " + endDate.toString())
                .setFontSize(12));

        document.add(new Paragraph("\n"));

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
    private java.sql.Date[] getSqlDates() {
        Calendar cal = Calendar.getInstance();

        java.util.Date utilEndDate = cal.getTime();
        java.util.Date utilStartDate = cal.getTime();

        String selectedPeriod = periodoComboBox.getSelectedItem().toString();

        try {
            if ("Periodo personalizado".equals(selectedPeriod)) {
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