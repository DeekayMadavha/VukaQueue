//package za.ac.cput.service;
//
//import org.springframework.stereotype.Service;
//import za.ac.cput.model.Clinic;
//import za.ac.cput.repository.ClinicRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ClinicService {
//
//    private final ClinicRepository clinicRepository;
//
//    public ClinicService(ClinicRepository clinicRepository) {
//        this.clinicRepository = clinicRepository;
//    }
//
//    public Clinic createClinic(Clinic clinic) {
//        if (clinicRepository.existsByNameAndAddress(clinic.getName(), clinic.getAddress())) {
//            throw new IllegalArgumentException("A clinic with this name and address already exists.");
//        }
//        return clinicRepository.save(clinic);
//    }
//
//    public Optional<Clinic> findById(String clinicId) {
//        return clinicRepository.findById(clinicId);
//    }
//
//    public List<Clinic> findAll() {
//        return clinicRepository.findAll();
//    }
//
//    public List<Clinic> findByCity(String city) {
//        return clinicRepository.findByCity(city);
//    }
//
//    public List<Clinic> findByProvince(String province) {
//        return clinicRepository.findByProvince(province);
//    }
//
//    public List<Clinic> findByCityAndProvince(String city, String province) {
//        return clinicRepository.findByCityAndProvince(city, province);
//    }
//
//    public Clinic updateClinic(Clinic updatedClinic) {
//        clinicRepository.findById(updatedClinic.getClinicId())
//                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + updatedClinic.getClinicId()));
//        return clinicRepository.save(updatedClinic);
//    }
//
//    public void deleteClinic(String clinicId) {
//        clinicRepository.deleteById(clinicId);
//    }
//}

package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.model.Clinic;
import za.ac.cput.repository.ClinicRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    // Called by: POST /api/clinics
    public Clinic create(Clinic clinic) {
        if (clinicRepository.existsByNameAndAddress(clinic.getName(), clinic.getAddress())) {
            throw new IllegalArgumentException("A clinic with this name and address already exists.");
        }
        return clinicRepository.save(clinic);
    }

    // Called by: GET /api/clinics/{id}
    public Optional<Clinic> findById(String clinicId) {
        return clinicRepository.findById(clinicId);
    }

    // Called by: GET /api/clinics (no filters)
    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    // Called by: GET /api/clinics?city=
    public List<Clinic> findByCity(String city) {
        return clinicRepository.findByCity(city);
    }

    // Called by: GET /api/clinics?province=
    public List<Clinic> findByProvince(String province) {
        return clinicRepository.findByProvince(province);
    }

    // Called by: GET /api/clinics?city=&province=
    public List<Clinic> findByCityAndProvince(String city, String province) {
        return clinicRepository.findByCityAndProvince(city, province);
    }

    // Called by: PUT /api/clinics/{id}
    public Clinic update(Clinic updatedClinic) {
        clinicRepository.findById(updatedClinic.getClinicId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + updatedClinic.getClinicId()));
        return clinicRepository.save(updatedClinic);
    }

    // Called by: DELETE /api/clinics/{id}
    public void delete(String clinicId) {
        clinicRepository.deleteById(clinicId);
    }
}