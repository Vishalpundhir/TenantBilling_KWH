package ncpl.bms.reports.service;
import ncpl.bms.reports.model.dto.TenantDTO;
import ncpl.bms.reports.model.dto.TenantEnergyMeterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TenantAndEnergyMeterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public TenantDTO getTenantDetailsById(int tenantId) {
        try {
            // Use backticks to handle case sensitivity in MySQL
            String sql = "SELECT `Name`, `Address` FROM `tenants` WHERE `Id` = ?";

            // Execute query and map results to TenantDTO
            return jdbcTemplate.queryForObject(sql, new Object[]{tenantId}, (rs, rowNum) -> {
                TenantDTO tenant = new TenantDTO();
                tenant.setName(rs.getString("Name"));
                tenant.setAddress(rs.getString("Address"));
                return tenant;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Tenant not found with ID: " + tenantId);
        }
    }

//    public List<TenantDTO> getAllTenants() {
//        String sql = "SELECT `Id`, `Name`, `Address` FROM `tenants`";
//
//        // Execute query and map results to a list of TenantDTO objects
//        return jdbcTemplate.query(sql, (rs, rowNum) -> {
//            TenantDTO tenant = new TenantDTO();
//            tenant.setId(rs.getInt("Id")); // Assuming TenantDTO has an `id` field now
//            tenant.setName(rs.getString("Name"));
//            tenant.setAddress(rs.getString("Address"));
//            return tenant;
//        });
//    }

    public List<TenantEnergyMeterDTO> getAllActiveEnergyMeters() {
        // Query to fetch Id, Name, and Tenant_Id from tenants_energy_meters
        String sql = "SELECT Id, Name, Tenant_Id FROM tenants_energy_meters";

        // Execute query and map results to TableDTO
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TenantEnergyMeterDTO dto = new TenantEnergyMeterDTO();
            dto.setId(rs.getInt("Id"));
            dto.setName(rs.getString("Name"));
            dto.setTenantId(rs.getObject("Tenant_Id", Integer.class)); // Handle nullable Tenant_Id
            return dto;
        });
    }

    public TenantEnergyMeterDTO addEnergyMeter(TenantEnergyMeterDTO energyMeter) {
        String sql = "INSERT INTO tenants_energy_meters (Name, Tenant_Id) VALUES (?, ?)";
        jdbcTemplate.update(sql, energyMeter.getName(), energyMeter.getTenantId());

        // Fetch the last inserted ID (optional, for returning the saved object with ID)
        String fetchSql = "SELECT Id, Name, Tenant_Id FROM tenants_energy_meters WHERE Id = LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(fetchSql, (rs, rowNum) -> {
            TenantEnergyMeterDTO dto = new TenantEnergyMeterDTO();
            dto.setId(rs.getInt("Id"));
            dto.setName(rs.getString("Name"));
            dto.setTenantId(rs.getObject("Tenant_Id", Integer.class));
            return dto;
        });
    }


    public void updateEnergyMeter(TenantEnergyMeterDTO energyMeter) {
        String sql = "UPDATE tenants_energy_meters SET Name = ?, Tenant_Id = ? WHERE Id = ?";
        int rowsAffected = jdbcTemplate.update(sql, energyMeter.getName(), energyMeter.getTenantId(), energyMeter.getId());

        if (rowsAffected == 0) {
            throw new RuntimeException("Energy meter not found with ID: " + energyMeter.getId());
        }
    }


    public void deleteEnergyMeterById(int id) {
        String sql = "DELETE FROM tenants_energy_meters WHERE Id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new RuntimeException("Energy meter not found with ID: " + id);
        }
    }

}
