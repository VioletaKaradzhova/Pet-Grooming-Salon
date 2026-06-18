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

    public void addPet(PetDto petDto, UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessRuleException("Owner not found."));

        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setBreed(petDto.getBreed());
        pet.setOwner(owner);

        petRepository.save(pet);
    }

    public List<Pet> getPetsByOwner(UUID ownerId) {
        return petRepository.findAllByOwnerId(ownerId);
    }
}