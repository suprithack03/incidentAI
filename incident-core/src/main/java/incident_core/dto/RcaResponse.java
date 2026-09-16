package incident_core.dto;

public class RcaResponse {

    private Long incidentId;
    private String rca;

    public RcaResponse() {
    }

    public RcaResponse(Long incidentId, String rca) {
        this.incidentId = incidentId;
        this.rca = rca;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public String getRca() {
        return rca;
    }

    public void setRca(String rca) {
        this.rca = rca;
    }
}

