package ncpl.bms.reports.service;
import ncpl.bms.reports.model.dto.TenantDTO;
import ncpl.bms.reports.model.dto.MonthlyKwhReportDTO;
import ncpl.bms.reports.model.dto.TenantEnergyMeterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TenantAndEnergyMeterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MonthlyKWHGenerationService monthlyKWHGenerationService;

    public TenantDTO getTenantDetailsById(int tenantId) {
        try {
            // Use backticks to handle case sensitivity in MySQL
            String sql = "SELECT `name`, `address` FROM `tenant` WHERE `id` = ?";

            // Execute query and map results to TenantDTO
            return jdbcTemplate.queryForObject(sql, new Object[]{tenantId}, (rs, rowNum) -> {
                TenantDTO tenant = new TenantDTO();
                tenant.setName(rs.getString("name"));
                tenant.setAddress(rs.getString("address"));
                return tenant;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Tenant not found with ID: " + tenantId);
        }
    }

    public List<TenantEnergyMeterDTO> getAllActiveEnergyMeters() {
        // Query to fetch Id, Name, and Tenant_Id from tenants_energy_meters
        String sql = "SELECT id, name, tenant_id FROM  tenant_to_energy_meter_relation";

        // Execute query and map results to TableDTO
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TenantEnergyMeterDTO dto = new TenantEnergyMeterDTO();
            dto.setId(rs.getInt("id"));
            dto.setName(rs.getString("name"));
            dto.setTenantId(rs.getObject("tenant_id", Integer.class)); // Handle nullable Tenant_Id
            return dto;
        });
    }

    public TenantEnergyMeterDTO addEnergyMeter(TenantEnergyMeterDTO energyMeter) {
        String sql = "INSERT INTO  tenant_to_energy_meter_relation (name, tenant_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, energyMeter.getName(), energyMeter.getTenantId());

        // Fetch the last inserted ID (optional, for returning the saved object with ID)
        String fetchSql = "SELECT id, name, tenant_id FROM tenant_to_energy_meter_relation WHERE id = LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(fetchSql, (rs, rowNum) -> {
            TenantEnergyMeterDTO dto = new TenantEnergyMeterDTO();
            dto.setId(rs.getInt("id"));
            dto.setName(rs.getString("name"));
            dto.setTenantId(rs.getObject("tenant_id", Integer.class));
            return dto;
        });
    }

    public void updateEnergyMeter(TenantEnergyMeterDTO energyMeter) {
        String sql = "UPDATE  tenant_to_energy_meter_relation SET name = ?, tenant_id = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, energyMeter.getName(), energyMeter.getTenantId(), energyMeter.getId());

        if (rowsAffected == 0) {
            throw new RuntimeException("Energy meter not found with ID: " + energyMeter.getId());
        }
    }

    public void deleteEnergyMeterById(int id) {
        String sql = "DELETE FROM  tenant_to_energy_meter_relation WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new RuntimeException("Energy meter not found with ID: " + id);
        }
    }

    public List<String> getAvailableEnergyMeterNames() {
        // SQL query to get energy meter names not assigned to tenants
        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE (table_name LIKE '%_dg_kwh' OR table_name LIKE '%_eb_kwh')
                """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    public Map<String, Double> getEnergyMetersForTenant(int tenantId, String month, String year) {
        // Query to fetch energy meters assigned to the tenant
        String sql = "SELECT name FROM  tenant_to_energy_meter_relation WHERE tenant_id = ?";
        List<String> energyMeters = jdbcTemplate.query(sql, new Object[]{tenantId}, (rs, rowNum) -> rs.getString("name"));

        // Separate energy meters into EB and DG categories
        List<String> ebMeters = energyMeters.stream()
                .filter(name -> name.endsWith("_eb_kwh"))
                .toList();

        List<String> dgMeters = energyMeters.stream()
                .filter(name -> name.endsWith("_dg_kwh"))
                .toList();

        // Convert month and year to YYYY-MM format
        String monthYear = year + "-" + String.format("%02d", Integer.parseInt(month));

        // Fetch data for EB meters
        double totalEbKwh = 0;
        if (!ebMeters.isEmpty()) {
            List<MonthlyKwhReportDTO> ebData = monthlyKWHGenerationService.generateMonthlyKwhReport(ebMeters, monthYear, monthYear);
            totalEbKwh = ebData.stream().mapToDouble(MonthlyKwhReportDTO::getMonthlyKwh).sum();
        }

        // Fetch data for DG meters
        double totalDgKwh = 0;
        if (!dgMeters.isEmpty()) {
            List<MonthlyKwhReportDTO> dgData = monthlyKWHGenerationService.generateMonthlyKwhReport(dgMeters, monthYear, monthYear);
            totalDgKwh = dgData.stream().mapToDouble(MonthlyKwhReportDTO::getMonthlyKwh).sum();
        }

        // Print results
//        System.out.println("EB Energy Meters assigned to Tenant ID " + tenantId + ": " + ebMeters);
//        System.out.println("Total EB Consumption (kWh): " + totalEbKwh);
//        System.out.println("DG Energy Meters assigned to Tenant ID " + tenantId + ": " + dgMeters);
//        System.out.println("Total DG Consumption (kWh): " + totalDgKwh);

        // Return the results
        Map<String, Double> energyUsage = new HashMap<>();
        energyUsage.put("totalEbKwh", totalEbKwh);
        energyUsage.put("totalDgKwh", totalDgKwh);

        return energyUsage;
    }

}
