package com.grooming.salon.controller;

import com.grooming.salon.repository.ServicePackageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    private final ServicePackageRepository servicePackageRepository;

    public PublicController(ServicePackageRepository servicePackageRepository) {
        this.servicePackageRepository = servicePackageRepository;
    }

    @GetMapping("/services")
    public String showPublicServices(Model model) {
        model.addAttribute("services", servicePackageRepository.findAll());
        return "services";
    }

    @GetMapping("/services/bath-and-brush")
    public String showBathAndBrush() { return "service-bath"; }

    @GetMapping("/services/full-spa")
    public String showFullSpa() { return "service-spa"; }

    @GetMapping("/services/nail-trim")
    public String showNailTrim() { return "service-nails"; }

    @GetMapping("/services/flea-tick")
    public String showFleaTick() { return "service-flea"; }

    @GetMapping("/services/puppy-trim")
    public String showPuppyTrim() { return "service-puppy"; }
}