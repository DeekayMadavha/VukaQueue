package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;

@Entity
@Table(name = "clinic_reports")
public class ClinicReport {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "report_id", updatable = false, nullable = false)
    private String reportId;

    @Column(nullable = false)
    private String clinicId;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false)
    private int totalAppointments;

    @Column(nullable = false)
    private int totalCompleted;

    @Column(nullable = false)
    private int totalNoShows;

    @Column(nullable = false)
    private int totalCancelled;

    @Column(nullable = false)
    private int peakHour; // Hour of day (0–23) with highest traffic

    @Column(nullable = false)
    private double averageWaitMinutes;

    protected ClinicReport() {}

    private ClinicReport(Builder builder) {
        this.reportId = builder.reportId;
        this.clinicId = builder.clinicId;
        this.reportDate = builder.reportDate;
        this.totalAppointments = builder.totalAppointments;
        this.totalCompleted = builder.totalCompleted;
        this.totalNoShows = builder.totalNoShows;
        this.totalCancelled = builder.totalCancelled;
        this.peakHour = builder.peakHour;
        this.averageWaitMinutes = builder.averageWaitMinutes;
    }

    public String getReportId() { return reportId; }
    public String getClinicId() { return clinicId; }
    public LocalDate getReportDate() { return reportDate; }
    public int getTotalAppointments() { return totalAppointments; }
    public int getTotalCompleted() { return totalCompleted; }
    public int getTotalNoShows() { return totalNoShows; }
    public int getTotalCancelled() { return totalCancelled; }
    public int getPeakHour() { return peakHour; }
    public double getAverageWaitMinutes() { return averageWaitMinutes; }

    public static class Builder {
        private String reportId;
        private String clinicId;
        private LocalDate reportDate;
        private int totalAppointments;
        private int totalCompleted;
        private int totalNoShows;
        private int totalCancelled;
        private int peakHour;
        private double averageWaitMinutes;

        public Builder setReportId(String reportId) { this.reportId = reportId; return this; }
        public Builder setClinicId(String clinicId) { this.clinicId = clinicId; return this; }
        public Builder setReportDate(LocalDate reportDate) { this.reportDate = reportDate; return this; }
        public Builder setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; return this; }
        public Builder setTotalCompleted(int totalCompleted) { this.totalCompleted = totalCompleted; return this; }
        public Builder setTotalNoShows(int totalNoShows) { this.totalNoShows = totalNoShows; return this; }
        public Builder setTotalCancelled(int totalCancelled) { this.totalCancelled = totalCancelled; return this; }
        public Builder setPeakHour(int peakHour) { this.peakHour = peakHour; return this; }
        public Builder setAverageWaitMinutes(double averageWaitMinutes) { this.averageWaitMinutes = averageWaitMinutes; return this; }

        public Builder copy(ClinicReport r) {
            this.reportId = r.reportId;
            this.clinicId = r.clinicId;
            this.reportDate = r.reportDate;
            this.totalAppointments = r.totalAppointments;
            this.totalCompleted = r.totalCompleted;
            this.totalNoShows = r.totalNoShows;
            this.totalCancelled = r.totalCancelled;
            this.peakHour = r.peakHour;
            this.averageWaitMinutes = r.averageWaitMinutes;
            return this;
        }

        public ClinicReport build() { return new ClinicReport(this); }
    }
}