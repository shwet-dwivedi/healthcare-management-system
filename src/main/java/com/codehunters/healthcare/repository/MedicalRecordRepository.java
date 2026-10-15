package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findByPatient_PatientIdOrderByRecordDateDesc(Integer patientId);
    List<MedicalRecord> findByDoctor_DoctorIdOrderByRecordDateDesc(Integer doctorId);
}
