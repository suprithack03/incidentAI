package incident_core.controller;

import incident_core.entity.Incident;
import incident_core.entity.IncidentStatus;
import incident_core.service.IncidentStatusService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentStatusController {

    private final IncidentStatusService incidentStatusService;

    public IncidentStatusController(
            IncidentStatusService incidentStatusService) {

        this.incidentStatusService = incidentStatusService;
    }

    @PutMapping("/{incidentId}/status")
    public Incident updateStatus(
            @PathVariable Long incidentId,
            @RequestParam IncidentStatus status) {

        return incidentStatusService.updateStatus(
                incidentId,
                status
        );
    }
}