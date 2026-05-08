package za.ac.cput.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.Appointment;
import za.ac.cput.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // POST /api/appointments
    // Books a new appointment — validates clinic capacity internally
    @PostMapping
    public ResponseEntity<Appointment> book(@RequestBody Appointment appointment) {
        Appointment booked = appointmentService.book(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(booked);
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable String id) {
        return appointmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/appointments/user/{userId}
    // Fetch all appointments for a patient
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.findByUserId(userId));
    }

    // GET /api/appointments/user/{userId}/upcoming
    // Upcoming BOOKED appointments for a patient — used on the patient dashboard
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<Appointment>> getUpcoming(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.findUpcoming(userId));
    }

    // GET /api/appointments/clinic/{clinicId}?date=2025-06-01&status=BOOKED
    // Staff / admin view of a clinic's appointments for a date
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Appointment>> getByClinic(
            @PathVariable String clinicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    appointmentService.findByClinicDateAndStatus(clinicId, date, status));
        }
        return ResponseEntity.ok(appointmentService.findByClinicAndDate(clinicId, date));
    }

    // PATCH /api/appointments/{id}/cancel
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancel(@PathVariable String id) {
        Appointment cancelled = appointmentService.cancel(id);
        return ResponseEntity.ok(cancelled);
    }

    // PATCH /api/appointments/{id}/complete
    // Used by clinic staff to mark a visit as done
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Appointment> complete(@PathVariable String id) {
        Appointment completed = appointmentService.complete(id);
        return ResponseEntity.ok(completed);
    }

    // PATCH /api/appointments/{id}/no-show
    // Used by admin or automated job when patient doesn't arrive
    @PatchMapping("/{id}/no-show")
    public ResponseEntity<Appointment> markNoShow(@PathVariable String id) {
        Appointment noShow = appointmentService.markNoShow(id);
        return ResponseEntity.ok(noShow);
    }

    // DELETE /api/appointments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}