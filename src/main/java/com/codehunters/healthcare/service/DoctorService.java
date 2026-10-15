package com.codehunters.healthcare.service;

import com.codehunters.healthcare.dto.DoctorDto;
import com.codehunters.healthcare.model.Doctor;
import com.codehunters.healthcare.model.DoctorAvailability;
import com.codehunters.healthcare.repository.DoctorAvailabilityRepository;
import com.codehunters.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream()
            .map(doctor -> new DoctorDto(
                doctor.getDoctorId(),
                doctor.getUser().getFullName(),
                doctor.getSpecialization(),
                doctor.getConsultFee()
            ))
            .collect(Collectors.toList());
    }

    public Doctor findById(Integer id) {
        return doctorRepository.findByDoctorId(id)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    public List<DoctorAvailability> getAvailableSlots(Integer doctorId, LocalDate date) {
        return doctorAvailabilityRepository.findByDoctor_DoctorIdAndSlotDateAndIsBookedFalse(doctorId, date);
    }

    public List<DoctorAvailability> getTodayAndTomorrowSlots(Integer doctorId) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        List<DoctorAvailability> todaySlots = getAvailableSlots(doctorId, today);
        List<DoctorAvailability> tomorrowSlots = getAvailableSlots(doctorId, tomorrow);
        
        todaySlots.addAll(tomorrowSlots);
        return todaySlots;
    }
}
