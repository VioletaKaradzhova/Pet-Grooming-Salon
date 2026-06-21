package com.grooming.salon.controller;

import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.dto.EmployeeDto;
import com.grooming.salon.repository.AppointmentRepository;
import com.grooming.salon.repository.UserRepository;
import com.grooming.salon.service.AppointmentService;
import com.grooming.salon.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final AuthService authService;

    public AdminController(AppointmentRepository appointmentRepository,
                           UserRepository userRepository,
                           AppointmentService appointmentService,
                           AuthService authService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("user_role");

        if (role == null) return "redirect:/login";

        if (role.equals("CLIENT")) {
            model.addAttribute("message", "Access Denied: You do not have permission to view the staff area.");
            return "error";
        }

        model.addAttribute("allAppointments", appointmentRepository.findAll());
        model.addAttribute("allUsers", userRepository.findAll());

        if (!model.containsAttribute("employeeDto")) {
            model.addAttribute("employeeDto", new EmployeeDto());
        }

        return "admin-dashboard";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(@PathVariable UUID id, @RequestParam("status") String newStatus, HttpSession session, Model model) {
        String role = (String) session.getAttribute("user_role");

        if (role == null || role.equals("CLIENT")) return "redirect:/login";

        try {
            appointmentService.updateAppointmentStatus(id, newStatus, role);
        } catch (BusinessRuleException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/employees/create")
    public String createEmployee(@Valid @ModelAttribute("employeeDto") EmployeeDto employeeDto,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        String role = (String) session.getAttribute("user_role");
        if (role == null || role.equals("CLIENT") || role.equals("STAFF")) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("allAppointments", appointmentRepository.findAll());
            model.addAttribute("allUsers", userRepository.findAll());
            return "admin-dashboard";
        }

        try {
            authService.createEmployee(employeeDto, role);
            return "redirect:/admin/dashboard";
        } catch (BusinessRuleException e) {
            model.addAttribute("employeeCreationError", e.getMessage());
            model.addAttribute("allAppointments", appointmentRepository.findAll());
            model.addAttribute("allUsers", userRepository.findAll());
            return "admin-dashboard";
        }
    }
}