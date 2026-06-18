package com.grooming.salon.controller;

import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.dto.LoginDto;
import com.grooming.salon.model.entity.User;
import com.grooming.salon.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            User user = authService.authenticateUser(loginDto);
            session.setAttribute("user_id", user.getId());
            return "redirect:/dashboard";
        } catch (BusinessRuleException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            User user = authService.registerUser(loginDto);
            session.setAttribute("user_id", user.getId());
            return "redirect:/dashboard";
        } catch (BusinessRuleException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}