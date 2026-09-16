package incident_core.controller;

import incident_core.entity.Incident;
import incident_core.service.IncidentQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentQueryController {

    private final IncidentQueryService incidentQueryService;

    public IncidentQueryController(
            IncidentQueryService incidentQueryService) {
        this.incidentQueryService = incidentQueryService;
    }

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentQueryService.getAllIncidents();
    }

    @GetMapping("/active")
    public List<Incident> getActiveIncidents() {
        return incidentQueryService.getActiveIncidents();
    }
}