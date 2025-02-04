package ncpl.bms.reports.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.time.Month;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class BillingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TenantAndEnergyMeterService tenantAndEnergyMeterService;


    public byte[] generateBillPdf(Long tenantId, String month, String year) {
        // Convert the numeric month to its full name
        String monthName;
        try {
            int monthNumber = Integer.parseInt(month);
            monthName = Month.of(monthNumber).name(); // Returns the month in uppercase
            monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase(); // Capitalize first letter
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid month format: " + month);
        }
        float cellFontSize = 9;

        // Fetch energy usage
        Map<String, Double> energyUsage = tenantAndEnergyMeterService.getEnergyMetersForTenant(tenantId.intValue(), month, year);
        double ebUsage = BigDecimal.valueOf(energyUsage.get("totalEbKwh")).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double dgUsage = BigDecimal.valueOf(energyUsage.get("totalDgKwh")).setScale(2, RoundingMode.HALF_UP).doubleValue();


        Map<String, Double> energyUsageCommonArea = tenantAndEnergyMeterService.getEnergyMetersForTenant(1, month, year);
        double ebUsageCommonArea = BigDecimal.valueOf(energyUsageCommonArea.get("totalEbKwh")).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double dgUsageCommonArea = BigDecimal.valueOf(energyUsageCommonArea.get("totalDgKwh")).setScale(2, RoundingMode.HALF_UP).doubleValue();

        // Fetch static information from the database
        String staticInfoSql = "SELECT bill_title, client_name, email, phone_number, eb_tarrif, dg_tarrif,  total_building_area  FROM static_information LIMIT 1";
        Map<String, Object> staticInfo = jdbcTemplate.queryForMap(staticInfoSql);

        String billTitle = (String) staticInfo.get("bill_title");
        String clientName = (String) staticInfo.get("client_name");
        String email = (String) staticInfo.get("email");
        String phoneNumber = (String) staticInfo.get("phone_number");
        double totalBuildingArea = getDoubleValue(staticInfo.get("total_building_area"));
        double ebTariff = ((BigDecimal) staticInfo.get("eb_tarrif")).doubleValue();
        double dgTariff = ((BigDecimal) staticInfo.get("dg_tarrif")).doubleValue();


        // Calculate total amounts
        double ebAmount = ebUsage * ebTariff;
        double dgAmount = dgUsage * dgTariff;
        double totalAmount = ebAmount + dgAmount;
        double ebAmountCommonArea = ebUsageCommonArea * ebTariff;
        double dgAmountCommonArea = dgUsageCommonArea * dgTariff;
        double totalAmountCommonArea = ebAmountCommonArea + dgAmountCommonArea;

        // Fetch tenant details based on tenantId
        String tenantInfoSql = "SELECT name, person_of_contact, email, mobile_number, unit_address,  area_occupied FROM tenant WHERE id = ?";
        Map<String, Object> tenantInfo = jdbcTemplate.queryForMap(tenantInfoSql, tenantId);
        double tenantArea = getDoubleValue(tenantInfo.get("area_occupied"));
        double tenantShareCommonArea = (totalAmountCommonArea / totalBuildingArea) * tenantArea;
        double netPayableAmount = totalAmount + tenantShareCommonArea;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add centered title with full month name and year
            Paragraph title = new Paragraph(billTitle + " - " + monthName + " " + year)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
//                    .setBold()
                    .setMarginBottom(20);
            document.add(title);

            // Add Client Details section
            document.add(new Paragraph("Client Details:")
                    .setFontSize(10)
//                    .setBold()
                    .setMarginBottom(10));
            Table clientTable = new Table(UnitValue.createPercentArray(new float[]{2, 4}))
                    .setWidth(UnitValue.createPercentValue(100));
            clientTable.addCell(new Cell().add(new Paragraph("Name").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            clientTable.addCell(new Cell().add(new Paragraph(clientName)).setFontSize(cellFontSize));
            clientTable.addCell(new Cell().add(new Paragraph("Email").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            clientTable.addCell(new Cell().add(new Paragraph(email)).setFontSize(cellFontSize));
            clientTable.addCell(new Cell().add(new Paragraph("Phone Number").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            clientTable.addCell(new Cell().add(new Paragraph(phoneNumber)).setFontSize(cellFontSize));
            document.add(clientTable);

            // Add Tenant Details section
            document.add(new Paragraph("Tenant Details:")
                    .setFontSize(10)
//                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10));
            Table tenantTable = new Table(UnitValue.createPercentArray(new float[]{2, 4}))
                    .setWidth(UnitValue.createPercentValue(100));
            tenantTable.addCell(new Cell().add(new Paragraph("Name").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tenantTable.addCell(new Cell().add(new Paragraph((String) tenantInfo.get("name")).setFontSize(cellFontSize)));
            tenantTable.addCell(new Cell().add(new Paragraph("Person of Contact").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tenantTable.addCell(new Cell().add(new Paragraph((String) tenantInfo.get("person_of_contact")).setFontSize(cellFontSize)));
            tenantTable.addCell(new Cell().add(new Paragraph("Email").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tenantTable.addCell(new Cell().add(new Paragraph((String) tenantInfo.get("email")).setFontSize(cellFontSize)));
            tenantTable.addCell(new Cell().add(new Paragraph("Phone Number").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tenantTable.addCell(new Cell().add(new Paragraph((String) tenantInfo.get("mobile_number")).setFontSize(cellFontSize)));
            tenantTable.addCell(new Cell().add(new Paragraph("Unit Address").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tenantTable.addCell(new Cell().add(new Paragraph((String) tenantInfo.get("unit_address")).setFontSize(cellFontSize)));
            document.add(tenantTable);

            // Add Bill Details section
            document.add(new Paragraph("Bill Details:")
                    .setFontSize(10)
//                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Define the table structure
            Table billTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Add headers for the main columns
            billTable.addCell(new Cell(2, 1).add(new Paragraph("Description").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell(1, 2).add(new Paragraph("EB").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell(1, 2).add(new Paragraph("DG").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell(2, 1).add(new Paragraph("Amount").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            // Add sub-headers for EB and DG
            billTable.addCell(new Cell().add(new Paragraph("Usage (kWh)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell().add(new Paragraph("Tariff (Rs)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell().add(new Paragraph("Usage (kWh)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell().add(new Paragraph("Tariff (Rs)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            // Add data rows for EB and DG
            billTable.addCell(new Cell().add(new Paragraph("Electricity Consumption ").setFontSize(cellFontSize)));
            billTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", ebUsage)).setFontSize(cellFontSize)));
            billTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", ebTariff)).setFontSize(cellFontSize)));
            billTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", dgUsage)).setFontSize(cellFontSize)));
            billTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", dgTariff)).setFontSize(cellFontSize)));
            billTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", totalAmount)).setFontSize(cellFontSize)));
            // Add total row
//            billTable.addCell(new Cell(1, 5).add(new Paragraph("Total Amount"))
//                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                    .setTextAlignment(TextAlignment.RIGHT));
//            billTable.addCell(new Cell().add(new Paragraph(String.format("₹ %.2f", totalAmount))));

            document.add(billTable);
            // common area details-----------------------------

            document.add(new Paragraph("Common Area Bill Details:")
                    .setFontSize(10)
//                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Define the table structure
            Table commonAreabillTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Add headers for the main columns
            commonAreabillTable.addCell(new Cell(2, 1).add(new Paragraph("Description").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell(1, 2).add(new Paragraph("EB").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell(1, 2).add(new Paragraph("DG").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell(2, 1).add(new Paragraph("Amount").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            // Add sub-headers for EB and DG
            commonAreabillTable.addCell(new Cell().add(new Paragraph("Usage (kWh)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell().add(new Paragraph("Tariff (Rs)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell().add(new Paragraph("Usage (kWh)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            commonAreabillTable.addCell(new Cell().add(new Paragraph("Tariff (Rs)").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            // Add data rows for EB and DG
            commonAreabillTable.addCell(new Cell().add(new Paragraph("Electricity Consumption of common Area").setFontSize(cellFontSize)));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", ebUsageCommonArea)).setFontSize(cellFontSize)));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", ebTariff)).setFontSize(cellFontSize)));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", dgUsageCommonArea)).setFontSize(cellFontSize)));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", dgTariff)).setFontSize(cellFontSize)));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f ", totalAmountCommonArea)).setFontSize(cellFontSize)));
            // Add total row
//            commonAreabillTable.addCell(new Cell(1, 5).add(new Paragraph("Total Amount"))
//                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                    .setTextAlignment(TextAlignment.RIGHT));
//            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("₹ %.2f", totalAmountCommonArea))));
            commonAreabillTable.addCell(new Cell(1, 5).add(new Paragraph("Net Payble Ammount Of CommonArea").setFontSize(cellFontSize))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));
            commonAreabillTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", tenantShareCommonArea)).setFontSize(cellFontSize)));

            document.add(commonAreabillTable);

            // Add Final Net Payable Amount

            document.add(new Paragraph("Net Payable Amount:" + "₹").setFontSize(10).setMarginTop(20).setMarginBottom(10));
            Table netPayableTable = new Table(UnitValue.createPercentArray(new float[]{4, 2}))
                    .setWidth(UnitValue.createPercentValue(100));
            netPayableTable.addCell(new Cell().add(new Paragraph("Tenant Unit Address Consumption (INR)").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            netPayableTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", totalAmount)).setFontSize(cellFontSize).setTextAlignment(TextAlignment.RIGHT)));
            netPayableTable.addCell(new Cell().add(new Paragraph("Tenant Share of Common Area (INR)").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            netPayableTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", tenantShareCommonArea)).setFontSize(cellFontSize).setTextAlignment(TextAlignment.RIGHT)));
            netPayableTable.addCell(new Cell().add(new Paragraph("Net Payable Amount (INR)").setFontSize(cellFontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            netPayableTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", netPayableAmount)).setFontSize(cellFontSize).setTextAlignment(TextAlignment.RIGHT)));
            document.add(netPayableTable);

            //-------------------------------------------------

            // Close document and return the byte array
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }


    private double getDoubleValue(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else {
            throw new RuntimeException("Unexpected type: " + value.getClass().getName());
        }
    }

}
