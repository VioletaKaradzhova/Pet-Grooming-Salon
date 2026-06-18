package com.grooming.salon.controller;

import com.grooming.salon.model.dto.PetDto;
import com.grooming.salon.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/add")
    public String showAddPetForm(HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }
        model.addAttribute("petDto", new PetDto());
        return "add-pet";
    }

    @PostMapping("/add")
    public String addPet(@Valid @ModelAttribute("petDto") PetDto petDto,
                         BindingResult bindingResult,
                         HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "add-pet";
        }

        petService.addPet(petDto, userId);
        return "redirect:/dashboard";
    }
}