package incident_core.controller;

import incident_core.entity.Anomaly;
import incident_core.service.AnomalyDetectionService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyDetectionController {

    private final AnomalyDetectionService anomalyDetectionService;

    public AnomalyDetectionController(
            AnomalyDetectionService anomalyDetectionService) {

        this.anomalyDetectionService = anomalyDetectionService;
    }

    @PostMapping("/detect/elevated-5xx")
    public Anomaly detectElevated5xx(
            @RequestParam String service,
            @RequestParam String endTime) {

        return anomalyDetectionService.detectElevated5xx(
                service,
                LocalDateTime.parse(endTime)
        );
    }

    @PostMapping("/detect/slow-response")
    public Anomaly detectSlowResponse(
            @RequestParam String service,
            @RequestParam String endTime) {

        return anomalyDetectionService.detectSlowResponse(
                service,
                LocalDateTime.parse(endTime)
        );
    }

    @PostMapping("/detect/db-failure")
    public Anomaly detectDbFailure(
            @RequestParam String service,
            @RequestParam String endTime) {

        return anomalyDetectionService.detectDbFailure(
                service,
                LocalDateTime.parse(endTime)
        );
    }

    @PostMapping("/detect/redis-failure")
    public Anomaly detectRedisFailure(
            @RequestParam String service,
            @RequestParam String endTime) {

        return anomalyDetectionService.detectRedisFailure(
                service,
                LocalDateTime.parse(endTime)
        );
    }
}