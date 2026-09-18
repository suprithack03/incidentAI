package incident_core.service;

import incident_core.entity.RunbookRagDocument;
import incident_core.repository.RunbookRagDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunbookRetrievalService {

    private final RunbookRagDocumentRepository repository;
    private final EmbeddingService embeddingService;

    public RunbookRetrievalService(
            RunbookRagDocumentRepository repository,
            EmbeddingService embeddingService) {

        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    public List<RunbookRagDocument> findSimilarRunbooks(
            String incidentContext,
            int limit) {

        List<Double> embedding =
                embeddingService.generateEmbedding(incidentContext);

        String vector =
                embedding.toString();

        return repository.findSimilarRunbooks(
                vector,
                limit
        );
    }
}