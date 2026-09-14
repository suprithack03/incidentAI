package incident_core.config;

import incident_core.entity.Service;
import incident_core.entity.ServiceStatus;
import incident_core.repository.ServiceRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceInitializer {

    @Bean
    CommandLineRunner initializeServices(ServiceRepository serviceRepository) {

        return args -> {

            createServiceIfMissing(
                    serviceRepository,
                    "user-service"
            );

            createServiceIfMissing(
                    serviceRepository,
                    "payment-service"
            );

            createServiceIfMissing(
                    serviceRepository,
                    "inventory-service"
            );
        };
    }

    private void createServiceIfMissing(
            ServiceRepository serviceRepository,
            String serviceName) {

        if (serviceRepository.findByName(serviceName).isEmpty()) {

            Service service = new Service(
                    serviceName,
                    ServiceStatus.HEALTHY
            );

            serviceRepository.save(service);
        }
    }
}