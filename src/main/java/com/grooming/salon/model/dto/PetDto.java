package com.grooming.salon.model.dto;

import jakarta.validation.constraints.NotBlank;

public class PetDto {

    @NotBlank(message = "Pet name cannot be empty.")
    private String name;

    @NotBlank(message = "Breed cannot be empty.")
    private String breed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}