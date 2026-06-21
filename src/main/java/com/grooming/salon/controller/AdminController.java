package com.grooming.salon.controller;

import com.grooming.salon.repository.AppointmentRepository;
import com.grooming.salon.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AdminController(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("user_role");

        // Access Control: Block Guests
        if (role == null) {
            return "redirect:/login";
        }

        // Access Control: Block Standard Clients
        if (role.equals("CLIENT")) {
            model.addAttribute("message", "Access Denied: You do not have permission to view the staff area.");
            return "error";
        }

        // Allow STAFF and MANAGEMENT
        // Fetch all data in the system for the staff to manage
        model.addAttribute("allAppointments", appointmentRepository.findAll());
        model.addAttribute("allUsers", userRepository.findAll());

        return "admin-dashboard";
    }
}