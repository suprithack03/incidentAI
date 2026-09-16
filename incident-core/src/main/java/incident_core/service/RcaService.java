package incident_core.service;

import incident_core.dto.RcaResponse;
import incident_core.entity.Anomaly;
import incident_core.entity.Incident;
import incident_core.entity.IncidentAnomaly;
import incident_core.repository.IncidentAnomalyRepository;
import incident_core.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RcaService {

    private final IncidentRepository incidentRepository;
    private final IncidentAnomalyRepository incidentAnomalyRepository;
    private final GeminiService geminiService;

    public RcaService(
            IncidentRepository incidentRepository,
            IncidentAnomalyRepository incidentAnomalyRepository,
            GeminiService geminiService) {

        this.incidentRepository = incidentRepository;
        this.incidentAnomalyRepository = incidentAnomalyRepository;
        this.geminiService = geminiService;
    }

    public RcaResponse generateRca(Long incidentId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Incident not found: " + incidentId
                        )
                );

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository.findByIncidentId(incidentId);

        String prompt = buildPrompt(incident, incidentAnomalies);

        String rca = geminiService.generateRca(prompt);

        return new RcaResponse(incidentId, rca);
    }

    private String buildPrompt(
            Incident incident,
            List<IncidentAnomaly> incidentAnomalies) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are the Root Cause Analysis engine for IncidentAI.

                Analyze the software incident using only the incident and
                anomaly information provided below.

                Do not invent logs, metrics, services, dependencies,
                infrastructure details, or causes that are not supported
                by the provided information.

                Provide the response in this structure:

                Root Cause:
                Explain the most likely root cause based on the available evidence.

                Evidence:
                List the incident and anomaly evidence that supports the conclusion.

                Impact:
                Explain the likely impact on the affected service.

                Recommended Actions:
                Provide practical actions that an engineer should investigate
                or take next.

                Incident:
                """);

        prompt.append("\nIncident ID: ")
                .append(incident.getId());

        prompt.append("\nTitle: ")
                .append(incident.getTitle());

        prompt.append("\nSeverity: ")
                .append(incident.getSeverity());

        prompt.append("\nStatus: ")
                .append(incident.getStatus());

        prompt.append("\nCreated At: ")
                .append(incident.getCreatedAt());

        if (incidentAnomalies.isEmpty()) {

            prompt.append("""
                    
                    Anomalies:
                    No anomalies are currently linked to this incident.
                    """);

        } else {

            prompt.append("\n\nAnomalies:\n");

            for (IncidentAnomaly incidentAnomaly : incidentAnomalies) {

                Anomaly anomaly = incidentAnomaly.getAnomaly();

                prompt.append("\n- Anomaly ID: ")
                        .append(anomaly.getId());

                prompt.append("\n  Service: ")
                        .append(anomaly.getService().getName());

                prompt.append("\n  Type: ")
                        .append(anomaly.getType());

                prompt.append("\n  Detected At: ")
                        .append(anomaly.getDetectedAt());

                prompt.append("\n  Metric Value: ")
                        .append(anomaly.getMetricValue());

                prompt.append("\n  Threshold Used: ")
                        .append(anomaly.getThresholdUsed());

                prompt.append("\n");
            }
        }

        return prompt.toString();
    }
}

