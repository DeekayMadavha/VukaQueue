//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.Appointment;
//import za.ac.cput.model.ClinicReport;
//import za.ac.cput.model.QueueEntry;
//import za.ac.cput.repository.AppointmentRepository;
//import za.ac.cput.repository.ClinicReportRepository;
//import za.ac.cput.repository.QueueEntryRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ClinicReportService {
//
//    private final ClinicReportRepository clinicReportRepository;
//    private final AppointmentRepository appointmentRepository;
//    private final QueueEntryRepository queueEntryRepository;
//
//    public ClinicReportService(ClinicReportRepository clinicReportRepository,
//                               AppointmentRepository appointmentRepository,
//                               QueueEntryRepository queueEntryRepository) {
//        this.clinicReportRepository = clinicReportRepository;
//        this.appointmentRepository = appointmentRepository;
//        this.queueEntryRepository = queueEntryRepository;
//    }
//
//    // Generate and persist the daily report for a clinic — called by scheduler at end of day
//    public ClinicReport generateDailyReport(String clinicId, LocalDate date) {
//        if (clinicReportRepository.existsByClinicIdAndReportDate(clinicId, date)) {
//            throw new IllegalStateException("Report already exists for clinic " + clinicId + " on " + date);
//        }
//
//        List<Appointment> appointments = appointmentRepository
//                .findByClinicIdAndAppointmentDate(clinicId, date);
//
//        int total = appointments.size();
//        int completed = (int) appointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
//        int noShows  = (int) appointments.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count();
//        int cancelled = (int) appointments.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count();
//
//        // Derive peak hour from check-in timestamps in the queue
//        List<QueueEntry> entries = queueEntryRepository
//                .findByClinicIdOrderByQueuePositionAsc(clinicId);
//
//        int peakHour = entries.stream()
//                .filter(q -> q.getCheckedInAt() != null)
//                .collect(java.util.stream.Collectors.groupingBy(
//                        q -> q.getCheckedInAt().getHour(),
//                        java.util.stream.Collectors.counting()))
//                .entrySet().stream()
//                .max(java.util.Map.Entry.comparingByValue())
//                .map(java.util.Map.Entry::getKey)
//                .orElse(0);
//
//        double avgWait = entries.stream()
//                .mapToInt(QueueEntry::getEstimatedWaitMinutes)
//                .average()
//                .orElse(0.0);
//
//        ClinicReport report = new ClinicReport.Builder()
//                .setClinicId(clinicId)
//                .setReportDate(date)
//                .setTotalAppointments(total)
//                .setTotalCompleted(completed)
//                .setTotalNoShows(noShows)
//                .setTotalCancelled(cancelled)
//                .setPeakHour(peakHour)
//                .setAverageWaitMinutes(avgWait)
//                .build();
//
//        return clinicReportRepository.save(report);
//    }
//
//    public Optional<ClinicReport> findByClinicAndDate(String clinicId, LocalDate date) {
//        return clinicReportRepository.findByClinicIdAndReportDate(clinicId, date);
//    }
//
//    public List<ClinicReport> findByClinicAndDateRange(String clinicId, LocalDate from, LocalDate to) {
//        return clinicReportRepository
//                .findByClinicIdAndReportDateBetweenOrderByReportDateAsc(clinicId, from, to);
//    }
//
//    public List<ClinicReport> findAllByDate(LocalDate date) {
//        return clinicReportRepository.findByReportDate(date);
//    }
//
//    public List<ClinicReport> findByHighestNoShowRate(LocalDate from, LocalDate to) {
//        return clinicReportRepository.findByNoShowRateDescending(from, to);
//    }
//
//    public Double getAverageWaitTime(String clinicId, LocalDate from, LocalDate to) {
//        return clinicReportRepository.findAverageWaitTime(clinicId, from, to);
//    }
//
//    public void deleteReport(String reportId) {
//        clinicReportRepository.deleteById(reportId);
//    }
//}
package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.model.Appointment;
import za.ac.cput.model.ClinicReport;
import za.ac.cput.model.QueueEntry;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.ClinicReportRepository;
import za.ac.cput.repository.QueueEntryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClinicReportService {

    private final ClinicReportRepository clinicReportRepository;
    private final AppointmentRepository appointmentRepository;
    private final QueueEntryRepository queueEntryRepository;

    public ClinicReportService(ClinicReportRepository clinicReportRepository,
                               AppointmentRepository appointmentRepository,
                               QueueEntryRepository queueEntryRepository) {
        this.clinicReportRepository = clinicReportRepository;
        this.appointmentRepository = appointmentRepository;
        this.queueEntryRepository = queueEntryRepository;
    }

    // Called by: POST /api/reports/generate
    public ClinicReport generateDailyReport(String clinicId, LocalDate date) {
        if (clinicReportRepository.existsByClinicIdAndReportDate(clinicId, date)) {
            throw new IllegalStateException("Report already exists for clinic " + clinicId + " on " + date);
        }

        List<Appointment> appointments = appointmentRepository
                .findByClinicIdAndAppointmentDate(clinicId, date);

        int total     = appointments.size();
        int completed = (int) appointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
        int noShows   = (int) appointments.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count();
        int cancelled = (int) appointments.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count();

        // Derive peak hour from check-in timestamps in the queue
        List<QueueEntry> entries = queueEntryRepository
                .findByClinicIdOrderByQueuePositionAsc(clinicId);

        int peakHour = entries.stream()
                .filter(q -> q.getCheckedInAt() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        q -> q.getCheckedInAt().getHour(),
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(0);

        double avgWait = entries.stream()
                .mapToInt(QueueEntry::getEstimatedWaitMinutes)
                .average()
                .orElse(0.0);

        ClinicReport report = new ClinicReport.Builder()
                .setClinicId(clinicId)
                .setReportDate(date)
                .setTotalAppointments(total)
                .setTotalCompleted(completed)
                .setTotalNoShows(noShows)
                .setTotalCancelled(cancelled)
                .setPeakHour(peakHour)
                .setAverageWaitMinutes(avgWait)
                .build();

        return clinicReportRepository.save(report);
    }

    // Called by: GET /api/reports/clinic/{clinicId}?date=
    public Optional<ClinicReport> findByClinicAndDate(String clinicId, LocalDate date) {
        return clinicReportRepository.findByClinicIdAndReportDate(clinicId, date);
    }

    // Called by: GET /api/reports/clinic/{clinicId}/range?from=&to=
    // FIX: was findByClinicAndDateRange() — renamed to match controller
    public List<ClinicReport> findByDateRange(String clinicId, LocalDate from, LocalDate to) {
        return clinicReportRepository
                .findByClinicIdAndReportDateBetweenOrderByReportDateAsc(clinicId, from, to);
    }

    // Called by: GET /api/reports/daily?date=
    // FIX: was findAllByDate() — renamed to match controller
    public List<ClinicReport> findAllClinicsForDate(LocalDate date) {
        return clinicReportRepository.findByReportDate(date);
    }

    // Called by: GET /api/reports/no-show-ranking?from=&to=
    // FIX: was findByHighestNoShowRate() — renamed to match controller
    public List<ClinicReport> findByNoShowRateDescending(LocalDate from, LocalDate to) {
        return clinicReportRepository.findByNoShowRateDescending(from, to);
    }

    // Called by: GET /api/reports/clinic/{clinicId}/avg-wait?from=&to=
    public Double getAverageWaitTime(String clinicId, LocalDate from, LocalDate to) {
        return clinicReportRepository.findAverageWaitTime(clinicId, from, to);
    }

    // Called by: DELETE /api/reports/{id}
    // FIX: was deleteReport() — renamed to match controller
    public void delete(String reportId) {
        clinicReportRepository.deleteById(reportId);
    }
}