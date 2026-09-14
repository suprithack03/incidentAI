package incident_core.repository;

import incident_core.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
            Long serviceId,
            LocalDateTime start,
            LocalDateTime end);
}