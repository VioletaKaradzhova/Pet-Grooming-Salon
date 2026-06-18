package com.grooming.salon.repository;

import com.grooming.salon.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findAllByPetId(UUID petId);
}