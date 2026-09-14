package incident_core.repository;

import incident_core.entity.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
}