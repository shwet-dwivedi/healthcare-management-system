package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    List<DoctorAvailability> findByDoctor_DoctorIdAndSlotDateAndIsBookedFalse(Integer doctorId, LocalDate date);
    List<DoctorAvailability> findByDoctor_DoctorIdAndSlotDateBetweenAndIsBookedFalse(Integer doctorId, LocalDate start, LocalDate end);
    List<DoctorAvailability> findByDoctor_DoctorIdAndSlotDate(Integer doctorId, LocalDate date);
}
