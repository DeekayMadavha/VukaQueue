//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.QueueEntry;
//import za.ac.cput.repository.QueueEntryRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class QueueService {
//
//    private static final int ALERT_THRESHOLD_MINUTES = 15;
//    private static final int AVERAGE_MINUTES_PER_PATIENT = 10;
//
//    private final QueueEntryRepository queueEntryRepository;
//
//    public QueueService(QueueEntryRepository queueEntryRepository) {
//        this.queueEntryRepository = queueEntryRepository;
//    }
//
//    public QueueEntry checkIn(QueueEntry queueEntry) {
//        // Prevent duplicate check-in for the same appointment
//        queueEntryRepository.findByAppointmentId(queueEntry.getAppointmentId())
//                .ifPresent(q -> { throw new IllegalStateException("Patient already checked in for this appointment."); });
//
//        int nextPosition = queueEntryRepository
//                .findMaxActivePosition(queueEntry.getClinicId())
//                .orElse(0) + 1;
//
//        int estimatedWait = nextPosition * AVERAGE_MINUTES_PER_PATIENT;
//
//        QueueEntry entry = new QueueEntry.Builder()
//                .copy(queueEntry)
//                .setQueuePosition(nextPosition)
//                .setEstimatedWaitMinutes(estimatedWait)
//                .setQueueStatus("WAITING")
//                .setCheckedInAt(LocalDateTime.now())
//                .setAlertSent(false)
//                .build();
//
//        return queueEntryRepository.save(entry);
//    }
//
//    public Optional<QueueEntry> findByAppointmentId(String appointmentId) {
//        return queueEntryRepository.findByAppointmentId(appointmentId);
//    }
//
//    public List<QueueEntry> getFullQueue(String clinicId) {
//        return queueEntryRepository.findByClinicIdOrderByQueuePositionAsc(clinicId);
//    }
//
//    public List<QueueEntry> getActiveQueue(String clinicId) {
//        return queueEntryRepository.findByClinicIdAndQueueStatusOrderByQueuePositionAsc(clinicId, "WAITING");
//    }
//
//    public int getPatientsAhead(String clinicId, int queuePosition) {
//        return queueEntryRepository.countPatientsAhead(clinicId, queuePosition);
//    }
//
//    public QueueEntry callNextPatient(String queueEntryId) {
//        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
//                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
//        QueueEntry called = new QueueEntry.Builder()
//                .copy(existing)
//                .setQueueStatus("CALLED")
//                .setCalledAt(LocalDateTime.now())
//                .build();
//        return queueEntryRepository.save(called);
//    }
//
//    public QueueEntry markInProgress(String queueEntryId) {
//        return updateStatus(queueEntryId, "IN_PROGRESS");
//    }
//
//    public QueueEntry markDone(String queueEntryId) {
//        return updateStatus(queueEntryId, "DONE");
//    }
//
//    public QueueEntry markSkipped(String queueEntryId) {
//        return updateStatus(queueEntryId, "SKIPPED");
//    }
//
//    public List<QueueEntry> getPendingAlerts(String clinicId) {
//        return queueEntryRepository.findPendingAlerts(clinicId, ALERT_THRESHOLD_MINUTES);
//    }
//
//    public QueueEntry markAlertSent(String queueEntryId) {
//        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
//                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
//        QueueEntry updated = new QueueEntry.Builder().copy(existing).setAlertSent(true).build();
//        return queueEntryRepository.save(updated);
//    }
//
//    private QueueEntry updateStatus(String queueEntryId, String status) {
//        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
//                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
//        QueueEntry updated = new QueueEntry.Builder().copy(existing).setQueueStatus(status).build();
//        return queueEntryRepository.save(updated);
//    }
//}

package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.model.QueueEntry;
import za.ac.cput.repository.QueueEntryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private static final int ALERT_THRESHOLD_MINUTES = 15;
    private static final int AVERAGE_MINUTES_PER_PATIENT = 10;

    private final QueueEntryRepository queueEntryRepository;

    public QueueService(QueueEntryRepository queueEntryRepository) {
        this.queueEntryRepository = queueEntryRepository;
    }

    // Called by: POST /api/queue/check-in
    // FIX: was checkIn(QueueEntry) — controller passes two strings, not a QueueEntry object
    public QueueEntry checkIn(String appointmentId, String clinicId) {
        queueEntryRepository.findByAppointmentId(appointmentId)
                .ifPresent(q -> {
                    throw new IllegalStateException("Patient already checked in for this appointment.");
                });

        int nextPosition = queueEntryRepository
                .findMaxActivePosition(clinicId)
                .orElse(0) + 1;

        int estimatedWait = nextPosition * AVERAGE_MINUTES_PER_PATIENT;

        QueueEntry entry = new QueueEntry.Builder()
                .setAppointmentId(appointmentId)
                .setClinicId(clinicId)
                .setQueuePosition(nextPosition)
                .setEstimatedWaitMinutes(estimatedWait)
                .setQueueStatus("WAITING")
                .setCheckedInAt(LocalDateTime.now())
                .setAlertSent(false)
                .build();

        return queueEntryRepository.save(entry);
    }

    // Called by: GET /api/queue/appointment/{appointmentId}
    public Optional<QueueEntry> findByAppointmentId(String appointmentId) {
        return queueEntryRepository.findByAppointmentId(appointmentId);
    }

    // Called by: GET /api/queue/clinic/{clinicId}
    // FIX: was getFullQueue() — renamed to match controller
    public List<QueueEntry> getClinicQueue(String clinicId) {
        return queueEntryRepository.findByClinicIdOrderByQueuePositionAsc(clinicId);
    }

    // Called by: GET /api/queue/clinic/{clinicId}/waiting
    // FIX: was getActiveQueue() — renamed to match controller
    public List<QueueEntry> getWaitingQueue(String clinicId) {
        return queueEntryRepository.findByClinicIdAndQueueStatusOrderByQueuePositionAsc(clinicId, "WAITING");
    }

    // Called by: GET /api/queue/clinic/{clinicId}/ahead?position=
    public int getPatientsAhead(String clinicId, int queuePosition) {
        return queueEntryRepository.countPatientsAhead(clinicId, queuePosition);
    }

    // Called by: PATCH /api/queue/{id}/call
    // FIX: was callNextPatient() — renamed to match controller
    public QueueEntry callPatient(String queueEntryId) {
        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
        QueueEntry called = new QueueEntry.Builder()
                .copy(existing)
                .setQueueStatus("CALLED")
                .setCalledAt(LocalDateTime.now())
                .build();
        return queueEntryRepository.save(called);
    }

    // Called by: PATCH /api/queue/{id}/start
    // FIX: was markInProgress() — renamed to match controller
    public QueueEntry startConsultation(String queueEntryId) {
        return updateStatus(queueEntryId, "IN_PROGRESS");
    }

    // Called by: PATCH /api/queue/{id}/complete
    // FIX: was markDone() — renamed to match controller
    public QueueEntry complete(String queueEntryId) {
        return updateStatus(queueEntryId, "DONE");
    }

    // Called by: PATCH /api/queue/{id}/skip
    // FIX: was markSkipped() — renamed to match controller
    public QueueEntry skip(String queueEntryId) {
        return updateStatus(queueEntryId, "SKIPPED");
    }

    // Called by: GET /api/queue/clinic/{clinicId}/pending-alerts
    // FIX: was getPendingAlerts() — renamed to match controller
    public List<QueueEntry> findPendingAlerts(String clinicId) {
        return queueEntryRepository.findPendingAlerts(clinicId, ALERT_THRESHOLD_MINUTES);
    }

    // Called by: PATCH /api/queue/{id}/alert-sent
    public QueueEntry markAlertSent(String queueEntryId) {
        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
        QueueEntry updated = new QueueEntry.Builder()
                .copy(existing)
                .setAlertSent(true)
                .build();
        return queueEntryRepository.save(updated);
    }

    // Called by: QueueController after complete() and skip()
    // FIX: was missing entirely — added to recalculate wait times downstream
    public void recalculateWaitTimes(String clinicId) {
        List<QueueEntry> waiting = getWaitingQueue(clinicId);
        for (int i = 0; i < waiting.size(); i++) {
            QueueEntry entry = waiting.get(i);
            int newEstimate = i * AVERAGE_MINUTES_PER_PATIENT;
            QueueEntry updated = new QueueEntry.Builder()
                    .copy(entry)
                    .setEstimatedWaitMinutes(newEstimate)
                    .build();
            queueEntryRepository.save(updated);
        }
    }

    // Internal utility shared by startConsultation(), complete(), skip()
    private QueueEntry updateStatus(String queueEntryId, String status) {
        QueueEntry existing = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueEntryId));
        QueueEntry updated = new QueueEntry.Builder()
                .copy(existing)
                .setQueueStatus(status)
                .build();
        return queueEntryRepository.save(updated);
    }
}