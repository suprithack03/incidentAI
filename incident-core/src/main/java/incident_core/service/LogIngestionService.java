package incident_core.service;

import incident_core.dto.LogIngestRequest;
import incident_core.entity.LogEntry;
import incident_core.repository.LogEntryRepository;
import incident_core.repository.ServiceRepository;

import org.springframework.stereotype.Service;

@Service
public class LogIngestionService {

    private final ServiceRepository serviceRepository;
    private final LogEntryRepository logEntryRepository;

    public LogIngestionService(
            ServiceRepository serviceRepository,
            LogEntryRepository logEntryRepository) {

        this.serviceRepository = serviceRepository;
        this.logEntryRepository = logEntryRepository;
    }

    public LogEntry ingestLog(LogIngestRequest request) {

        incident_core.entity.Service service = serviceRepository
                .findByName(request.getService())
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        LogEntry logEntry = new LogEntry(
                service,
                request.getTimestamp(),
                request.getLevel(),
                request.getMessage(),
                request.getLatencyMs(),
                request.getStatusCode()
        );

        return logEntryRepository.save(logEntry);
    }
}