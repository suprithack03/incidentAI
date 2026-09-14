package incident_core.detection;

import incident_core.entity.LogEntry;

import java.util.List;

public class SlowResponseRule {

    private static final double LATENCY_THRESHOLD_MS = 1000.0;
    private static final int MIN_CONSECUTIVE_REQUESTS = 3;

    public boolean isTriggered(List<LogEntry> logs) {

        if (logs.size() < MIN_CONSECUTIVE_REQUESTS) {
            return false;
        }

        List<LogEntry> latencyLogs = logs.stream()
                .filter(log -> log.getLatencyMs() != null)
                .toList();

        if (latencyLogs.size() < MIN_CONSECUTIVE_REQUESTS) {
            return false;
        }

        for (int i = 0;
             i <= latencyLogs.size() - MIN_CONSECUTIVE_REQUESTS;
             i++) {

            List<LogEntry> consecutiveLogs =
                    latencyLogs.subList(
                            i,
                            i + MIN_CONSECUTIVE_REQUESTS
                    );

            double averageLatency =
                    consecutiveLogs.stream()
                            .mapToInt(LogEntry::getLatencyMs)
                            .average()
                            .orElse(0.0);

            if (averageLatency > LATENCY_THRESHOLD_MS) {
                return true;
            }
        }

        return false;
    }

    public double getMetricValue(List<LogEntry> logs) {

        return logs.stream()
                .filter(log -> log.getLatencyMs() != null)
                .mapToInt(LogEntry::getLatencyMs)
                .average()
                .orElse(0.0);
    }

    public double getThreshold() {
        return LATENCY_THRESHOLD_MS;
    }

    public int getMinimumConsecutiveRequests() {
        return MIN_CONSECUTIVE_REQUESTS;
    }
}