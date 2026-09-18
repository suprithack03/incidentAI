package incident_core.service;

import incident_core.entity.IncidentRagDocument;
import incident_core.repository.IncidentRagDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalIncidentRetrievalService {

    private final IncidentRagDocumentRepository repository;
    private final EmbeddingService embeddingService;

    public HistoricalIncidentRetrievalService(
            IncidentRagDocumentRepository repository,
            EmbeddingService embeddingService) {

        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    public List<IncidentRagDocument> findSimilarIncidents(
            String incidentContext,
            int limit) {

        List<Double> embedding =
                embeddingService.generateEmbedding(incidentContext);

        String vector =
                embedding.toString();

        return repository.findSimilarIncidents(
                vector,
                limit
        );
    }
}