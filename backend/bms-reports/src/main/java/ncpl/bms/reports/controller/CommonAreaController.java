package ncpl.bms.reports.controller;
import lombok.extern.slf4j.Slf4j;
import ncpl.bms.reports.model.dto.TenantDTO;
import ncpl.bms.reports.service.CommonAreaService;
import ncpl.bms.reports.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ncpl.bms.reports.model.dto.CommonAreaDTO;
import ncpl.bms.reports.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("v1")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class CommonAreaController {

    @Autowired
    private CommonAreaService commonAreaService;

    @GetMapping("/get-common-area")
    public List<CommonAreaDTO> getCommonArea() {
        return commonAreaService.getCommonArea();
    }

    @PostMapping("/update-common-area")
    public ResponseEntity<Map<String, String>> updateCommonArea(@RequestBody CommonAreaDTO commonAreaDTO) {
        commonAreaService.updateCommonArea(commonAreaDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Common Area updated successfully");
        return ResponseEntity.ok(response);
    }


}
