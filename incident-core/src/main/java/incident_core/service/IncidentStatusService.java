package incident_core.service;

import incident_core.entity.Incident;
import incident_core.entity.IncidentStatus;
import incident_core.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IncidentStatusService {

    private final IncidentRepository incidentRepository;

    public IncidentStatusService(
            IncidentRepository incidentRepository) {

        this.incidentRepository = incidentRepository;
    }

    public Incident updateStatus(
            Long incidentId,
            IncidentStatus newStatus) {

        Incident incident = incidentRepository
                .findById(incidentId)
                .orElseThrow(() ->
                        new RuntimeException("Incident not found"));

        incident.setStatus(newStatus);

        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        } else {
            incident.setResolvedAt(null);
        }

        return incidentRepository.save(incident);
    }
}