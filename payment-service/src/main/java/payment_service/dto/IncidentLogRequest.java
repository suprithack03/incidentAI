package payment_service.dto;

import java.time.LocalDateTime;

public class IncidentLogRequest {

    private String service;
    private LocalDateTime timestamp;
    private String level;
    private String message;
    private Integer latencyMs;
    private Integer statusCode;

    public IncidentLogRequest() {
    }

    public IncidentLogRequest(
            String service,
            LocalDateTime timestamp,
            String level,
            String message,
            Integer latencyMs,
            Integer statusCode) {

        this.service = service;
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}