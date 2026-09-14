package incident_core.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_entry")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "status_code")
    private Integer statusCode;

    public LogEntry() {
    }

    public LogEntry(
            Service service,
            LocalDateTime timestamp,
            LogLevel level,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
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