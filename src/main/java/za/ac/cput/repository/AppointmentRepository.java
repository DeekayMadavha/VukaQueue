package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.model.Appointment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    // All appointments for a specific patient
    List<Appointment> findByUserId(String userId);

    // All appointments at a specific clinic
    List<Appointment> findByClinicId(String clinicId);

    // All appointments for a clinic on a given date
    List<Appointment> findByClinicIdAndAppointmentDate(String clinicId, LocalDate appointmentDate);

    // All appointments for a patient at a clinic
    List<Appointment> findByUserIdAndClinicId(String userId, String clinicId);

    // All appointments by status (e.g., all BOOKED or all NO_SHOW)
    List<Appointment> findByStatus(String status);

    // Appointments for a clinic filtered by both date and status
    List<Appointment> findByClinicIdAndAppointmentDateAndStatus(
            String clinicId, LocalDate appointmentDate, String status);

    // Count booked appointments for a clinic on a date (for overbooking logic)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.clinicId = :clinicId " +
            "AND a.appointmentDate = :date AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    int countActiveAppointments(@Param("clinicId") String clinicId,
                                @Param("date") LocalDate date);

    // Find upcoming appointments for a patient (from today onwards)
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId " +
            "AND a.appointmentDate >= :today AND a.status = 'BOOKED' ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingByUserId(@Param("userId") String userId,
                                           @Param("today") LocalDate today);
}