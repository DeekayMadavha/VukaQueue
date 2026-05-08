package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "queue_entry_id", updatable = false, nullable = false)
    private String queueEntryId;

    @Column(nullable = false)
    private String appointmentId;

    @Column(nullable = false)
    private String clinicId;

    @Column(nullable = false)
    private int queuePosition;

    @Column(nullable = false)
    private int estimatedWaitMinutes;

    @Column(nullable = false)
    private String queueStatus; // WAITING, CALLED, IN_PROGRESS, DONE, SKIPPED

    @Column(nullable = false)
    private LocalDateTime checkedInAt;

    private LocalDateTime calledAt;

    private boolean alertSent; // SMS near-turn alert

    protected QueueEntry() {}

    private QueueEntry(Builder builder) {
        this.queueEntryId = builder.queueEntryId;
        this.appointmentId = builder.appointmentId;
        this.clinicId = builder.clinicId;
        this.queuePosition = builder.queuePosition;
        this.estimatedWaitMinutes = builder.estimatedWaitMinutes;
        this.queueStatus = builder.queueStatus;
        this.checkedInAt = builder.checkedInAt;
        this.calledAt = builder.calledAt;
        this.alertSent = builder.alertSent;
    }

    public String getQueueEntryId() { return queueEntryId; }
    public String getAppointmentId() { return appointmentId; }
    public String getClinicId() { return clinicId; }
    public int getQueuePosition() { return queuePosition; }
    public int getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public String getQueueStatus() { return queueStatus; }
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public LocalDateTime getCalledAt() { return calledAt; }
    public boolean isAlertSent() { return alertSent; }

    public static class Builder {
        private String queueEntryId;
        private String appointmentId;
        private String clinicId;
        private int queuePosition;
        private int estimatedWaitMinutes;
        private String queueStatus;
        private LocalDateTime checkedInAt;
        private LocalDateTime calledAt;
        private boolean alertSent;

        public Builder setQueueEntryId(String queueEntryId) { this.queueEntryId = queueEntryId; return this; }
        public Builder setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; return this; }
        public Builder setClinicId(String clinicId) { this.clinicId = clinicId; return this; }
        public Builder setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; return this; }
        public Builder setEstimatedWaitMinutes(int estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; return this; }
        public Builder setQueueStatus(String queueStatus) { this.queueStatus = queueStatus; return this; }
        public Builder setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; return this; }
        public Builder setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; return this; }
        public Builder setAlertSent(boolean alertSent) { this.alertSent = alertSent; return this; }

        public Builder copy(QueueEntry q) {
            this.queueEntryId = q.queueEntryId;
            this.appointmentId = q.appointmentId;
            this.clinicId = q.clinicId;
            this.queuePosition = q.queuePosition;
            this.estimatedWaitMinutes = q.estimatedWaitMinutes;
            this.queueStatus = q.queueStatus;
            this.checkedInAt = q.checkedInAt;
            this.calledAt = q.calledAt;
            this.alertSent = q.alertSent;
            return this;
        }

        public QueueEntry build() { return new QueueEntry(this); }
    }
}