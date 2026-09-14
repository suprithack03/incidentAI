package incident_core.controller;

import incident_core.entity.Anomaly;
import incident_core.entity.Incident;
import incident_core.grouping.IncidentGroupingService;
import incident_core.repository.AnomalyRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents/group")
public class IncidentGroupingController {

    private final AnomalyRepository anomalyRepository;
    private final IncidentGroupingService incidentGroupingService;

    public IncidentGroupingController(
            AnomalyRepository anomalyRepository,
            IncidentGroupingService incidentGroupingService) {

        this.anomalyRepository = anomalyRepository;
        this.incidentGroupingService = incidentGroupingService;
    }

    @PostMapping("/{anomalyId}")
    public ResponseEntity<Incident> groupAnomaly(
            @PathVariable Long anomalyId) {

        Anomaly anomaly = anomalyRepository
                .findById(anomalyId)
                .orElseThrow(() ->
                        new RuntimeException("Anomaly not found"));

        Incident incident =
                incidentGroupingService.groupAnomaly(anomaly);

        return ResponseEntity.ok(incident);
    }
}