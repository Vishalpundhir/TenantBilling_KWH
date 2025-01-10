package ncpl.bms.reports.service;
import org.springframework.stereotype.Service;
import ncpl.bms.reports.model.dto.CommonAreaDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class CommonAreaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CommonAreaDTO> getCommonArea() {
        String sql = "SELECT id, name,  address , personOfContact, mobileNumber, unitAddress, totalArea, email FROM CommonArea";
        return jdbcTemplate.query(sql, new RowMapper<CommonAreaDTO>() {
            @Override
            public CommonAreaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CommonAreaDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("personOfContact"),
                        rs.getString("mobileNumber")  ,
                        rs.getString("unitAddress")  ,
                        rs.getInt("totalArea"),
                        rs.getString("email")
                );
            }
        });
    }

    public void updateCommonArea(CommonAreaDTO commonAreaDTO) {
        String sql = "UPDATE CommonArea SET name = ?, address = ?, personOfContact = ?, mobileNumber = ?, email = ?, unitAddress = ?, totalArea = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                commonAreaDTO.getName(),
                commonAreaDTO.getAddress(),
                commonAreaDTO.getPersonOfContact(),
                commonAreaDTO.getMobileNumber(),
                commonAreaDTO.getEmail(),
                commonAreaDTO.getUnitAddress(),
                commonAreaDTO.getTotalArea(),
                commonAreaDTO.getId()
        );
    }

}
