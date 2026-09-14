package incident_core.severity;

import incident_core.entity.Anomaly;
import incident_core.entity.IncidentSeverity;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IncidentSeverityService {

    private final CoreServiceConfig coreServiceConfig;

    public IncidentSeverityService(
            CoreServiceConfig coreServiceConfig) {

        this.coreServiceConfig = coreServiceConfig;
    }

    public IncidentSeverity calculateSeverity(
            List<Anomaly> anomalies) {

        if (anomalies == null || anomalies.isEmpty()) {
            throw new IllegalArgumentException(
                    "An incident must contain at least one anomaly");
        }

        long anomalyCount = anomalies.size();

        Set<String> affectedServices =
                anomalies.stream()
                        .map(anomaly ->
                                anomaly.getService().getName())
                        .collect(Collectors.toSet());

        boolean coreServiceAffected =
                affectedServices.stream()
                        .anyMatch(coreServiceConfig::isCoreService);

        long downstreamServiceCount =
                affectedServices.stream()
                        .filter(service ->
                                !coreServiceConfig.isCoreService(service))
                        .count();

        if (coreServiceAffected) {

            if (downstreamServiceCount >= 2) {
                return IncidentSeverity.CRITICAL;
            }

            return IncidentSeverity.HIGH;
        }

        if (anomalyCount >= 2) {
            return IncidentSeverity.MEDIUM;
        }

        return IncidentSeverity.LOW;
    }
}