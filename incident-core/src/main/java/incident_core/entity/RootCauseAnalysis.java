package incident_core.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "root_cause_analysis")
public class RootCauseAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_id", nullable = false)
    private Long incidentId;

    @Column(name = "probable_cause", nullable = false, columnDefinition = "TEXT")
    private String probableCause;

    @Column(name = "supporting_evidence", columnDefinition = "TEXT")
    private String supportingEvidence;

    @Column(name = "affected_service", length = 255)
    private String affectedService;

    @Column(name = "confidence", nullable = false, length = 20)
    private String confidence;

    @Column(name = "recommended_remediation", columnDefinition = "TEXT")
    private String recommendedRemediation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RootCauseAnalysis() {
    }

    public Long getId() {
        return id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public String getProbableCause() {
        return probableCause;
    }

    public String getSupportingEvidence() {
        return supportingEvidence;
    }

    public String getAffectedService() {
        return affectedService;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getRecommendedRemediation() {
        return recommendedRemediation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public void setProbableCause(String probableCause) {
        this.probableCause = probableCause;
    }

    public void setSupportingEvidence(String supportingEvidence) {
        this.supportingEvidence = supportingEvidence;
    }

    public void setAffectedService(String affectedService) {
        this.affectedService = affectedService;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public void setRecommendedRemediation(String recommendedRemediation) {
        this.recommendedRemediation = recommendedRemediation;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}