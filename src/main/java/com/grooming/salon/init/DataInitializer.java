package com.grooming.salon.init;

import com.grooming.salon.model.entity.ServicePackage;
import com.grooming.salon.repository.ServicePackageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ServicePackageRepository servicePackageRepository;

    public DataInitializer(ServicePackageRepository servicePackageRepository) {
        this.servicePackageRepository = servicePackageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (servicePackageRepository.count() == 0) {
            ServicePackage s1 = new ServicePackage(); s1.setName("Bath & Brush"); s1.setPrice(35.0);
            ServicePackage s2 = new ServicePackage(); s2.setName("Full Grooming Spa"); s2.setPrice(75.0);
            ServicePackage s3 = new ServicePackage(); s3.setName("Nail Trim & File"); s3.setPrice(15.0);
            ServicePackage s4 = new ServicePackage(); s4.setName("Flea & Tick Treatment"); s4.setPrice(25.0);
            ServicePackage s5 = new ServicePackage(); s5.setName("Puppy's First Trim"); s5.setPrice(40.0);

            servicePackageRepository.saveAll(List.of(s1, s2, s3, s4, s5));
            System.out.println("Initialized 5 default grooming services in the database!");
        }
    }
}