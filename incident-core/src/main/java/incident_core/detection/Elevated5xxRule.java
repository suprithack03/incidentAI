package incident_core.detection;

import incident_core.entity.LogEntry;

import java.util.List;

public class Elevated5xxRule {

    private static final int THRESHOLD = 5;

    public boolean isTriggered(List<LogEntry> logs) {

        long errorCount = logs.stream()
                .filter(log -> log.getStatusCode() != null)
                .filter(log -> log.getStatusCode() >= 500)
                .count();

        return errorCount > THRESHOLD;
    }

    public long getMetricValue(List<LogEntry> logs) {

        return logs.stream()
                .filter(log -> log.getStatusCode() != null)
                .filter(log -> log.getStatusCode() >= 500)
                .count();
    }

    public int getThreshold() {
        return THRESHOLD;
    }
}