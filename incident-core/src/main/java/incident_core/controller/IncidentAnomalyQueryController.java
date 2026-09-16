package incident_core.controller;

import incident_core.entity.IncidentAnomaly;
import incident_core.service.IncidentAnomalyQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentAnomalyQueryController {

    private final IncidentAnomalyQueryService incidentAnomalyQueryService;

    public IncidentAnomalyQueryController(
            IncidentAnomalyQueryService incidentAnomalyQueryService) {

        this.incidentAnomalyQueryService = incidentAnomalyQueryService;
    }

    @GetMapping("/{incidentId}/anomalies")
    public List<IncidentAnomaly> getIncidentAnomalies(
            @PathVariable Long incidentId) {

        return incidentAnomalyQueryService
                .getAnomaliesForIncident(incidentId);
    }
}

