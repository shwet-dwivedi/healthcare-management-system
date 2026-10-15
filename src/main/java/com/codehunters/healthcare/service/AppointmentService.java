package com.codehunters.healthcare.service;

import com.codehunters.healthcare.dto.AppointmentDto;
import com.codehunters.healthcare.model.Appointment;
import com.codehunters.healthcare.model.Appointment.AppointmentStatus;
import com.codehunters.healthcare.model.Doctor;
import com.codehunters.healthcare.model.DoctorAvailability;
import com.codehunters.healthcare.model.MedicalRecord;
import com.codehunters.healthcare.model.Patient;
import com.codehunters.healthcare.repository.AppointmentRepository;
import com.codehunters.healthcare.repository.DoctorAvailabilityRepository;
import com.codehunters.healthcare.repository.DoctorRepository;
import com.codehunters.healthcare.repository.MedicalRecordRepository;
import com.codehunters.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public Appointment bookAppointment(Integer patientId, Integer doctorId, Integer slotId, String reason) {
        Patient patient = patientRepository.findByPatientId(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        DoctorAvailability slot = doctorAvailabilityRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found with id: " + slotId));

        if (slot.getIsBooked()) {
            throw new RuntimeException("Slot is already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setReason(reason);
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        slot.setIsBooked(true);
        doctorAvailabilityRepository.save(slot);

        return saved;
    }

    public void confirmAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
    }

    public void rejectAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        DoctorAvailability slot = appointment.getSlot();
        slot.setIsBooked(false);
        doctorAvailabilityRepository.save(slot);
    }

    public void addMedicalRecord(Integer appointmentId, String diagnosis, String prescription, String notes) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Appointment must be in CONFIRMED status to add medical record");
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatient(appointment.getPatient());
        record.setDoctor(appointment.getDoctor());
        record.setAppointment(appointment);
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setNotes(notes);

        medicalRecordRepository.save(record);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    public List<AppointmentDto> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
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

    public Appointment findById(Integer id) {
        return appointmentRepository.findByAppointmentId(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }
}
