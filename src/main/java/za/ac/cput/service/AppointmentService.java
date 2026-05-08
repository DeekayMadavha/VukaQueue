//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.Appointment;
//import za.ac.cput.model.Clinic;
//import za.ac.cput.repository.AppointmentRepository;
//import za.ac.cput.repository.ClinicRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class AppointmentService {
//
//    private final AppointmentRepository appointmentRepository;
//    private final ClinicRepository clinicRepository;
//
//    public AppointmentService(AppointmentRepository appointmentRepository,
//                              ClinicRepository clinicRepository) {
//        this.appointmentRepository = appointmentRepository;
//        this.clinicRepository = clinicRepository;
//    }
//
//    public Appointment bookAppointment(Appointment appointment) {
//        Clinic clinic = clinicRepository.findById(appointment.getClinicId())
//                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + appointment.getClinicId()));
//
//        int activeCount = appointmentRepository.countActiveAppointments(
//                appointment.getClinicId(), appointment.getAppointmentDate());
//
//        if (activeCount >= clinic.getMaxDailyCapacity()) {
//            throw new IllegalStateException("Clinic is fully booked for " + appointment.getAppointmentDate());
//        }
//
//        return appointmentRepository.save(appointment);
//    }
//
//    public Optional<Appointment> findById(String appointmentId) {
//        return appointmentRepository.findById(appointmentId);
//    }
//
//    public List<Appointment> findByUserId(String userId) {
//        return appointmentRepository.findByUserId(userId);
//    }
//
//    public List<Appointment> findUpcomingByUserId(String userId) {
//        return appointmentRepository.findUpcomingByUserId(userId, LocalDate.now());
//    }
//
//    public List<Appointment> findByClinicAndDate(String clinicId, LocalDate date) {
//        return appointmentRepository.findByClinicIdAndAppointmentDate(clinicId, date);
//    }
//
//    public List<Appointment> findByStatus(String status) {
//        return appointmentRepository.findByStatus(status);
//    }
//
//    public Appointment updateStatus(String appointmentId, String newStatus) {
//        Appointment existing = appointmentRepository.findById(appointmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
//        Appointment updated = new Appointment.Builder().copy(existing).setStatus(newStatus).build();
//        return appointmentRepository.save(updated);
//    }
//
//    public Appointment markSmsConfirmationSent(String appointmentId) {
//        Appointment existing = appointmentRepository.findById(appointmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
//        Appointment updated = new Appointment.Builder().copy(existing).setSmsConfirmationSent(true).build();
//        return appointmentRepository.save(updated);
//    }
//
//    public Appointment cancelAppointment(String appointmentId) {
//        return updateStatus(appointmentId, "CANCELLED");
//    }
//
//    public void deleteAppointment(String appointmentId) {
//        appointmentRepository.deleteById(appointmentId);
//    }
//}

package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.model.Appointment;
import za.ac.cput.model.Clinic;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.ClinicRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ClinicRepository clinicRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clinicRepository = clinicRepository;
    }

    // Called by: POST /api/appointments
    public Appointment book(Appointment appointment) {
        Clinic clinic = clinicRepository.findById(appointment.getClinicId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + appointment.getClinicId()));

        int activeCount = appointmentRepository.countActiveAppointments(
                appointment.getClinicId(), appointment.getAppointmentDate());

        if (activeCount >= clinic.getMaxDailyCapacity()) {
            throw new IllegalStateException("Clinic is fully booked for " + appointment.getAppointmentDate());
        }

        return appointmentRepository.save(appointment);
    }

    // Called by: GET /api/appointments/{id}
    public Optional<Appointment> findById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    // Called by: GET /api/appointments/user/{userId}
    public List<Appointment> findByUserId(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    // Called by: GET /api/appointments/user/{userId}/upcoming
    public List<Appointment> findUpcoming(String userId) {
        return appointmentRepository.findUpcomingByUserId(userId, LocalDate.now());
    }

    // Called by: GET /api/appointments/clinic/{clinicId}?date=
    public List<Appointment> findByClinicAndDate(String clinicId, LocalDate date) {
        return appointmentRepository.findByClinicIdAndAppointmentDate(clinicId, date);
    }

    // Called by: GET /api/appointments/clinic/{clinicId}?date=&status=
    public List<Appointment> findByClinicDateAndStatus(String clinicId, LocalDate date, String status) {
        return appointmentRepository.findByClinicIdAndAppointmentDateAndStatus(clinicId, date, status);
    }

    // Called by: PATCH /api/appointments/{id}/cancel
    public Appointment cancel(String appointmentId) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (existing.getStatus().equals("COMPLETED") || existing.getStatus().equals("CANCELLED")) {
            throw new IllegalStateException("Cannot cancel an appointment with status: " + existing.getStatus());
        }

        Appointment cancelled = new Appointment.Builder()
                .copy(existing)
                .setStatus("CANCELLED")
                .build();
        return appointmentRepository.save(cancelled);
    }

    // Called by: PATCH /api/appointments/{id}/complete
    public Appointment complete(String appointmentId) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        Appointment completed = new Appointment.Builder()
                .copy(existing)
                .setStatus("COMPLETED")
                .build();
        return appointmentRepository.save(completed);
    }

    // Called by: PATCH /api/appointments/{id}/no-show
    public Appointment markNoShow(String appointmentId) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        Appointment noShow = new Appointment.Builder()
                .copy(existing)
                .setStatus("NO_SHOW")
                .build();
        return appointmentRepository.save(noShow);
    }

    // Called by: DELETE /api/appointments/{id}
    public void delete(String appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    // Internal utility — mark SMS confirmation as sent
    public Appointment markSmsConfirmationSent(String appointmentId) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        Appointment updated = new Appointment.Builder()
                .copy(existing)
                .setSmsConfirmationSent(true)
                .build();
        return appointmentRepository.save(updated);
    }
}