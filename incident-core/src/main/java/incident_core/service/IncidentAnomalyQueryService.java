package incident_core.service;

import incident_core.entity.IncidentAnomaly;
import incident_core.repository.IncidentAnomalyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentAnomalyQueryService {

    private final IncidentAnomalyRepository incidentAnomalyRepository;

    public IncidentAnomalyQueryService(
            IncidentAnomalyRepository incidentAnomalyRepository) {

        this.incidentAnomalyRepository = incidentAnomalyRepository;
    }

    public List<IncidentAnomaly> getAnomaliesForIncident(Long incidentId) {

        return incidentAnomalyRepository.findByIncidentId(incidentId);
    }
}

