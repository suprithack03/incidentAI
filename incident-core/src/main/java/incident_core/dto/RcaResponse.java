package incident_core.dto;

public class RcaResponse {

    private Long incidentId;
    private StructuredRca rca;

    public RcaResponse() {
    }

    public RcaResponse(Long incidentId, StructuredRca rca) {
        this.incidentId = incidentId;
        this.rca = rca;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public StructuredRca getRca() {
        return rca;
    }

    public void setRca(StructuredRca rca) {
        this.rca = rca;
    }
}