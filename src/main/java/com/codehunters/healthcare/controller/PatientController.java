package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.dto.AppointmentDto;
import com.codehunters.healthcare.dto.MedicalRecordDto;
import com.codehunters.healthcare.dto.SlotDto;
import com.codehunters.healthcare.dto.DoctorDto;
import com.codehunters.healthcare.model.Doctor;
import com.codehunters.healthcare.model.DoctorAvailability;
import com.codehunters.healthcare.model.Patient;
import com.codehunters.healthcare.model.User;
import com.codehunters.healthcare.repository.UserRepository;
import com.codehunters.healthcare.service.AppointmentService;
import com.codehunters.healthcare.service.DoctorService;
import com.codehunters.healthcare.service.PatientService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    private Patient getCurrentPatient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        // DEMO-ONLY: Handle demo user without database lookup
        if ("demo@carelink.app".equals(email)) {
            Patient demoPatient = new Patient();
            demoPatient.setPatientId(-1); // Special ID for demo
            User demoUser = new User();
            demoUser.setFullName("Demo User");
            demoUser.setEmail("demo@carelink.app");
            demoUser.setPhone("---");
            demoPatient.setUser(demoUser);
            return demoPatient;
        }
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return patientService.findByUserId(user.getUserId());
    }

    @GetMapping("")
    public String dashboard(Model model) {
        Patient patient = getCurrentPatient();
        List<AppointmentDto> appointments = patientService.getAppointments(patient.getPatientId());
        model.addAttribute("patientName", patient.getUser().getFullName());
        model.addAttribute("appointments", appointments);
        // DEMO-ONLY: Add demo flag for template
        model.addAttribute("isDemo", patient.getPatientId() == -1);
        return "patient-dashboard";
    }

    @GetMapping("/book")
    public String bookDoctorList(Model model) {
        List<DoctorDto> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        return "book-doctor-list";
    }

    @GetMapping("/book/{doctorId}")
    public String bookSlots(@PathVariable Integer doctorId, Model model) {
        Doctor doctor = doctorService.findById(doctorId);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<DoctorAvailability> todaySlots = doctorService.getAvailableSlots(doctorId, today);
        List<DoctorAvailability> tomorrowSlots = doctorService.getAvailableSlots(doctorId, tomorrow);

        List<SlotDto> todaySlotDtos = todaySlots.stream()
            .map(slot -> new SlotDto(slot.getSlotId(), slot.getStartTime().toString(), slot.getEndTime().toString()))
            .collect(Collectors.toList());

        List<SlotDto> tomorrowSlotDtos = tomorrowSlots.stream()
            .map(slot -> new SlotDto(slot.getSlotId(), slot.getStartTime().toString(), slot.getEndTime().toString()))
            .collect(Collectors.toList());

        model.addAttribute("doctorId", doctorId);
        model.addAttribute("doctorName", doctor.getUser().getFullName());
        model.addAttribute("specialization", doctor.getSpecialization());
        model.addAttribute("todaySlots", todaySlotDtos);
        model.addAttribute("tomorrowSlots", tomorrowSlotDtos);
        return "book-slots";
    }

    @PostMapping("/book")
    public String bookAppointment(
            @RequestParam Integer doctorId,
            @RequestParam Integer slotId,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        try {
            Patient patient = getCurrentPatient();
            // DEMO-ONLY: Prevent demo user from booking
            if (patient.getPatientId() == -1) {
                redirectAttributes.addFlashAttribute("errorMessage", "Demo mode: cannot book appointments. Sign in with a real account to book.");
                return "redirect:/patient/book/" + doctorId;
            }
            appointmentService.bookAppointment(patient.getPatientId(), doctorId, slotId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully!");
            return "redirect:/patient";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to book appointment: " + e.getMessage());
            return "redirect:/patient/book/" + doctorId;
        }
    }

    @GetMapping("/history")
    public String history(Model model) {
        Patient patient = getCurrentPatient();
        List<MedicalRecordDto> records = patientService.getMedicalRecords(patient.getPatientId());
        model.addAttribute("patientName", patient.getUser().getFullName());
        model.addAttribute("records", records);
        return "patient-history";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Patient patient = getCurrentPatient();
        User user = patient.getUser();
        model.addAttribute("patientName", user.getFullName());
        model.addAttribute("patientEmail", user.getEmail());
        model.addAttribute("patientPhone", user.getPhone());
        return "patient-profile";
    }
}
