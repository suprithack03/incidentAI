package incident_core.controller;

import incident_core.dto.RcaResponse;
import incident_core.service.RcaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class RcaController {

    private final RcaService rcaService;

    public RcaController(RcaService rcaService) {
        this.rcaService = rcaService;
    }

    @GetMapping("/{incidentId}/rca")
    public RcaResponse generateRca(
            @PathVariable Long incidentId) {

        return rcaService.generateRca(incidentId);
    }
}

