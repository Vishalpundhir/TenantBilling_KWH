package ncpl.bms.reports.controller;
import ncpl.bms.reports.model.dto.TenantDTO;
import ncpl.bms.reports.model.dto.TenantEnergyMeterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ncpl.bms.reports.service.TenantAndEnergyMeterService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1")
@CrossOrigin(origins = "http://localhost:4200")
public class TenantAndEnergyMeterController {

    @Autowired
    private TenantAndEnergyMeterService tenantAndEnergyMeterService;

    @GetMapping("/tenant-detail-by-id")
    public TenantDTO getTenantDetails(@RequestParam int tenantId) {
        return tenantAndEnergyMeterService.getTenantDetailsById(tenantId);
    }

//    @GetMapping("/all-tenants")
//    public List<TenantDTO> getAllTenants() {
//        return tenantAndEnergyMeterService.getAllTenants();
//    }

    @GetMapping("/all-active-energy-meters")
    public List<TenantEnergyMeterDTO> getAllTableNames() {
        return tenantAndEnergyMeterService.getAllActiveEnergyMeters();
    }

    @PostMapping("/add-energy-meter")
    public ResponseEntity<TenantEnergyMeterDTO> addEnergyMeter(@RequestBody TenantEnergyMeterDTO energyMeter) {
        try {
            TenantEnergyMeterDTO savedEnergyMeter = tenantAndEnergyMeterService.addEnergyMeter(energyMeter);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEnergyMeter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update-energy-meter")
    public ResponseEntity<String> updateEnergyMeter(@RequestBody TenantEnergyMeterDTO energyMeter) {
        try {
            tenantAndEnergyMeterService.updateEnergyMeter(energyMeter);
            return ResponseEntity.ok("Energy meter updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the energy meter.");
        }
    }


    @DeleteMapping("/delete-energy-meter/{id}")
    public ResponseEntity<Map<String, String>> deleteEnergyMeter(@PathVariable int id) {
        Map<String, String> response = new HashMap<>();
        try {
            tenantAndEnergyMeterService.deleteEnergyMeterById(id);
            response.put("message", "Energy meter deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
