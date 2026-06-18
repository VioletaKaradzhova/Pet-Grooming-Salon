package com.grooming.salon.repository;

import com.grooming.salon.model.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, UUID> {
}