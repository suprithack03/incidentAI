package incident_core.controller;

import incident_core.dto.LogIngestRequest;
import incident_core.entity.LogEntry;
import incident_core.service.LogIngestionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LogIngestionController {

    private final LogIngestionService logIngestionService;

    public LogIngestionController(
            LogIngestionService logIngestionService) {

        this.logIngestionService = logIngestionService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<LogEntry> ingestLog(
            @RequestBody LogIngestRequest request) {

        LogEntry logEntry =
                logIngestionService.ingestLog(request);

        return ResponseEntity.ok(logEntry);
    }
}