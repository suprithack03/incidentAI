package incident_core.repository;

import incident_core.entity.IncidentAnomaly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentAnomalyRepository
        extends JpaRepository<IncidentAnomaly, Long> {

    List<IncidentAnomaly> findByIncidentId(Long incidentId);
}