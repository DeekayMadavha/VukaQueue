package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // All notifications for a user
    List<Notification> findByUserId(String userId);

    // All notifications linked to a specific appointment
    List<Notification> findByAppointmentId(String appointmentId);

    // All notifications by delivery status (e.g., PENDING, SENT, FAILED)
    List<Notification> findByStatus(String status);

    // All notifications by channel (SMS, USSD, IN_APP)
    List<Notification> findByChannel(String channel);

    // Pending notifications due for sending now or earlier
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' " +
            "AND n.scheduledAt <= :now ORDER BY n.scheduledAt ASC")
    List<Notification> findDueNotifications(@Param("now") LocalDateTime now);

    // Check if a specific notification type was already sent for an appointment
    boolean existsByAppointmentIdAndTypeAndStatus(String appointmentId,
                                                  String type,
                                                  String status);

    // All failed notifications for retry logic
    List<Notification> findByStatusAndChannel(String status, String channel);
}