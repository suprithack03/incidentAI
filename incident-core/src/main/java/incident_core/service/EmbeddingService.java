package incident_core.service;

import incident_core.config.GeminiConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final GeminiConfig geminiConfig;
    private final RestClient restClient;

    public EmbeddingService(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
        this.restClient = RestClient.builder().build();
    }

    public List<Double> generateEmbedding(String text) {

        Map<String, Object> part = new HashMap<>();
        part.put("text", text);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(
                "model",
                "models/gemini-embedding-001"
        );
        requestBody.put("content", content);
        requestBody.put(
                "output_dimensionality",
                768
        );

        Map<?, ?> response = restClient
                .post()
                .uri(
                        geminiConfig.getApiUrl()
                                + "/v1beta/models/gemini-embedding-001:embedContent"
                )
                .header(
                        "x-goog-api-key",
                        geminiConfig.getApiKey()
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty embedding response"
            );
        }

        Object embeddingObject = response.get("embedding");

        if (!(embeddingObject instanceof Map<?, ?> embeddingMap)) {
            throw new IllegalStateException(
                    "Gemini response did not contain an embedding"
            );
        }

        Object valuesObject = embeddingMap.get("values");

        if (!(valuesObject instanceof List<?> values)) {
            throw new IllegalStateException(
                    "Gemini response did not contain embedding values"
            );
        }

        return values.stream()
                .map(value -> ((Number) value).doubleValue())
                .toList();
    }
}