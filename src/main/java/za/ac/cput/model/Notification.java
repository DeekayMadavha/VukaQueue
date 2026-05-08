package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "notification_id", updatable = false, nullable = false)
    private String notificationId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String appointmentId;

    @Column(nullable = false)
    private String channel; // SMS, USSD, IN_APP

    @Column(nullable = false)
    private String type; // APPOINTMENT_CONFIRMED, REMINDER, NEAR_TURN, TURN_NOW, CANCELLED

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String status; // PENDING, SENT, FAILED

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    protected Notification() {}

    private Notification(Builder builder) {
        this.notificationId = builder.notificationId;
        this.userId = builder.userId;
        this.appointmentId = builder.appointmentId;
        this.channel = builder.channel;
        this.type = builder.type;
        this.message = builder.message;
        this.status = builder.status;
        this.scheduledAt = builder.scheduledAt;
        this.sentAt = builder.sentAt;
    }

    public String getNotificationId() { return notificationId; }
    public String getUserId() { return userId; }
    public String getAppointmentId() { return appointmentId; }
    public String getChannel() { return channel; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getSentAt() { return sentAt; }

    public static class Builder {
        private String notificationId;
        private String userId;
        private String appointmentId;
        private String channel;
        private String type;
        private String message;
        private String status;
        private LocalDateTime scheduledAt;
        private LocalDateTime sentAt;

        public Builder setNotificationId(String notificationId) { this.notificationId = notificationId; return this; }
        public Builder setUserId(String userId) { this.userId = userId; return this; }
        public Builder setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; return this; }
        public Builder setChannel(String channel) { this.channel = channel; return this; }
        public Builder setType(String type) { this.type = type; return this; }
        public Builder setMessage(String message) { this.message = message; return this; }
        public Builder setStatus(String status) { this.status = status; return this; }
        public Builder setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; return this; }
        public Builder setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; return this; }

        public Builder copy(Notification n) {
            this.notificationId = n.notificationId;
            this.userId = n.userId;
            this.appointmentId = n.appointmentId;
            this.channel = n.channel;
            this.type = n.type;
            this.message = n.message;
            this.status = n.status;
            this.scheduledAt = n.scheduledAt;
            this.sentAt = n.sentAt;
            return this;
        }

        public Notification build() { return new Notification(this); }
    }
}