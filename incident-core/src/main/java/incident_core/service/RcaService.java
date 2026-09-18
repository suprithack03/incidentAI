package incident_core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import incident_core.dto.RcaResponse;
import incident_core.dto.StructuredRca;
import incident_core.entity.Anomaly;
import incident_core.entity.Incident;
import incident_core.entity.IncidentAnomaly;
import incident_core.entity.RootCauseAnalysis;
import incident_core.repository.IncidentAnomalyRepository;
import incident_core.repository.IncidentRepository;
import incident_core.repository.RootCauseAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RcaService {

    private final IncidentRepository incidentRepository;
    private final IncidentAnomalyRepository incidentAnomalyRepository;
    private final RootCauseAnalysisRepository rootCauseAnalysisRepository;
    private final GeminiService geminiService;
    private final RagContextService ragContextService;
    private final ObjectMapper objectMapper;

    public RcaService(
            IncidentRepository incidentRepository,
            IncidentAnomalyRepository incidentAnomalyRepository,
            RootCauseAnalysisRepository rootCauseAnalysisRepository,
            GeminiService geminiService,
            RagContextService ragContextService,
            ObjectMapper objectMapper) {

        this.incidentRepository = incidentRepository;
        this.incidentAnomalyRepository = incidentAnomalyRepository;
        this.rootCauseAnalysisRepository = rootCauseAnalysisRepository;
        this.geminiService = geminiService;
        this.ragContextService = ragContextService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RcaResponse generateRca(Long incidentId) {

        Incident incident =
                incidentRepository.findById(incidentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Incident not found: " + incidentId
                                )
                        );

        List<IncidentAnomaly> incidentAnomalies =
                incidentAnomalyRepository.findByIncidentId(incidentId);

        String incidentContext =
                buildIncidentContext(
                        incident,
                        incidentAnomalies
                );

        String ragContext =
                ragContextService.buildContext(
                        incidentId,
                        incidentContext
                );

        String prompt =
                buildPrompt(
                        incident,
                        incidentAnomalies,
                        ragContext
                );

        StructuredRca structuredRca;

        try {

            String geminiResponse =
                    geminiService.generateRca(prompt);

            structuredRca =
                    parseAndValidate(geminiResponse);

        } catch (Exception exception) {

            structuredRca =
                    buildGeminiFailureFallback();
        }

        if (structuredRca == null) {

            String retryPrompt =
                    buildStrictRetryPrompt(prompt);

            try {

                String retryResponse =
                        geminiService.generateRca(
                                retryPrompt
                        );

                structuredRca =
                        parseAndValidate(
                                retryResponse
                        );

            } catch (Exception exception) {

                structuredRca =
                        buildGeminiFailureFallback();
            }
        }

        if (structuredRca == null) {

            structuredRca =
                    buildFallbackRca();
        }

        saveRca(
                incidentId,
                structuredRca
        );

        return new RcaResponse(
                incidentId,
                structuredRca
        );
    }

    private void saveRca(
            Long incidentId,
            StructuredRca structuredRca) {

        RootCauseAnalysis rca =
                rootCauseAnalysisRepository
                        .findByIncidentId(incidentId)
                        .orElseGet(RootCauseAnalysis::new);

        rca.setIncidentId(incidentId);

        rca.setProbableCause(
                structuredRca.getProbableCause()
        );

        rca.setSupportingEvidence(
                structuredRca.getSupportingEvidence() == null
                        ? null
                        : String.join(
                                "\n",
                                structuredRca.getSupportingEvidence()
                        )
        );

        rca.setAffectedService(
                structuredRca.getAffectedService()
        );

        rca.setConfidence(
                structuredRca.getConfidence()
        );

        rca.setRecommendedRemediation(
                structuredRca.getRecommendedRemediation()
        );

        if (rca.getCreatedAt() == null) {
            rca.setCreatedAt(
                    LocalDateTime.now()
            );
        }

        rootCauseAnalysisRepository.save(rca);
    }

    private StructuredRca parseAndValidate(
            String geminiResponse) {

        try {

            StructuredRca rca =
                    objectMapper.readValue(
                            geminiResponse,
                            StructuredRca.class
                    );

            if (!isValid(rca)) {
                return null;
            }

            return rca;

        } catch (Exception exception) {

            return null;
        }
    }

    private boolean isValid(
            StructuredRca rca) {

        if (rca == null) {
            return false;
        }

        if (isBlank(rca.getProbableCause())) {
            return false;
        }

        if (rca.getSupportingEvidence() == null
                || rca.getSupportingEvidence().isEmpty()) {
            return false;
        }

        if (rca.getSupportingEvidence().stream()
                .anyMatch(this::isBlank)) {
            return false;
        }

        if (isBlank(rca.getAffectedService())) {
            return false;
        }

        if (isBlank(rca.getConfidence())) {
            return false;
        }

        if (!isValidConfidence(rca.getConfidence())) {
            return false;
        }

        if (isBlank(rca.getRecommendedRemediation())) {
            return false;
        }

        return true;
    }

    private boolean isValidConfidence(
            String confidence) {

        return "LOW".equalsIgnoreCase(confidence)
                || "MEDIUM".equalsIgnoreCase(confidence)
                || "HIGH".equalsIgnoreCase(confidence);
    }

    private boolean isBlank(String value) {

        return value == null
                || value.isBlank();
    }

    private StructuredRca buildFallbackRca() {

        StructuredRca fallback =
                new StructuredRca();

        fallback.setProbableCause(
                "insufficient data for automated analysis"
        );

        fallback.setSupportingEvidence(
                List.of(
                        "Gemini returned invalid RCA data after retry."
                )
        );

        fallback.setAffectedService(
                "Unknown"
        );

        fallback.setConfidence(
                "LOW"
        );

        fallback.setRecommendedRemediation(
                "Review the incident logs, anomalies, and affected service manually."
        );

        return fallback;
    }

    private StructuredRca buildGeminiFailureFallback() {

        StructuredRca fallback =
                new StructuredRca();

        fallback.setProbableCause(
                "insufficient data for automated analysis"
        );

        fallback.setSupportingEvidence(
                List.of(
                        "Gemini RCA generation was temporarily unavailable."
                )
        );

        fallback.setAffectedService(
                "Unknown"
        );

        fallback.setConfidence(
                "LOW"
        );

        fallback.setRecommendedRemediation(
                "Review the incident logs, anomalies, and affected service manually."
        );

        return fallback;
    }

    private String buildStrictRetryPrompt(
            String originalPrompt) {

        return originalPrompt
                + """

                IMPORTANT RETRY INSTRUCTION:

                Your previous response could not be validated.

                Return ONLY a valid JSON object.

                The response MUST contain all five fields:

                probable_cause
                supporting_evidence
                affected_service
                confidence
                recommended_remediation

                All fields must be non-empty.

                supporting_evidence must be a JSON array
                containing at least one non-empty string.

                confidence MUST be exactly one of:
                LOW
                MEDIUM
                HIGH

                Do not use Markdown.
                Do not use code fences.
                Do not write any text before or after the JSON.
                """;
    }

    private String buildIncidentContext(
            Incident incident,
            List<IncidentAnomaly> incidentAnomalies) {

        StringBuilder context =
                new StringBuilder();

        context.append("Incident ID: ")
                .append(incident.getId());

        context.append("\nTitle: ")
                .append(incident.getTitle());

        context.append("\nSeverity: ")
                .append(incident.getSeverity());

        context.append("\nStatus: ")
                .append(incident.getStatus());

        context.append("\nCreated At: ")
                .append(incident.getCreatedAt());

        if (incident.getResolvedAt() != null) {

            context.append("\nResolved At: ")
                    .append(incident.getResolvedAt());
        }

        if (incidentAnomalies.isEmpty()) {

            context.append(
                    "\nNo anomalies are currently linked to this incident."
            );

        } else {

            context.append("\nAnomalies:\n");

            for (IncidentAnomaly incidentAnomaly :
                    incidentAnomalies) {

                Anomaly anomaly =
                        incidentAnomaly.getAnomaly();

                context.append("\n- Anomaly ID: ")
                        .append(anomaly.getId());

                context.append("\n  Service: ")
                        .append(anomaly.getService().getName());

                context.append("\n  Type: ")
                        .append(anomaly.getType());

                context.append("\n  Detected At: ")
                        .append(anomaly.getDetectedAt());

                context.append("\n  Metric Value: ")
                        .append(anomaly.getMetricValue());

                context.append("\n  Threshold Used: ")
                        .append(anomaly.getThresholdUsed());

                context.append("\n");
            }
        }

        return context.toString();
    }

    private String buildPrompt(
            Incident incident,
            List<IncidentAnomaly> incidentAnomalies,
            String ragContext) {

        StringBuilder prompt =
                new StringBuilder();

        prompt.append("""
                You are the Root Cause Analysis engine for IncidentAI.

                Analyze the software incident using ONLY the information
                provided in the incident details and retrieved RAG context.

                The retrieved RAG context contains:
                1. Logs from the current incident.
                2. Similar historical incidents.
                3. Operational runbooks.

                Treat retrieved information as evidence.
                Do not invent logs, metrics, services, dependencies,
                infrastructure details, or causes that are not supported
                by the provided information.

                Return ONLY valid JSON.

                The JSON must contain exactly these fields:

                {
                  "probable_cause": "string",
                  "supporting_evidence": ["string"],
                  "affected_service": "string",
                  "confidence": "LOW | MEDIUM | HIGH",
                  "recommended_remediation": "string"
                }

                Do not include Markdown.
                Do not include ```json.
                Do not include explanations outside the JSON.

                CURRENT INCIDENT:
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

            for (IncidentAnomaly incidentAnomaly :
                    incidentAnomalies) {

                Anomaly anomaly =
                        incidentAnomaly.getAnomaly();

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

        prompt.append("""

                

                RETRIEVED RAG CONTEXT:

                """);

        prompt.append(ragContext);

        prompt.append("""

                

                FINAL INSTRUCTION:

                Use the current incident information and retrieved RAG
                context to determine the most probable root cause.

                Supporting evidence must refer only to information
                contained in the supplied incident data or RAG context.

                If the evidence is insufficient, use LOW confidence.
                """);

        return prompt.toString();
    }
}