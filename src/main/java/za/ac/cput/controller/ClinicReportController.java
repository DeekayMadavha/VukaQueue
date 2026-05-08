package za.ac.cput.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.ClinicReport;
import za.ac.cput.service.ClinicReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ClinicReportController {

    private final ClinicReportService clinicReportService;

    public ClinicReportController(ClinicReportService clinicReportService) {
        this.clinicReportService = clinicReportService;
    }

    // POST /api/reports/generate?clinicId=...&date=2025-06-01
    // Triggers daily report generation for a clinic — called by scheduler or admin
    @PostMapping("/generate")
    public ResponseEntity<ClinicReport> generate(
            @RequestParam String clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ClinicReport report = clinicReportService.generateDailyReport(clinicId, date);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    // GET /api/reports/clinic/{clinicId}?date=2025-06-01
    // Single daily report for a clinic
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<ClinicReport> getByClinicAndDate(
            @PathVariable String clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return clinicReportService.findByClinicAndDate(clinicId, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/reports/clinic/{clinicId}/range?from=2025-06-01&to=2025-06-30
    // Trend data for a clinic over a date range
    @GetMapping("/clinic/{clinicId}/range")
    public ResponseEntity<List<ClinicReport>> getByDateRange(
            @PathVariable String clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(clinicReportService.findByDateRange(clinicId, from, to));
    }

    // GET /api/reports/daily?date=2025-06-01
    // System-wide summary — all clinics on a given date (DoH oversight)
    @GetMapping("/daily")
    public ResponseEntity<List<ClinicReport>> getDailyAllClinics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(clinicReportService.findAllClinicsForDate(date));
    }

    // GET /api/reports/no-show-ranking?from=2025-06-01&to=2025-06-30
    // Clinics ranked by no-show rate — admin intervention view
    @GetMapping("/no-show-ranking")
    public ResponseEntity<List<ClinicReport>> getNoShowRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(clinicReportService.findByNoShowRateDescending(from, to));
    }

    // GET /api/reports/clinic/{clinicId}/avg-wait?from=2025-06-01&to=2025-06-30
    // Average wait time for a clinic — key success metric
    @GetMapping("/clinic/{clinicId}/avg-wait")
    public ResponseEntity<Map<String, Double>> getAverageWaitTime(
            @PathVariable String clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Double avg = clinicReportService.getAverageWaitTime(clinicId, from, to);
        return ResponseEntity.ok(Map.of("averageWaitMinutes", avg != null ? avg : 0.0));
    }

    // DELETE /api/reports/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clinicReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}