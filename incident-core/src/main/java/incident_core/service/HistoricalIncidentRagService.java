package incident_core.service;

import incident_core.entity.Anomaly;
import incident_core.entity.Incident;
import incident_core.entity.IncidentAnomaly;
import incident_core.repository.IncidentAnomalyRepository;
import incident_core.repository.IncidentRagDocumentRepository;
import incident_core.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricalIncidentRagService {

    private final IncidentRepository incidentRepository;
    private final IncidentAnomalyRepository incidentAnomalyRepository;
    private final IncidentRagDocumentRepository repository;
    private final EmbeddingService embeddingService;

    public HistoricalIncidentRagService(
            IncidentRepository incidentRepository,
            IncidentAnomalyRepository incidentAnomalyRepository,
            IncidentRagDocumentRepository repository,
            EmbeddingService embeddingService) {

        this.incidentRepository = incidentRepository;
        this.incidentAnomalyRepository = incidentAnomalyRepository;
        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public int loadHistoricalIncidents() {

        List<Incident> incidents =
                incidentRepository.findAll();

        int loadedCount = 0;

        for (Incident incident : incidents) {

            if (repository.existsByIncidentId(incident.getId())) {
                continue;
            }

            List<IncidentAnomaly> incidentAnomalies =
                    incidentAnomalyRepository.findByIncidentId(
                            incident.getId()
                    );

            String content =
                    buildHistoricalDocument(
                            incident,
                            incidentAnomalies
                    );

            List<Double> embedding =
                    embeddingService.generateEmbedding(content);

            String vector =
                    convertToVectorString(embedding);

            repository.insertWithEmbedding(
                    incident.getId(),
                    content,
                    vector,
                    LocalDateTime.now()
            );

            loadedCount++;
        }

        return loadedCount;
    }

    private String buildHistoricalDocument(
            Incident incident,
            List<IncidentAnomaly> incidentAnomalies) {

        StringBuilder document = new StringBuilder();

        document.append("Historical Incident\n\n");

        document.append("Incident ID: ")
                .append(incident.getId())
                .append("\n");

        document.append("Title: ")
                .append(incident.getTitle())
                .append("\n");

        document.append("Severity: ")
                .append(incident.getSeverity())
                .append("\n");

        document.append("Status: ")
                .append(incident.getStatus())
                .append("\n");

        document.append("Created At: ")
                .append(incident.getCreatedAt())
                .append("\n");

        document.append("Resolved At: ")
                .append(incident.getResolvedAt())
                .append("\n");

        document.append("\nAffected Services and Anomalies:\n");

        if (incidentAnomalies.isEmpty()) {

            document.append(
                    "No anomalies were linked to this incident.\n"
            );

        } else {

            for (IncidentAnomaly incidentAnomaly :
                    incidentAnomalies) {

                Anomaly anomaly =
                        incidentAnomaly.getAnomaly();

                document.append("\n");

                document.append("Anomaly ID: ")
                        .append(anomaly.getId())
                        .append("\n");

                document.append("Service: ")
                        .append(anomaly.getService().getName())
                        .append("\n");

                document.append("Type: ")
                        .append(anomaly.getType())
                        .append("\n");

                document.append("Detected At: ")
                        .append(anomaly.getDetectedAt())
                        .append("\n");

                document.append("Metric Value: ")
                        .append(anomaly.getMetricValue())
                        .append("\n");

                document.append("Threshold Used: ")
                        .append(anomaly.getThresholdUsed())
                        .append("\n");
            }
        }

        document.append("\nHistorical RCA:\n");
        document.append(
                "No previously stored automated root cause analysis is available for this incident."
        );

        return document.toString();
    }

    private String convertToVectorString(
            List<Double> embedding) {

        return embedding.toString();
    }
}