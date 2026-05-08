package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.model.Notification;
import za.ac.cput.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // POST /api/notifications
    // Manually schedule a notification (used by admin or internal services)
    @PostMapping
    public ResponseEntity<Notification> schedule(@RequestBody Notification notification) {
        Notification created = notificationService.schedule(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/notifications/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable String id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/notifications/user/{userId}
    // Patient's notification history
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }

    // GET /api/notifications/appointment/{appointmentId}
    // All notifications tied to a specific appointment
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Notification>> getByAppointment(
            @PathVariable String appointmentId) {
        return ResponseEntity.ok(notificationService.findByAppointmentId(appointmentId));
    }

    // GET /api/notifications/due
    // Fetches all PENDING notifications scheduled for now or earlier
    // Called by the internal SMS dispatcher on a cron schedule
    @GetMapping("/due")
    public ResponseEntity<List<Notification>> getDue() {
        return ResponseEntity.ok(notificationService.getDueNotifications());
    }

    // GET /api/notifications/failed/sms
    // Returns failed SMS notifications for retry handling
    @GetMapping("/failed/sms")
    public ResponseEntity<List<Notification>> getFailedSms() {
        return ResponseEntity.ok(notificationService.getFailedSmsNotifications());
    }

    // PATCH /api/notifications/{id}/sent
    // Gateway callback — marks notification as delivered
    @PatchMapping("/{id}/sent")
    public ResponseEntity<Notification> markSent(@PathVariable String id) {
        Notification sent = notificationService.markSent(id);
        return ResponseEntity.ok(sent);
    }

    // PATCH /api/notifications/{id}/failed
    // Gateway callback — marks notification as failed
    @PatchMapping("/{id}/failed")
    public ResponseEntity<Notification> markFailed(@PathVariable String id) {
        Notification failed = notificationService.markFailed(id);
        return ResponseEntity.ok(failed);
    }

    // DELETE /api/notifications/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}