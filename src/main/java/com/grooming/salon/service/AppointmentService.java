package com.grooming.salon.service;
import com.grooming.salon.exception.BusinessRuleException;
import com.grooming.salon.model.entity.Appointment;
import com.grooming.salon.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void cancelAppointment(UUID appointmentId, UUID userId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Appointment not found."));

        if (!appt.getPet().getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("Not authorized.");
        }

        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
    }
}