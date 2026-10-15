package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByPatient_PatientIdOrderBySlot_SlotDateDesc(Integer patientId);
    List<Appointment> findByDoctor_DoctorIdOrderBySlot_SlotDateDesc(Integer doctorId);
    Optional<Appointment> findByAppointmentId(Integer appointmentId);
}
