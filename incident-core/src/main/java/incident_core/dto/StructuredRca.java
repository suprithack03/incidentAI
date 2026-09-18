package incident_core.dto;

import java.util.List;

public class StructuredRca {

    private String probableCause;
    private List<String> supportingEvidence;
    private String affectedService;
    private String confidence;
    private String recommendedRemediation;

    public StructuredRca() {
    }

    public String getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(String probableCause) {
        this.probableCause = probableCause;
    }

    public List<String> getSupportingEvidence() {
        return supportingEvidence;
    }

    public void setSupportingEvidence(List<String> supportingEvidence) {
        this.supportingEvidence = supportingEvidence;
    }

    public String getAffectedService() {
        return affectedService;
    }

    public void setAffectedService(String affectedService) {
        this.affectedService = affectedService;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getRecommendedRemediation() {
        return recommendedRemediation;
    }

    public void setRecommendedRemediation(String recommendedRemediation) {
        this.recommendedRemediation = recommendedRemediation;
    }
}