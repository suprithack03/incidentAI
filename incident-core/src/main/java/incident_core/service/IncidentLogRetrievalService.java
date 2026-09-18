package incident_core.service;

import incident_core.entity.Incident;
import incident_core.entity.IncidentAnomaly;
import incident_core.entity.LogEntry;
import incident_core.repository.IncidentAnomalyRepository;
import incident_core.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class IncidentLogRetrievalService {

    private final IncidentAnomalyRepository incidentAnomalyRepository;
    private final LogEntryRepository logEntryRepository;

    public IncidentLogRetrievalService(
            IncidentAnomalyRepository incidentAnomalyRepository,
            LogEntryRepository logEntryRepository) {

        this.incidentAnomalyRepository =
                incidentAnomalyRepository;

        this.logEntryRepository =
                logEntryRepository;
    }

    public List<LogEntry> findIncidentLogs(
            Incident incident) {

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository
                        .findByIncidentId(incident.getId());

        Set<Long> serviceIds =
                new LinkedHashSet<>();

        for (IncidentAnomaly incidentAnomaly :
                incidentAnomalies) {

            serviceIds.add(
                    incidentAnomaly
                            .getAnomaly()
                            .getService()
                            .getId()
            );
        }

        if (serviceIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime start =
                incident.getCreatedAt();

        LocalDateTime end =
                incident.getResolvedAt() != null
                        ? incident.getResolvedAt()
                        : LocalDateTime.now();

        List<LogEntry> logs = new java.util.ArrayList<>();

        for (Long serviceId : serviceIds) {

            logs.addAll(
                    logEntryRepository
                            .findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
                                    serviceId,
                                    start,
                                    end
                            )
            );
        }

        logs.sort(
                java.util.Comparator.comparing(
                        LogEntry::getTimestamp
                )
        );

        return logs;
    }
}