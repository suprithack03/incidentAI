package incident_core.service;

import incident_core.entity.AnomalyType;
import incident_core.entity.ServiceStatus;
import incident_core.repository.ServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceStatusService {

    private final ServiceRepository serviceRepository;

    public ServiceStatusService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public void updateStatus(
            incident_core.entity.Service service,
            AnomalyType anomalyType) {

        if (anomalyType == AnomalyType.DB_FAILURE
                || anomalyType == AnomalyType.REDIS_FAILURE) {

            service.setStatus(ServiceStatus.DOWN);

        } else if (anomalyType == AnomalyType.SLOW_RESPONSE
                || anomalyType == AnomalyType.ELEVATED_5XX) {

            service.setStatus(ServiceStatus.DEGRADED);
        }

        serviceRepository.save(service);
    }
}