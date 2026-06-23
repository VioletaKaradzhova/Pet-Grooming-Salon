package com.grooming.salon.controller;

import com.grooming.salon.model.dto.PetDto;
import com.grooming.salon.model.entity.Pet;
import com.grooming.salon.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (session.getAttribute("user_id") == null) return "redirect:/login";
        model.addAttribute("petDto", new PetDto());
        return "add-pet";
    }

    @PostMapping("/add")
    public String addPet(@Valid @ModelAttribute("petDto") PetDto petDto,
                         BindingResult bindingResult,
                         HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            return "add-pet";
        }

        petService.addPet(petDto, userId);
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/edit")
    public String showEditPetForm(@PathVariable UUID id, HttpSession session, Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        Pet pet = petService.getPetById(id, userId);

        PetDto dto = new PetDto();
        dto.setName(pet.getName());
        dto.setBreed(pet.getBreed());

        model.addAttribute("petDto", dto);
        model.addAttribute("petId", id);
        return "edit-pet";
    }

    @PostMapping("/{id}/edit")
    public String editPet(@PathVariable UUID id,
                          @Valid @ModelAttribute("petDto") PetDto dto,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("petId", id);
            return "edit-pet";
        }

        petService.updatePet(id, dto, userId);

        redirectAttributes.addFlashAttribute("successMessage", "Pet successfully updated.");
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable UUID id, HttpSession session, RedirectAttributes redirectAttributes) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        try {
            petService.deletePet(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Pet successfully deleted.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Action Blocked: You cannot delete a pet that has current, scheduled or past appointments.");
        }

        return "redirect:/dashboard";
    }
}