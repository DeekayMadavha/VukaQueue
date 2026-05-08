package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.Clinic;
import za.ac.cput.service.ClinicService;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    // POST /api/clinics
    @PostMapping
    public ResponseEntity<Clinic> create(@RequestBody Clinic clinic) {
        Clinic created = clinicService.create(clinic);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/clinics/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getById(@PathVariable String id) {
        return clinicService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/clinics?city=Cape Town&province=Western Cape
    // Supports "find a clinic near me" patient user story
    @GetMapping
    public ResponseEntity<List<Clinic>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province) {

        if (city != null && province != null) {
            return ResponseEntity.ok(clinicService.findByCityAndProvince(city, province));
        } else if (city != null) {
            return ResponseEntity.ok(clinicService.findByCity(city));
        } else if (province != null) {
            return ResponseEntity.ok(clinicService.findByProvince(province));
        }
        return ResponseEntity.ok(clinicService.findAll());
    }

    // PUT /api/clinics/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Clinic> update(@PathVariable String id, @RequestBody Clinic clinic) {
        Clinic toUpdate = new Clinic.Builder().copy(clinic).setClinicId(id).build();
        Clinic updated = clinicService.update(toUpdate);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/clinics/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clinicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}