package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.QueueEntry;
import za.ac.cput.service.QueueService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    // POST /api/queue/check-in
    // Patient checks in on arrival — body: { "appointmentId": "...", "clinicId": "..." }
    @PostMapping("/check-in")
    public ResponseEntity<QueueEntry> checkIn(@RequestBody Map<String, String> body) {
        String appointmentId = body.get("appointmentId");
        String clinicId = body.get("clinicId");
        QueueEntry entry = queueService.checkIn(appointmentId, clinicId);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    // GET /api/queue/appointment/{appointmentId}
    // Patient polls this to see their current position and estimated wait
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<QueueEntry> getByAppointment(@PathVariable String appointmentId) {
        return queueService.findByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/queue/clinic/{clinicId}
    // Full ordered queue for a clinic — real-time staff view
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<QueueEntry>> getClinicQueue(@PathVariable String clinicId) {
        return ResponseEntity.ok(queueService.getClinicQueue(clinicId));
    }

    // GET /api/queue/clinic/{clinicId}/waiting
    // Only patients currently waiting — used for the live queue board
    @GetMapping("/clinic/{clinicId}/waiting")
    public ResponseEntity<List<QueueEntry>> getWaiting(@PathVariable String clinicId) {
        return ResponseEntity.ok(queueService.getWaitingQueue(clinicId));
    }

    // GET /api/queue/clinic/{clinicId}/ahead?position=5
    // How many patients are ahead of a given position number
    @GetMapping("/clinic/{clinicId}/ahead")
    public ResponseEntity<Map<String, Integer>> getPatientsAhead(
            @PathVariable String clinicId,
            @RequestParam int position) {
        int count = queueService.getPatientsAhead(clinicId, position);
        return ResponseEntity.ok(Map.of("patientsAhead", count));
    }

    // PATCH /api/queue/{id}/call
    // Staff calls the next patient
    @PatchMapping("/{id}/call")
    public ResponseEntity<QueueEntry> callPatient(@PathVariable String id) {
        QueueEntry called = queueService.callPatient(id);
        return ResponseEntity.ok(called);
    }

    // PATCH /api/queue/{id}/start
    // Consultation begins
    @PatchMapping("/{id}/start")
    public ResponseEntity<QueueEntry> startConsultation(@PathVariable String id) {
        QueueEntry inProgress = queueService.startConsultation(id);
        return ResponseEntity.ok(inProgress);
    }

    // PATCH /api/queue/{id}/complete
    // Consultation done — triggers wait time recalculation
    @PatchMapping("/{id}/complete")
    public ResponseEntity<QueueEntry> complete(@PathVariable String id) {
        QueueEntry done = queueService.complete(id);
        // Recalculate downstream wait estimates after each completion
        queueService.recalculateWaitTimes(done.getClinicId());
        return ResponseEntity.ok(done);
    }

    // PATCH /api/queue/{id}/skip
    // Patient did not respond when called
    @PatchMapping("/{id}/skip")
    public ResponseEntity<QueueEntry> skip(@PathVariable String id) {
        QueueEntry skipped = queueService.skip(id);
        queueService.recalculateWaitTimes(skipped.getClinicId());
        return ResponseEntity.ok(skipped);
    }

    // GET /api/queue/clinic/{clinicId}/pending-alerts
    // Scheduler or admin fetches entries needing a near-turn SMS
    @GetMapping("/clinic/{clinicId}/pending-alerts")
    public ResponseEntity<List<QueueEntry>> getPendingAlerts(@PathVariable String clinicId) {
        return ResponseEntity.ok(queueService.findPendingAlerts(clinicId));
    }

    // PATCH /api/queue/{id}/alert-sent
    // Mark that the near-turn SMS was dispatched for this entry
    @PatchMapping("/{id}/alert-sent")
    public ResponseEntity<QueueEntry> markAlertSent(@PathVariable String id) {
        QueueEntry updated = queueService.markAlertSent(id);
        return ResponseEntity.ok(updated);
    }
}