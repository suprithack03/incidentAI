package incident_core.controller;

import incident_core.entity.Service;
import incident_core.repository.ServiceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceQueryController {

    private final ServiceRepository serviceRepository;

    public ServiceQueryController(
            ServiceRepository serviceRepository) {

        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}