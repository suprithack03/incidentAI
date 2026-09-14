package incident_core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly")
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnomalyType type;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "threshold_used", nullable = false)
    private Double thresholdUsed;

    public Anomaly() {
    }

    public Anomaly(
            Service service,
            AnomalyType type,
            LocalDateTime detectedAt,
            Double metricValue,
            Double thresholdUsed) {

        this.service = service;
        this.type = type;
        this.detectedAt = detectedAt;
        this.metricValue = metricValue;
        this.thresholdUsed = thresholdUsed;
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

    public AnomalyType getType() {
        return type;
    }

    public void setType(AnomalyType type) {
        this.type = type;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public Double getThresholdUsed() {
        return thresholdUsed;
    }

    public void setThresholdUsed(Double thresholdUsed) {
        this.thresholdUsed = thresholdUsed;
    }
}