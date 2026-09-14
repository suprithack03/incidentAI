package incident_core.repository;

import incident_core.entity.Incident;
import incident_core.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByStatusIn(
            List<IncidentStatus> statuses);
}