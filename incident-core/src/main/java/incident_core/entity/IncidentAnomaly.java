package incident_core.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "incident_anomaly",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"incident_id", "anomaly_id"}
                )
        }
)
public class IncidentAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anomaly_id", nullable = false)
    private Anomaly anomaly;

    public IncidentAnomaly() {
    }

    public IncidentAnomaly(
            Incident incident,
            Anomaly anomaly) {

        this.incident = incident;
        this.anomaly = anomaly;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Anomaly anomaly) {
        this.anomaly = anomaly;
    }
}