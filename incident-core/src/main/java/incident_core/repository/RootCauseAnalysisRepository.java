package incident_core.repository;

import incident_core.entity.RootCauseAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RootCauseAnalysisRepository
        extends JpaRepository<RootCauseAnalysis, Long> {

    Optional<RootCauseAnalysis> findByIncidentId(Long incidentId);

    boolean existsByIncidentId(Long incidentId);
}