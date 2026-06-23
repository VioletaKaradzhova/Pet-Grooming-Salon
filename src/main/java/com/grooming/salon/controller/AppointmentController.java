package com.grooming.salon.controller;

import com.grooming.salon.model.dto.AppointmentDto;
import com.grooming.salon.model.entity.Appointment;
import com.grooming.salon.service.AppointmentService;
import com.grooming.salon.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (userId == null) return "redirect:/login";

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
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("userPets", petService.getPetsByOwner(userId));
            model.addAttribute("servicePackages", appointmentService.getAllServicePackages());
            return "book-appointment";
        }

        appointmentService.createAppointment(dto, userId);
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable UUID id, HttpSession session, RedirectAttributes redirectAttributes) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        appointmentService.cancelAppointment(id, userId);

        redirectAttributes.addFlashAttribute("successMessage", "Appointment successfully cancelled.");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, HttpSession session, Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        Appointment appt = appointmentService.getAppointmentById(id);

        AppointmentDto dto = new AppointmentDto();
        dto.setAppointmentDate(appt.getAppointmentDate());
        dto.setPetId(appt.getPet().getId());
        dto.setServicePackageId(appt.getServicePackage().getId());

        model.addAttribute("appointmentDto", dto);
        model.addAttribute("appointmentId", id);

        model.addAttribute("userPets", petService.getPetsByOwner(userId));
        model.addAttribute("servicePackages", appointmentService.getAllServicePackages());

        return "edit-appointment";
    }

    @PostMapping("/{id}/edit")
    public String editAppointment(@PathVariable UUID id,
                                  @Valid @ModelAttribute("appointmentDto") AppointmentDto dto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentId", id);
            model.addAttribute("userPets", petService.getPetsByOwner(userId));
            model.addAttribute("servicePackages", appointmentService.getAllServicePackages());
            return "edit-appointment";
        }

        appointmentService.editAppointment(id, dto, userId);

        redirectAttributes.addFlashAttribute("successMessage", "Appointment successfully updated.");
        return "redirect:/dashboard";
    }
}