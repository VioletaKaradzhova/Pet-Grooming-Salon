package com.grooming.salon.service;

import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.dto.AppointmentDto;
import com.grooming.salon.model.entity.Appointment;
import com.grooming.salon.model.entity.Pet;
import com.grooming.salon.model.entity.ServicePackage;
import com.grooming.salon.repository.AppointmentRepository;
import com.grooming.salon.repository.PetRepository;
import com.grooming.salon.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ServicePackageRepository servicePackageRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              ServicePackageRepository servicePackageRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.servicePackageRepository = servicePackageRepository;
    }

    public List<ServicePackage> getAllServicePackages() {
        return servicePackageRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPetOwner(UUID ownerId) {
        // Fetches all pets for the owner, then gets all appointments for those pets
        List<Pet> ownerPets = petRepository.findAllByOwnerId(ownerId);
        return appointmentRepository.findAll().stream()
                .filter(appt -> ownerPets.contains(appt.getPet()))
                .toList();
    }

    public void createAppointment(AppointmentDto dto, UUID ownerId) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new BusinessRuleException("Pet not found."));

        // Security check: Ensure the pet actually belongs to the logged-in user
        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new BusinessRuleException("You do not have permission to book for this pet.");
        }

        ServicePackage servicePackage = servicePackageRepository.findById(dto.getServicePackageId())
                .orElseThrow(() -> new BusinessRuleException("Service package not found."));

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setPet(pet);
        appointment.setServicePackage(servicePackage);
        appointment.setStatus("SCHEDULED");

        appointmentRepository.save(appointment);
    }

    public void cancelAppointment(UUID appointmentId, UUID userId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found."));

        // Security check
        if (!appt.getPet().getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to cancel this appointment.");
        }

        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
    }
}