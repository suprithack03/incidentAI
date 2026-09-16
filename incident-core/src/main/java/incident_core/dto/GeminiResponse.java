package incident_core.dto;

public class GeminiResponse {

    private String rca;

    public GeminiResponse() {
    }

    public GeminiResponse(String rca) {
        this.rca = rca;
    }

    public String getRca() {
        return rca;
    }

    public void setRca(String rca) {
        this.rca = rca;
    }
}