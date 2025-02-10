package ncpl.bms.reports.controller;
import lombok.extern.slf4j.Slf4j;
import ncpl.bms.reports.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import ncpl.bms.reports.model.dto.BuildingDTO;

@RestController
@RequestMapping("v1")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Slf4j
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/get-building-details/{id}")
    public BuildingDTO getBuildingById(@PathVariable Integer id) {
        return buildingService.getBuildingById(id);
    }
}
