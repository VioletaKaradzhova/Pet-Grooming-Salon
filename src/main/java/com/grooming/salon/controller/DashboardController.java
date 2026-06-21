package com.grooming.salon.controller;

import com.grooming.salon.service.AppointmentService;
import com.grooming.salon.service.PetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class DashboardController {

    private final PetService petService;
    private final AppointmentService appointmentService;

    public DashboardController(PetService petService, AppointmentService appointmentService) {
        this.petService = petService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // Ensure the user is actually logged in
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Fetch the user's specific data from the database
        model.addAttribute("pets", petService.getPetsByOwner(userId));
        model.addAttribute("appointments", appointmentService.getAppointmentsByPetOwner(userId));

        return "dashboard";
    }
}