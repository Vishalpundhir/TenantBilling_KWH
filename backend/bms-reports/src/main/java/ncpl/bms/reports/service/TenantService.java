package ncpl.bms.reports.service;
import org.springframework.stereotype.Service;
import ncpl.bms.reports.model.dto.TenantDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import ncpl.bms.reports.model.dto.TenantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



@Service
public class TenantService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    public TenantDTO addTenant(TenantDTO tenantDTO) {
//        String sql = "INSERT INTO tenants (Name, Address, person_of_contact, mobile_number, unitAddress, email) VALUES (?, ?, ?, ?, ?, ?)";
//        jdbcTemplate.update(sql, tenantDTO.getName(), tenantDTO.getAddress(), tenantDTO.getPersonOfContact(), tenantDTO.getMobileNumber(), tenantDTO.getUnitAddress(),tenantDTO.getEmail() );
//        return tenantDTO;
//    }

    public void deleteTenant(Integer id) {
        String sql = "DELETE FROM tenants WHERE Id = ?";
        jdbcTemplate.update(sql, id);
    }

//    public TenantDTO updateTenant(Integer id, TenantDTO tenantDTO) {
//        String sql = "UPDATE tenants SET Name = ?, Address = ?, person_of_contact = ?, mobile_number = ? , unitAddress = ?, email = ? WHERE Id = ?";
//        jdbcTemplate.update(sql, tenantDTO.getName(), tenantDTO.getAddress(), tenantDTO.getPersonOfContact(), tenantDTO.getMobileNumber(),tenantDTO.getUnitAddress(),tenantDTO.getEmail(), id);
//        tenantDTO.setId(id);
//        return tenantDTO;
//    }

    public TenantDTO addTenant(TenantDTO tenantDTO) {
        String sql = "INSERT INTO tenants (Name, Address, person_of_contact, mobile_number, unitAddress, email, areaOccupied) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                tenantDTO.getName(),
                tenantDTO.getAddress(),
                tenantDTO.getPersonOfContact(),
                tenantDTO.getMobileNumber(),
                tenantDTO.getUnitAddress(),
                tenantDTO.getEmail(),
                tenantDTO.getAreaOccupied()
        );
        return tenantDTO;
    }

    public TenantDTO updateTenant(Integer id, TenantDTO tenantDTO) {
        String sql = "UPDATE tenants SET Name = ?, Address = ?, person_of_contact = ?, mobile_number = ?, unitAddress = ?, email = ?, areaOccupied = ? WHERE Id = ?";
        jdbcTemplate.update(
                sql,
                tenantDTO.getName(),
                tenantDTO.getAddress(),
                tenantDTO.getPersonOfContact(),
                tenantDTO.getMobileNumber(),
                tenantDTO.getUnitAddress(),
                tenantDTO.getEmail(),
                tenantDTO.getAreaOccupied(),
                id
        );
        tenantDTO.setId(id);
        return tenantDTO;
    }

    public List<TenantDTO> getAllTenants() {
        String sql = "SELECT Id, Name, Address, person_of_contact, mobile_number, unitAddress,areaOccupied, email FROM tenants";
        return jdbcTemplate.query(sql, new RowMapper<TenantDTO>() {
            @Override
            public TenantDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new TenantDTO(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Address"),
                        rs.getString("person_of_contact"),
                        rs.getString("mobile_number")  ,
                        rs.getString("unitAddress")  ,
                        rs.getInt("areaOccupied"),
                        rs.getString("email")
                );
            }
        });
    }
}
