package user_service.service;

import user_service.dto.IncidentLogRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class IncidentLogReporter {

    private final HttpClient httpClient;
    private final String incidentCoreUrl;
    private final String internalServiceToken;

    public IncidentLogReporter(
            @Value("${incident.core.url}") String incidentCoreUrl,
            @Value("${internal.service.token}") String internalServiceToken) {

        this.httpClient = HttpClient.newHttpClient();
        this.incidentCoreUrl = incidentCoreUrl;
        this.internalServiceToken = internalServiceToken;
    }

    public void report(IncidentLogRequest logRequest) {

        try {

            String json = "{"
                    + "\"service\":\"" + escape(logRequest.getService()) + "\","
                    + "\"timestamp\":\"" + logRequest.getTimestamp() + "\","
                    + "\"level\":\"" + escape(logRequest.getLevel()) + "\","
                    + "\"message\":\"" + escape(logRequest.getMessage()) + "\","
                    + "\"latencyMs\":" + valueOrNull(logRequest.getLatencyMs()) + ","
                    + "\"statusCode\":" + valueOrNull(logRequest.getStatusCode())
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            incidentCoreUrl + "/api/logs/ingest"
                    ))
                    .header("Content-Type", "application/json")
                    .header("X-Internal-Token", internalServiceToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

        } catch (Exception exception) {

            System.err.println(
                    "Failed to report log to Incident Core: "
                            + exception.getMessage()
            );
        }
    }

    private String valueOrNull(Integer value) {

        return value == null
                ? "null"
                : value.toString();
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