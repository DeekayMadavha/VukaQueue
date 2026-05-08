package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.model.Clinic;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, String> {

    List<Clinic> findByCity(String city);

    List<Clinic> findByProvince(String province);

    List<Clinic> findByCityAndProvince(String city, String province);

    boolean existsByNameAndAddress(String name, String address);
}