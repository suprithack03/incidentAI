package incident_core.service;

import incident_core.config.GeminiConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestClient restClient;

    public GeminiService(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
        this.restClient = RestClient.builder().build();
    }

    public String generateRca(String prompt) {

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "model",
                geminiConfig.getModel()
        );

        requestBody.put(
                "input",
                prompt
        );

        Map<?, ?> response;

        try {

            response =
                    restClient
                            .post()
                            .uri(
                                    geminiConfig.getApiUrl()
                                            + "/v1beta/interactions"
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

        } catch (HttpStatusCodeException exception) {

            throw new IllegalStateException(
                    "Gemini API request failed with HTTP "
                            + exception.getStatusCode().value()
                            + ": "
                            + exception.getResponseBodyAsString(),
                    exception
            );
        }

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response"
            );
        }

        Object stepsObject =
                response.get("steps");

        if (!(stepsObject instanceof List<?> steps)
                || steps.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini response did not contain any output steps"
            );
        }

        for (Object stepObject : steps) {

            if (!(stepObject instanceof Map<?, ?> step)) {
                continue;
            }

            Object typeObject =
                    step.get("type");

            if (!"model_output".equals(typeObject)) {
                continue;
            }

            Object contentObject =
                    step.get("content");

            if (!(contentObject instanceof List<?> contentList)) {
                continue;
            }

            for (Object contentObjectItem : contentList) {

                if (!(contentObjectItem instanceof Map<?, ?> contentItem)) {
                    continue;
                }

                Object textObject =
                        contentItem.get("text");

                if (textObject instanceof String text
                        && !text.isBlank()) {

                    return text;
                }
            }
        }

        throw new IllegalStateException(
                "Gemini response did not contain text output"
        );
    }
}