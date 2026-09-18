package incident_core.service;

import incident_core.entity.Incident;
import incident_core.entity.IncidentRagDocument;
import incident_core.entity.LogEntry;
import incident_core.entity.RunbookRagDocument;
import incident_core.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagContextService {

    private final IncidentRepository incidentRepository;
    private final IncidentLogRetrievalService incidentLogRetrievalService;
    private final HistoricalIncidentRetrievalService historicalIncidentRetrievalService;
    private final RunbookRetrievalService runbookRetrievalService;

    public RagContextService(
            IncidentRepository incidentRepository,
            IncidentLogRetrievalService incidentLogRetrievalService,
            HistoricalIncidentRetrievalService historicalIncidentRetrievalService,
            RunbookRetrievalService runbookRetrievalService) {

        this.incidentRepository =
                incidentRepository;

        this.incidentLogRetrievalService =
                incidentLogRetrievalService;

        this.historicalIncidentRetrievalService =
                historicalIncidentRetrievalService;

        this.runbookRetrievalService =
                runbookRetrievalService;
    }

    public String buildContext(
            Long incidentId,
            String incidentContext) {

        Incident incident =
                incidentRepository.findById(incidentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Incident not found: " + incidentId
                                )
                        );

        List<LogEntry> currentLogs =
                incidentLogRetrievalService
                        .findIncidentLogs(incident);

        List<IncidentRagDocument> similarIncidents =
                historicalIncidentRetrievalService
                        .findSimilarIncidents(incidentContext, 3);

        List<RunbookRagDocument> similarRunbooks =
                runbookRetrievalService
                        .findSimilarRunbooks(incidentContext, 3);

        StringBuilder context =
                new StringBuilder();

        context.append(
                "CURRENT INCIDENT LOGS:\n\n"
        );

        if (currentLogs.isEmpty()) {

            context.append(
                    "No logs were found for the current incident.\n"
            );

        } else {

            for (LogEntry log : currentLogs) {

                context.append("Timestamp: ")
                        .append(log.getTimestamp())
                        .append("\n");

                context.append("Service: ")
                        .append(log.getService().getName())
                        .append("\n");

                context.append("Level: ")
                        .append(log.getLevel())
                        .append("\n");

                context.append("Message: ")
                        .append(log.getMessage())
                        .append("\n");

                context.append("Latency: ")
                        .append(log.getLatencyMs())
                        .append(" ms\n");

                context.append("Status Code: ")
                        .append(log.getStatusCode())
                        .append("\n\n");
            }
        }

        context.append(
                "RETRIEVED HISTORICAL INCIDENTS:\n\n"
        );

        if (similarIncidents.isEmpty()) {

            context.append(
                    "No similar historical incidents were found.\n"
            );

        } else {

            for (IncidentRagDocument document :
                    similarIncidents) {

                context.append("Historical Incident ID: ")
                        .append(document.getIncidentId())
                        .append("\n");

                context.append(document.getContent())
                        .append("\n\n");
            }
        }

        context.append(
                "RETRIEVED RUNBOOKS:\n\n"
        );

        if (similarRunbooks.isEmpty()) {

            context.append(
                    "No relevant runbooks were found.\n"
            );

        } else {

            for (RunbookRagDocument document :
                    similarRunbooks) {

                context.append("Runbook: ")
                        .append(document.getTitle())
                        .append("\n");

                context.append(document.getContent())
                        .append("\n\n");
            }
        }

        return context.toString();
    }
}