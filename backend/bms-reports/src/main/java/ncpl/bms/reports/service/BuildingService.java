package ncpl.bms.reports.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ncpl.bms.reports.model.dto.BuildingDTO;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class BuildingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BuildingDTO getBuildingById(Integer id) {
        String sql = "SELECT * FROM building_details WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToBuildingDTO);
    }

    private BuildingDTO mapRowToBuildingDTO(ResultSet rs, int rowNum) throws SQLException {
        BuildingDTO building = new BuildingDTO();
        building.setId(rs.getInt("id"));
        building.setBuildingName(rs.getString("building_name"));
        building.setAddress(rs.getString("address"));
        building.setTotalArea(rs.getDouble("total_area"));
        building.setEmail(rs.getString("email"));
        building.setPersonOfContact(rs.getString("person_of_contact"));
        building.setPhoneNumber(rs.getString("phone_number"));
        return building;
    }
}
