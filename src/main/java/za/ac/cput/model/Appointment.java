package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "appointment_id", updatable = false, nullable = false)
    private String appointmentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String clinicId;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private LocalTime appointmentTime;

    @Column(nullable = false)
    private String status; // BOOKED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW

    private String reasonForVisit;

    @Column(nullable = false)
    private boolean smsConfirmationSent;

    protected Appointment() {}

    private Appointment(Builder builder) {
        this.appointmentId = builder.appointmentId;
        this.userId = builder.userId;
        this.clinicId = builder.clinicId;
        this.appointmentDate = builder.appointmentDate;
        this.appointmentTime = builder.appointmentTime;
        this.status = builder.status;
        this.reasonForVisit = builder.reasonForVisit;
        this.smsConfirmationSent = builder.smsConfirmationSent;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getUserId() { return userId; }
    public String getClinicId() { return clinicId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getReasonForVisit() { return reasonForVisit; }
    public boolean isSmsConfirmationSent() { return smsConfirmationSent; }

    public static class Builder {
        private String appointmentId;
        private String userId;
        private String clinicId;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String status;
        private String reasonForVisit;
        private boolean smsConfirmationSent;

        public Builder setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; return this; }
        public Builder setUserId(String userId) { this.userId = userId; return this; }
        public Builder setClinicId(String clinicId) { this.clinicId = clinicId; return this; }
        public Builder setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }
        public Builder setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; return this; }
        public Builder setStatus(String status) { this.status = status; return this; }
        public Builder setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; return this; }
        public Builder setSmsConfirmationSent(boolean smsConfirmationSent) { this.smsConfirmationSent = smsConfirmationSent; return this; }

        public Builder copy(Appointment a) {
            this.appointmentId = a.appointmentId;
            this.userId = a.userId;
            this.clinicId = a.clinicId;
            this.appointmentDate = a.appointmentDate;
            this.appointmentTime = a.appointmentTime;
            this.status = a.status;
            this.reasonForVisit = a.reasonForVisit;
            this.smsConfirmationSent = a.smsConfirmationSent;
            return this;
        }

        public Appointment build() { return new Appointment(this); }
    }
}