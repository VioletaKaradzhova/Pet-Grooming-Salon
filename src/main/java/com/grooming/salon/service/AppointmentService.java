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

        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
    }

    public Appointment getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found."));
    }

    public void rescheduleAppointment(UUID appointmentId, AppointmentDto dto, UUID userId) {
        Appointment appt = getAppointmentById(appointmentId);

        if (!appt.getPet().getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to edit this appointment.");
        }

        appt.setAppointmentDate(dto.getAppointmentDate());
        appointmentRepository.save(appt);
    }

    public void updateAppointmentStatus(UUID appointmentId, String newStatus) {
        Appointment appt = getAppointmentById(appointmentId);
        String currentStatus = appt.getStatus();

        if (currentStatus.equals("COMPLETE") || currentStatus.equals("CANCELLED")) {
            throw new BusinessRuleException("Cannot alter an appointment that is already " + currentStatus);
        }

        if (!newStatus.equals("SCHEDULED") && !newStatus.equals("IN_PROGRESS") &&
                !newStatus.equals("COMPLETE") && !newStatus.equals("CANCELLED")) {
            throw new BusinessRuleException("Invalid status update requested.");
        }

        appt.setStatus(newStatus);
        appointmentRepository.save(appt);
    }
}