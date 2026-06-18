package com.grooming.salon.controller;

import com.grooming.salon.model.dto.AppointmentDto;
import com.grooming.salon.service.AppointmentService;
import com.grooming.salon.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PetService petService;

    public AppointmentController(AppointmentService appointmentService, PetService petService) {
        this.appointmentService = appointmentService;
        this.petService = petService;
    }

    @GetMapping("/book")
    public String showBookingForm(HttpSession session, Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login"; // Access control for guests
        }

        // Pass data to the Thymeleaf view to populate the HTML <select> dropdowns
        model.addAttribute("appointmentDto", new AppointmentDto());
        model.addAttribute("userPets", petService.getPetsByOwner(userId));
        model.addAttribute("servicePackages", appointmentService.getAllServicePackages());

        return "book-appointment";
    }

    @PostMapping("/book")
    public String bookAppointment(@Valid @ModelAttribute("appointmentDto") AppointmentDto dto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // Repopulate dropdowns before returning to the view if validation fails
            model.addAttribute("userPets", petService.getPetsByOwner(userId));
            model.addAttribute("servicePackages", appointmentService.getAllServicePackages());
            return "book-appointment";
        }

        appointmentService.createAppointment(dto, userId);
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        appointmentService.cancelAppointment(id, userId);
        return "redirect:/dashboard";
    }
}