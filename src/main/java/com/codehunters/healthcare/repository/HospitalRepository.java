package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    List<Hospital> findByEmergencyAvailableTrue();
}
