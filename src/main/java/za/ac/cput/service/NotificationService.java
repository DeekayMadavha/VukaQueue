//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.Notification;
//import za.ac.cput.repository.NotificationRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//
//    public NotificationService(NotificationRepository notificationRepository) {
//        this.notificationRepository = notificationRepository;
//    }
//
//    public Notification scheduleNotification(Notification notification) {
//        // Guard: don't schedule duplicates of the same type for the same appointment
//        boolean alreadyScheduled = notificationRepository.existsByAppointmentIdAndTypeAndStatus(
//                notification.getAppointmentId(), notification.getType(), "PENDING");
//        if (alreadyScheduled) {
//            throw new IllegalStateException("A pending notification of type '"
//                    + notification.getType() + "' already exists for this appointment.");
//        }
//        return notificationRepository.save(notification);
//    }
//
//    public Optional<Notification> findById(String notificationId) {
//        return notificationRepository.findById(notificationId);
//    }
//
//    public List<Notification> findByUserId(String userId) {
//        return notificationRepository.findByUserId(userId);
//    }
//
//    public List<Notification> findByAppointmentId(String appointmentId) {
//        return notificationRepository.findByAppointmentId(appointmentId);
//    }
//
//    // Called by the scheduler to fetch all notifications ready to be sent
//    public List<Notification> getDueNotifications() {
//        return notificationRepository.findDueNotifications(LocalDateTime.now());
//    }
//
//    // Called by the scheduler or SMS gateway callback after successful dispatch
//    public Notification markAsSent(String notificationId) {
//        Notification existing = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
//        Notification sent = new Notification.Builder()
//                .copy(existing)
//                .setStatus("SENT")
//                .setSentAt(LocalDateTime.now())
//                .build();
//        return notificationRepository.save(sent);
//    }
//
//    // Called when the SMS gateway returns a failure
//    public Notification markAsFailed(String notificationId) {
//        Notification existing = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
//        Notification failed = new Notification.Builder()
//                .copy(existing)
//                .setStatus("FAILED")
//                .build();
//        return notificationRepository.save(failed);
//    }
//
//    // Retry: fetch all FAILED SMS notifications for re-dispatch
//    public List<Notification> getFailedSmsNotifications() {
//        return notificationRepository.findByStatusAndChannel("FAILED", "SMS");
//    }
//
//    public void deleteNotification(String notificationId) {
//        notificationRepository.deleteById(notificationId);
//    }
//}

package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.model.Notification;
import za.ac.cput.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Called by: POST /api/notifications
    // FIX: was scheduleNotification() — renamed to match controller
    public Notification schedule(Notification notification) {
        boolean alreadyScheduled = notificationRepository.existsByAppointmentIdAndTypeAndStatus(
                notification.getAppointmentId(), notification.getType(), "PENDING");
        if (alreadyScheduled) {
            throw new IllegalStateException("A pending notification of type '"
                    + notification.getType() + "' already exists for this appointment.");
        }
        return notificationRepository.save(notification);
    }

    // Called by: GET /api/notifications/{id}
    public Optional<Notification> findById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    // Called by: GET /api/notifications/user/{userId}
    public List<Notification> findByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Called by: GET /api/notifications/appointment/{appointmentId}
    public List<Notification> findByAppointmentId(String appointmentId) {
        return notificationRepository.findByAppointmentId(appointmentId);
    }

    // Called by: GET /api/notifications/due
    public List<Notification> getDueNotifications() {
        return notificationRepository.findDueNotifications(LocalDateTime.now());
    }

    // Called by: GET /api/notifications/failed/sms
    public List<Notification> getFailedSmsNotifications() {
        return notificationRepository.findByStatusAndChannel("FAILED", "SMS");
    }

    // Called by: PATCH /api/notifications/{id}/sent
    // FIX: was markAsSent() — renamed to match controller
    public Notification markSent(String notificationId) {
        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        Notification sent = new Notification.Builder()
                .copy(existing)
                .setStatus("SENT")
                .setSentAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(sent);
    }

    // Called by: PATCH /api/notifications/{id}/failed
    // FIX: was markAsFailed() — renamed to match controller
    public Notification markFailed(String notificationId) {
        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        Notification failed = new Notification.Builder()
                .copy(existing)
                .setStatus("FAILED")
                .build();
        return notificationRepository.save(failed);
    }

    // Called by: DELETE /api/notifications/{id}
    // FIX: was deleteNotification() — renamed to match controller
    public void delete(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}