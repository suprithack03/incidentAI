package incident_core.grouping;

import java.time.Duration;

public final class IncidentGroupingConfig {

    private IncidentGroupingConfig() {
    }

    public static final Duration GROUPING_WINDOW =
            Duration.ofMinutes(10);
}