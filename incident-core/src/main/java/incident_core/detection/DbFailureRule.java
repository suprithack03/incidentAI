package incident_core.detection;

import incident_core.entity.LogEntry;

import java.util.List;

public class DbFailureRule {

    private static final List<String> DB_FAILURE_PATTERNS = List.of(
            "database connection failed",
            "database connection refused",
            "connection to database failed",
            "could not connect to database"
    );

    public boolean isTriggered(List<LogEntry> logs) {

        return logs.stream()
                .anyMatch(this::isDatabaseFailure);
    }

    private boolean isDatabaseFailure(LogEntry log) {

        if (log.getLevel() == null ||
                log.getMessage() == null) {
            return false;
        }

        if (!log.getLevel().name().equals("ERROR")) {
            return false;
        }

        String message =
                log.getMessage().toLowerCase();

        return DB_FAILURE_PATTERNS.stream()
                .anyMatch(message::contains);
    }
}