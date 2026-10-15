package com.codehunters.healthcare.service;

import com.codehunters.healthcare.dto.AppointmentDto;
import com.codehunters.healthcare.dto.MedicalRecordDto;
import com.codehunters.healthcare.model.Appointment;
import com.codehunters.healthcare.model.MedicalRecord;
import com.codehunters.healthcare.model.Patient;
import com.codehunters.healthcare.repository.AppointmentRepository;
import com.codehunters.healthcare.repository.MedicalRecordRepository;
import com.codehunters.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public Patient findByUserId(Integer userId) {
        return patientRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new RuntimeException("Patient not found with user id: " + userId));
    }

    public List<AppointmentDto> getAppointments(Integer patientId) {
        // DEMO-ONLY: Return empty list for demo user (patientId -1)
        if (patientId == -1) {
            return Collections.emptyList();
        }
        
        List<Appointment> appointments = appointmentRepository.findByPatient_PatientIdOrderBySlot_SlotDateDesc(patientId);
        return appointments.stream()
            .map(appt -> new AppointmentDto(
                appt.getAppointmentId(),
                appt.getPatient().getUser().getFullName(),
                appt.getDoctor().getUser().getFullName(),
                appt.getDoctor().getSpecialization(),
                appt.getSlot().getSlotDate(),
                appt.getSlot().getStartTime(),
                appt.getReason(),
                appt.getStatus()
            ))
            .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> getMedicalRecords(Integer patientId) {
        // DEMO-ONLY: Return empty list for demo user (patientId -1)
        if (patientId == -1) {
            return Collections.emptyList();
        }
        
        List<MedicalRecord> records = medicalRecordRepository.findByPatient_PatientIdOrderByRecordDateDesc(patientId);
        return records.stream()
            .map(record -> new MedicalRecordDto(
                record.getDoctor().getUser().getFullName(),
                record.getRecordDate().toLocalDate().toString(),
                record.getDiagnosis(),
                record.getPrescription(),
                record.getNotes()
            ))
            .collect(Collectors.toList());
    }
}
