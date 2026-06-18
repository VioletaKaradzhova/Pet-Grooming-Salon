package com.grooming.salon.controller;
import com.grooming.salon.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        appointmentService.cancelAppointment(id, userId);
        return "redirect:/dashboard";
    }
}