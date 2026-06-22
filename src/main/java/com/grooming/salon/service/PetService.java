package com.grooming.salon.service;

import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.dto.PetDto;
import com.grooming.salon.model.entity.Pet;
import com.grooming.salon.model.entity.User;
import com.grooming.salon.repository.PetRepository;
import com.grooming.salon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<Pet> getPetsByOwner(UUID ownerId) {
        return petRepository.findAllByOwnerId(ownerId);
    }

    public void addPet(PetDto dto, UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessRuleException("User not found."));

        Pet pet = new Pet();
        pet.setName(dto.getName());
        pet.setBreed(dto.getBreed());
        pet.setOwner(owner);

        petRepository.save(pet);
    }

    public Pet getPetById(UUID petId, UUID ownerId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessRuleException("Pet not found."));

        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new BusinessRuleException("You do not have permission to view this pet.");
        }
        return pet;
    }

    public void updatePet(UUID petId, PetDto dto, UUID ownerId) {
        Pet pet = getPetById(petId, ownerId);

        pet.setName(dto.getName());
        pet.setBreed(dto.getBreed());

        petRepository.save(pet);
    }

    public void deletePet(UUID petId, UUID ownerId) {
        Pet pet = getPetById(petId, ownerId);

        petRepository.delete(pet);
    }
}