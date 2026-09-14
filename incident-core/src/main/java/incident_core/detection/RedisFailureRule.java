package incident_core.detection;

import incident_core.entity.LogEntry;

import java.util.List;

public class RedisFailureRule {

    private static final List<String> REDIS_FAILURE_PATTERNS = List.of(
            "redis connection failed",
            "redis connection refused",
            "connection to redis failed",
            "could not connect to redis",
            "redis timeout",
            "redis unavailable"
    );

    public boolean isTriggered(List<LogEntry> logs) {

        return logs.stream()
                .anyMatch(this::isRedisFailure);
    }

    private boolean isRedisFailure(LogEntry log) {

        if (log.getLevel() == null ||
                log.getMessage() == null) {
            return false;
        }

        if (!log.getLevel().name().equals("ERROR")) {
            return false;
        }

        String message =
                log.getMessage().toLowerCase();

        return REDIS_FAILURE_PATTERNS.stream()
                .anyMatch(message::contains);
    }
}