package com.grooming.salon.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentDto {

    @NotNull(message = "Please select a valid date and time.")
    @Future(message = "Appointment date must be in the future.")
    private LocalDateTime appointmentDate;

    @NotNull(message = "Please select a pet.")
    private UUID petId;

    @NotNull(message = "Please select a service package.")
    private UUID servicePackageId;

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public UUID getPetId() {
        return petId;
    }

    public void setPetId(UUID petId) {
        this.petId = petId;
    }

    public UUID getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(UUID servicePackageId) {
        this.servicePackageId = servicePackageId;
    }
}