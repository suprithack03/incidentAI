package incident_core.service;

import incident_core.entity.Incident;
import incident_core.entity.IncidentStatus;
import incident_core.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentQueryService {

    private final IncidentRepository incidentRepository;

    public IncidentQueryService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public List<Incident> getActiveIncidents() {
        return incidentRepository.findByStatusIn(
                List.of(
                        IncidentStatus.OPEN,
                        IncidentStatus.INVESTIGATING
                )
        );
    }
}