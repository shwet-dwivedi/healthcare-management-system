package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.dto.AppointmentDto;
import com.codehunters.healthcare.model.Appointment;
import com.codehunters.healthcare.model.Doctor;
import com.codehunters.healthcare.model.User;
import com.codehunters.healthcare.repository.AppointmentRepository;
import com.codehunters.healthcare.repository.DoctorRepository;
import com.codehunters.healthcare.repository.UserRepository;
import com.codehunters.healthcare.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Doctor getCurrentDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return doctorRepository.findByDoctorId(user.getUserId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @GetMapping("")
    public String dashboard(Model model) {
        Doctor doctor = getCurrentDoctor();
        List<AppointmentDto> appointments = appointmentService.getAllAppointments().stream()
            .filter(appt -> appt.getDoctorName().equals(doctor.getUser().getFullName()))
            .collect(Collectors.toList());
        
        model.addAttribute("doctorName", doctor.getUser().getFullName());
        model.addAttribute("appointments", appointments);
        return "doctor-dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Doctor doctor = getCurrentDoctor();
        User user = doctor.getUser();
        model.addAttribute("doctorName", user.getFullName());
        model.addAttribute("doctorEmail", user.getEmail());
        model.addAttribute("doctorPhone", user.getPhone());
        return "doctor-profile";
    }

    @PostMapping("/appointments/{id}/confirm")
    public String confirmAppointment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(id);
            Doctor doctor = getCurrentDoctor();
            if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
                throw new RuntimeException("Unauthorized");
            }
            appointmentService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to confirm appointment: " + e.getMessage());
        }
        return "redirect:/doctor";
    }

    @PostMapping("/appointments/{id}/reject")
    public String rejectAppointment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(id);
            Doctor doctor = getCurrentDoctor();
            if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
                throw new RuntimeException("Unauthorized");
            }
            appointmentService.rejectAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel appointment: " + e.getMessage());
        }
        return "redirect:/doctor";
    }

    @GetMapping("/appointments/{id}/record")
    public String showAddRecordForm(
            @PathVariable Integer id,
            Model model) {
        Appointment appointment = appointmentService.findById(id);
        Doctor doctor = getCurrentDoctor();
        if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (appointment.getStatus().name().equals("CONFIRMED")) {
            model.addAttribute("appointmentId", id);
            model.addAttribute("patientName", appointment.getPatient().getUser().getFullName());
            return "add-record";
        }
        return "redirect:/doctor";
    }

    @PostMapping("/appointments/{id}/record")
    public String addMedicalRecord(
            @PathVariable Integer id,
            @RequestParam String diagnosis,
            @RequestParam String prescription,
            @RequestParam String notes,
            RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(id);
            Doctor doctor = getCurrentDoctor();
            if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
                throw new RuntimeException("Unauthorized");
            }
            appointmentService.addMedicalRecord(id, diagnosis, prescription, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Medical record added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add medical record: " + e.getMessage());
        }
        return "redirect:/doctor";
    }
}
