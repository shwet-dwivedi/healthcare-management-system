package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.dto.AppointmentDto;
import com.codehunters.healthcare.model.User;
import com.codehunters.healthcare.repository.UserRepository;
import com.codehunters.healthcare.service.AppointmentService;
import com.codehunters.healthcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("")
    public String dashboard(Model model) {
        List<User> users = userService.findAll();
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        model.addAttribute("users", users);
        model.addAttribute("appointments", appointments);
        return "admin-dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = getCurrentUser();
        model.addAttribute("adminName", user.getFullName());
        model.addAttribute("adminEmail", user.getEmail());
        model.addAttribute("adminPhone", user.getPhone());
        return "admin-profile";
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            userService.toggleActive(id);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
