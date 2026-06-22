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
        List<Pet> ownerPets = petRepository.findAllByOwnerId(ownerId);
        return appointmentRepository.findAll().stream()
                .filter(appt -> ownerPets.contains(appt.getPet()))
                .toList();
    }

    public void createAppointment(AppointmentDto dto, UUID ownerId) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new BusinessRuleException("Pet not found."));

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

        if (!appt.getPet().getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to cancel this appointment.");
        }

        if (!appt.getStatus().equals("SCHEDULED")) {
            throw new BusinessRuleException("Only SCHEDULED appointments can be cancelled by the client.");
        }

        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
    }

    public Appointment getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found."));
    }

    public void editAppointment(UUID appointmentId, AppointmentDto dto, UUID userId) {
        Appointment appt = getAppointmentById(appointmentId);

        if (!appt.getPet().getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to edit this appointment.");
        }
        if (!appt.getStatus().equals("SCHEDULED")) {
            throw new BusinessRuleException("You can only edit an appointment that is SCHEDULED.");
        }

        Pet newPet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new BusinessRuleException("Pet not found."));
        if (!newPet.getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You cannot book an appointment for a pet you do not own.");
        }

        ServicePackage newService = servicePackageRepository.findById(dto.getServicePackageId())
                .orElseThrow(() -> new BusinessRuleException("Service package not found."));

        appt.setAppointmentDate(dto.getAppointmentDate());
        appt.setPet(newPet);
        appt.setServicePackage(newService);

        appointmentRepository.save(appt);
    }

    public void updateAppointmentStatus(UUID appointmentId, String newStatus, String userRole) {
        Appointment appt = getAppointmentById(appointmentId);
        String currentStatus = appt.getStatus();

        if (currentStatus.equals("COMPLETE") || currentStatus.equals("CANCELLED")) {
            throw new BusinessRuleException("Cannot alter an appointment that is already " + currentStatus);
        }

        if (currentStatus.equals("SCHEDULED")) {
            if (!newStatus.equals("IN_PROGRESS") && !newStatus.equals("CANCELLED")) {
                throw new BusinessRuleException("A SCHEDULED appointment can only be changed to IN_PROGRESS or CANCELLED.");
            }
        } else if (currentStatus.equals("IN_PROGRESS")) {
            if (!newStatus.equals("COMPLETE")) {
                throw new BusinessRuleException("An IN_PROGRESS appointment can only be changed to COMPLETE.");
            }
        }

        appt.setStatus(newStatus);
        appointmentRepository.save(appt);
    }
}