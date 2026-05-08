package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.model.ClinicReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicReportRepository extends JpaRepository<ClinicReport, String> {

    // Get the daily report for a specific clinic on a specific date
    Optional<ClinicReport> findByClinicIdAndReportDate(String clinicId, LocalDate reportDate);

    // All reports for a clinic across a date range (for trend analysis)
    List<ClinicReport> findByClinicIdAndReportDateBetweenOrderByReportDateAsc(
            String clinicId, LocalDate from, LocalDate to);

    // All reports across all clinics for a given date (system-wide daily summary)
    List<ClinicReport> findByReportDate(LocalDate reportDate);

    // Clinics with highest no-show rate over a date range
    @Query("SELECT r FROM ClinicReport r WHERE r.reportDate BETWEEN :from AND :to " +
            "ORDER BY (r.totalNoShows * 1.0 / r.totalAppointments) DESC")
    List<ClinicReport> findByNoShowRateDescending(@Param("from") LocalDate from,
                                                  @Param("to") LocalDate to);

    // Average wait time across all reports for a clinic
    @Query("SELECT AVG(r.averageWaitMinutes) FROM ClinicReport r " +
            "WHERE r.clinicId = :clinicId AND r.reportDate BETWEEN :from AND :to")
    Double findAverageWaitTime(@Param("clinicId") String clinicId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);

    // Check if report already exists before generating (avoid duplicates)
    boolean existsByClinicIdAndReportDate(String clinicId, LocalDate reportDate);
}