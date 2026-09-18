package incident_core.service;

import incident_core.repository.RunbookRagDocumentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RunbookRagService {

    private final EmbeddingService embeddingService;
    private final RunbookRagDocumentRepository repository;

    public RunbookRagService(
            EmbeddingService embeddingService,
            RunbookRagDocumentRepository repository) {

        this.embeddingService = embeddingService;
        this.repository = repository;
    }

    @Transactional
    public void loadRunbooks() throws IOException {

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources(
                        "classpath:/runbooks/*.md"
                );

        for (Resource resource : resources) {

            String content = resource.getContentAsString(
                    StandardCharsets.UTF_8
            );

            String title = resource.getFilename();

            if (title == null) {
                continue;
            }

            List<Double> embedding =
                    embeddingService.generateEmbedding(content);

            String vector =
                    convertToVectorString(embedding);

            repository.insertWithEmbedding(
                    title,
                    content,
                    vector,
                    LocalDateTime.now()
            );
        }
    }

    private String convertToVectorString(
            List<Double> embedding) {

        return embedding.toString();
    }
}