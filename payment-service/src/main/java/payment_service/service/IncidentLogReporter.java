package payment_service.service;

import payment_service.dto.IncidentLogRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class IncidentLogReporter {

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();

    @Value("${incident.core.url}")
    private String incidentCoreUrl;

    @Value("${internal.service.token}")
    private String internalServiceToken;

    public void report(IncidentLogRequest logRequest) {

        CompletableFuture.runAsync(() -> {

            try {
                String requestBody = buildJson(logRequest);

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(
                                        incidentCoreUrl +
                                                "/api/logs/ingest"))
                                .header(
                                        "Content-Type",
                                        "application/json")
                                .header(
                                        "X-Internal-Token",
                                        internalServiceToken)
                                .POST(
                                        HttpRequest.BodyPublishers
                                                .ofString(requestBody))
                                .build();

                httpClient.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                ).exceptionally(exception -> {

                    System.err.println(
                            "Failed to report log to Incident Core: "
                                    + exception.getMessage()
                    );

                    return null;
                });

            } catch (Exception exception) {

                System.err.println(
                        "Failed to prepare log for Incident Core: "
                                + exception.getMessage()
                );
            }

        });
    }

    private String buildJson(IncidentLogRequest logRequest) {

        return "{"
                + "\"service\":\""
                + escape(logRequest.getService())
                + "\","
                + "\"timestamp\":\""
                + logRequest.getTimestamp()
                + "\","
                + "\"level\":\""
                + escape(logRequest.getLevel())
                + "\","
                + "\"message\":\""
                + escape(logRequest.getMessage())
                + "\","
                + "\"latencyMs\":"
                + valueOrNull(logRequest.getLatencyMs())
                + ","
                + "\"statusCode\":"
                + valueOrNull(logRequest.getStatusCode())
                + "}";
    }

    private String valueOrNull(Integer value) {
        return value == null ? "null" : value.toString();
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}