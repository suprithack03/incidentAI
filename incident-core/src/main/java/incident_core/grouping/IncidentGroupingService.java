package incident_core.grouping;

import incident_core.entity.Anomaly;
import incident_core.entity.Incident;
import incident_core.entity.IncidentAnomaly;
import incident_core.entity.IncidentStatus;
import incident_core.repository.IncidentAnomalyRepository;
import incident_core.repository.IncidentRepository;
import incident_core.severity.IncidentSeverityService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentGroupingService {

    private final IncidentRepository incidentRepository;
    private final IncidentAnomalyRepository incidentAnomalyRepository;
    private final ServiceDependencyMap serviceDependencyMap;
    private final IncidentSeverityService incidentSeverityService;

    public IncidentGroupingService(
            IncidentRepository incidentRepository,
            IncidentAnomalyRepository incidentAnomalyRepository,
            ServiceDependencyMap serviceDependencyMap,
            IncidentSeverityService incidentSeverityService) {

        this.incidentRepository = incidentRepository;
        this.incidentAnomalyRepository = incidentAnomalyRepository;
        this.serviceDependencyMap = serviceDependencyMap;
        this.incidentSeverityService = incidentSeverityService;
    }

    public Incident groupAnomaly(Anomaly anomaly) {

        List<Incident> openIncidents =
                incidentRepository.findByStatusIn(
                        List.of(
                                IncidentStatus.OPEN,
                                IncidentStatus.INVESTIGATING
                        )
                );

        for (Incident incident : openIncidents) {

            if (isWithinGroupingWindow(anomaly, incident)
                    && isRelatedToIncident(anomaly, incident)) {

                attachAnomalyToIncident(anomaly, incident);
                updateIncidentSeverity(incident);

                return incident;
            }
        }

        return createNewIncident(anomaly);
    }

    private boolean isWithinGroupingWindow(
            Anomaly anomaly,
            Incident incident) {

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository.findByIncidentId(
                        incident.getId());

        if (incidentAnomalies.isEmpty()) {
            return false;
        }

        LocalDateTime latestAnomalyTime =
                incidentAnomalies.stream()
                        .map(incidentAnomaly ->
                                incidentAnomaly
                                        .getAnomaly()
                                        .getDetectedAt())
                        .max(LocalDateTime::compareTo)
                        .orElse(incident.getCreatedAt());

        LocalDateTime windowStart =
                latestAnomalyTime
                        .minus(IncidentGroupingConfig.GROUPING_WINDOW);

        LocalDateTime windowEnd =
                latestAnomalyTime
                        .plus(IncidentGroupingConfig.GROUPING_WINDOW);

        return !anomaly.getDetectedAt().isBefore(windowStart)
                && !anomaly.getDetectedAt().isAfter(windowEnd);
    }

    private boolean isRelatedToIncident(
            Anomaly anomaly,
            Incident incident) {

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository.findByIncidentId(
                        incident.getId());

        for (IncidentAnomaly incidentAnomaly :
                incidentAnomalies) {

            Anomaly existingAnomaly =
                    incidentAnomaly.getAnomaly();

            String existingService =
                    existingAnomaly.getService().getName();

            String newService =
                    anomaly.getService().getName();

            if (existingService.equals(newService)) {
                return true;
            }

            if (serviceDependencyMap.hasDependency(
                    existingService,
                    newService)) {

                return true;
            }

            if (serviceDependencyMap.hasDependency(
                    newService,
                    existingService)) {

                return true;
            }
        }

        return false;
    }

    private void attachAnomalyToIncident(
            Anomaly anomaly,
            Incident incident) {

        IncidentAnomaly incidentAnomaly =
                new IncidentAnomaly(
                        incident,
                        anomaly
                );

        incidentAnomalyRepository.save(incidentAnomaly);
    }

    private void updateIncidentSeverity(
            Incident incident) {

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository.findByIncidentId(
                        incident.getId());

        List<Anomaly> anomalies =
                incidentAnomalies.stream()
                        .map(IncidentAnomaly::getAnomaly)
                        .toList();

        incident.setSeverity(
                incidentSeverityService.calculateSeverity(
                        anomalies
                )
        );

        incidentRepository.save(incident);
    }

    private Incident createNewIncident(
            Anomaly anomaly) {

        String title =
                "Incident in " +
                        anomaly.getService().getName();

        Incident incident =
                new Incident(
                        title,
                        incidentSeverityService.calculateSeverity(
                                List.of(anomaly)
                        ),
                        IncidentStatus.OPEN,
                        anomaly.getDetectedAt(),
                        null
                );

        incident = incidentRepository.save(incident);

        IncidentAnomaly incidentAnomaly =
                new IncidentAnomaly(
                        incident,
                        anomaly
                );

        incidentAnomalyRepository.save(incidentAnomaly);

        return incident;
    }
}