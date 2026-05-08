package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.model.QueueEntry;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, String> {

    // Get a patient's queue entry by their appointment
    Optional<QueueEntry> findByAppointmentId(String appointmentId);

    // Full queue for a clinic, ordered by position
    List<QueueEntry> findByClinicIdOrderByQueuePositionAsc(String clinicId);

    // Active queue only (exclude DONE and SKIPPED)
    List<QueueEntry> findByClinicIdAndQueueStatusOrderByQueuePositionAsc(
            String clinicId, String queueStatus);

    // Count how many patients are still waiting ahead in the queue
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.clinicId = :clinicId " +
            "AND q.queueStatus = 'WAITING' AND q.queuePosition < :position")
    int countPatientsAhead(@Param("clinicId") String clinicId,
                           @Param("position") int position);

    // Find entries where alert has not yet been sent and patient is close to their turn
    @Query("SELECT q FROM QueueEntry q WHERE q.clinicId = :clinicId " +
            "AND q.queueStatus = 'WAITING' AND q.alertSent = false " +
            "AND q.estimatedWaitMinutes <= :thresholdMinutes")
    List<QueueEntry> findPendingAlerts(@Param("clinicId") String clinicId,
                                       @Param("thresholdMinutes") int thresholdMinutes);

    // Highest current queue position for a clinic (used to assign next position)
    @Query("SELECT MAX(q.queuePosition) FROM QueueEntry q WHERE q.clinicId = :clinicId " +
            "AND q.queueStatus IN ('WAITING', 'CALLED', 'IN_PROGRESS')")
    Optional<Integer> findMaxActivePosition(@Param("clinicId") String clinicId);
}