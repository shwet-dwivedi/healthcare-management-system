package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByPatientId(Integer patientId);
    Optional<Patient> findByUser_UserId(Integer userId);
}
