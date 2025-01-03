package ncpl.bms.reports.service;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.List;
import ncpl.bms.reports.model.dto.DailyKwhReportDTO;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Component
@Slf4j
public class DailyKWHGenerationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DailyKWHExcelService dailyKWHExcelService;


    public List<DailyKwhReportDTO> generateDailyKwhReportData(List<String> tableNames, String fromDate, String toDate) {
        // Validate input dates
        if (!fromDate.matches("\\d{4}-\\d{2}-\\d{2}") || !toDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Dates must be in the format YYYY-MM-DD");
        }

        // Add time to the date parameters
        String fromTimestamp = fromDate + " 00:00:00";
        String toTimestamp = toDate + " 23:59:59";

        List<DailyKwhReportDTO> combinedReports = new ArrayList<>();

        for (String tableName : tableNames) {
            // SQL query for the current table
            String sql = String.format(
                    "SELECT DATE(t1.timestamp) AS day, " +
                            "t1.kwh AS start_kwh, " +
                            "t2.kwh AS next_day_kwh " +
                            "FROM %s t1 " +
                            "LEFT JOIN %s t2 ON t2.timestamp = DATE_ADD(DATE(t1.timestamp), INTERVAL 1 DAY) " +
                            "WHERE t1.timestamp BETWEEN ? AND ? " +
                            "AND TIME(t1.timestamp) = '00:00:00' " +
                            "ORDER BY day",
                    tableName, tableName
            );

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, fromTimestamp, toTimestamp);

            for (Map<String, Object> row : rows) {
                String date = row.get("day").toString();
                Double startKwh = row.get("start_kwh") != null ? Double.parseDouble(row.get("start_kwh").toString()) : null;
                Double nextDayKwh = row.get("next_day_kwh") != null ? Double.parseDouble(row.get("next_day_kwh").toString()) : null;

                if (startKwh != null && nextDayKwh != null) {
                    double dailyKwh = nextDayKwh - startKwh;
                    BigDecimal roundedDailyKwh = BigDecimal.valueOf(dailyKwh).setScale(2, RoundingMode.HALF_UP);

                    // Include table name in the DTO
                    combinedReports.add(new DailyKwhReportDTO(date, roundedDailyKwh.doubleValue(), tableName));
                }
            }
        }

        return combinedReports;
    }

    public void generateDailyKwhReportPdf(
            String fileName,
            Map<String, List<DailyKwhReportDTO>> reportData,
            String fromDate,
            String toDate
            , String tenantName) {
        try {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

            // Add a title
            Paragraph title = new Paragraph("Daily kWh Report of " + tenantName)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18);
            document.add(title);

            // Add subheading with date range
            Paragraph subheading = new Paragraph("From: " + fromDate + "  To: " + toDate)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setMarginBottom(20);
            document.add(subheading);

            // Iterate over tables and generate reports for each
            for (Map.Entry<String, List<DailyKwhReportDTO>> entry : reportData.entrySet()) {
                String tableName = entry.getKey();
                List<DailyKwhReportDTO> report = entry.getValue();

                // Add table name as section header
                Paragraph tableNameHeader = new Paragraph("Meter: " + tableName)
                        .setBold()
                        .setFontSize(14)
                        .setMarginTop(20);
                document.add(tableNameHeader);

                // Create a table with two columns
                Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginTop(10);

                // Add headers
                table.addHeaderCell(new Cell().add(new Paragraph("Date")
                                .setBold()
                                .setFontSize(12)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                table.addHeaderCell(new Cell().add(new Paragraph("Daily kWh")
                                .setBold()
                                .setFontSize(12)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));

                // Populate rows
//                for (DailyKwhReportDTO dto : report) {
//                    table.addCell(new Cell().add(new Paragraph(dto.getDate())
//                            .setTextAlignment(TextAlignment.CENTER)));
//
//                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dto.getDailyKwh()))
//                            .setTextAlignment(TextAlignment.RIGHT)));
//                }

                for (DailyKwhReportDTO dto : report) {
                    // Format the date to dd-MM-yyyy
                    String formattedDate = dateFormatter.format(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate()));

                    table.addCell(new Cell().add(new Paragraph(formattedDate)
                            .setTextAlignment(TextAlignment.CENTER)));

                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dto.getDailyKwh()))
                            .setTextAlignment(TextAlignment.RIGHT)));
                }


                // Add table to the document
                document.add(table);
            }

            // Close the document
            document.close();
            System.out.println("PDF generated successfully: " + fileName);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }



//    public void generateDailyKwhReportPdf(
//            String fileName,
//            Map<String, List<DailyKwhReportDTO>> reportData,
//            String fromDate,
//            String toDate,
//            String tenantName) {
//        try {
//            // Initialize PDF writer
//            PdfWriter writer = new PdfWriter(fileName);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//
//            // Add a title
//            Paragraph title = new Paragraph("Daily kWh Report of " + tenantName)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setBold()
//                    .setFontSize(18);
//            document.add(title);
//
//            // Add subheading with date range
//            Paragraph subheading = new Paragraph("From: " + fromDate + "  To: " + toDate)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFontSize(12)
//                    .setMarginBottom(20);
//            document.add(subheading);
//
//            // Iterate over tables and generate reports for each
//            for (Map.Entry<String, List<DailyKwhReportDTO>> entry : reportData.entrySet()) {
//                String tableName = entry.getKey();
//                List<DailyKwhReportDTO> report = entry.getValue();
//
//                // Add table name as section header
//                Paragraph tableNameHeader = new Paragraph("Meter: " + tableName)
//                        .setBold()
//                        .setFontSize(14)
//                        .setMarginTop(20);
//                document.add(tableNameHeader);
//
//                // Create a table with two columns
//                Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
//                        .setWidth(UnitValue.createPercentValue(100))
//                        .setMarginTop(10);
//
//                // Add headers
//                table.addHeaderCell(new Cell().add(new Paragraph("Date")
//                        .setBold()
//                        .setTextAlignment(TextAlignment.CENTER)));
//                table.addHeaderCell(new Cell().add(new Paragraph("Daily kWh")
//                        .setBold()
//                        .setTextAlignment(TextAlignment.CENTER)));
//
//                // Populate rows
//                double maxKwh = 0;
//                for (DailyKwhReportDTO dto : report) {
//                    table.addCell(new Cell().add(new Paragraph(dto.getDate())
//                            .setTextAlignment(TextAlignment.CENTER)));
//                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f", dto.getDailyKwh()))
//                            .setTextAlignment(TextAlignment.RIGHT)));
//
//                    // Track the maximum kWh value for scaling the chart
//                    maxKwh = Math.max(maxKwh, dto.getDailyKwh());
//                }
//
//                // Add table to the document
//                document.add(table);
//
//                // Draw bar chart for this energy meter
//                if (!report.isEmpty()) {
//                    PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
//                    float chartWidth = 500;
//                    float chartHeight = 200;
//                    float startX = 50;
//                    float startY = 600;
//
//                    // Draw chart axes
//                    canvas.moveTo(startX, startY)
//                            .lineTo(startX, startY + chartHeight)
//                            .lineTo(startX + chartWidth, startY)
//                            .stroke();
//
//                    float barWidth = chartWidth / report.size();
//                    float scaleFactor = (float) chartHeight / (float) maxKwh;
//
//                    // Draw bars
//                    float currentX = startX;
//                    for (DailyKwhReportDTO dto : report) {
//                        float barHeight = dto.getDailyKwh().floatValue() * scaleFactor;
//                        canvas.setFillColor(ColorConstants.BLUE)
//                                .rectangle(currentX, startY, barWidth - 5, barHeight)
//                                .fill();
//                        currentX += barWidth;
//                    }
//
//                    // Add labels
//                    currentX = startX;
//                    for (DailyKwhReportDTO dto : report) {
//                        canvas.beginText()
//                                .setFontAndSize(PdfFontFactory.createFont(), 10)
//                                .moveText(currentX + (barWidth / 4), startY - 10)
//                                .showText(dto.getDate())
//                                .endText();
//                        currentX += barWidth;
//                    }
//                }
//            }
//
//            // Close the document
//            document.close();
//            System.out.println("PDF generated successfully: " + fileName);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while generating PDF", e);
//        }
//    }


    public void ExportDailyKwhReportPdf(List<String> tableNames, String fromDate, String toDate, String tenantName) {
        // Map to store reports by table
        Map<String, List<DailyKwhReportDTO>> reportData = new HashMap<>();

        // Generate reports for each table
        for (String tableName : tableNames) {
            List<DailyKwhReportDTO> report = generateDailyKwhReportData(Collections.singletonList(tableName), fromDate, toDate);
            reportData.put(tableName, report);
        }

        // Specify output PDF file path
        String outputFileName = "daily_kwh_report.pdf";

        // Generate the PDF with reports for all tables
        generateDailyKwhReportPdf(outputFileName, reportData, fromDate, toDate, tenantName);
    }

    public void ExportDailyKwhReportExcel(List<String> tableNames, String fromDate, String toDate , String tenantName) {
        // Map to store reports by table
        Map<String, List<DailyKwhReportDTO>> reportData = new HashMap<>();

        // Generate reports for each table
        for (String tableName : tableNames) {
            List<DailyKwhReportDTO> report = generateDailyKwhReportData(Collections.singletonList(tableName), fromDate, toDate);
            reportData.put(tableName, report);
        }

        // Specify output Excel file path
        String outputFileName = "daily_kwh_report.xlsx";

        // Generate the Excel file with all table data
        dailyKWHExcelService.generateDailyKwhReportExcel(outputFileName, reportData, fromDate, toDate,  tenantName);
    }

}