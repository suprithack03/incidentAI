package user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import user_service.dto.IncidentLogRequest;
import user_service.service.IncidentLogReporter;
import user_service.service.StructuredLogService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/failures")
public class FailureInjectionController {

    private final StructuredLogService structuredLogService;
    private final IncidentLogReporter incidentLogReporter;

    public FailureInjectionController(
            StructuredLogService structuredLogService,
            IncidentLogReporter incidentLogReporter) {

        this.structuredLogService = structuredLogService;
        this.incidentLogReporter = incidentLogReporter;
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, String>> triggerDatabaseFailure() {

        String message = "database connection failed";

        LocalDateTime timestamp = LocalDateTime.now();

        Integer latencyMs = null;

        Integer statusCode = 500;

        structuredLogService.log(
                "ERROR",
                message,
                latencyMs,
                statusCode
        );

        IncidentLogRequest logRequest =
                new IncidentLogRequest(
                        "user-service",
                        timestamp,
                        "ERROR",
                        message,
                        latencyMs,
                        statusCode
                );

        incidentLogReporter.report(logRequest);

        throw new RuntimeException(message);
    }
}