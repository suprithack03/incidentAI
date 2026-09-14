package incident_core.grouping;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ServiceDependencyMap {

    private final Map<String, Set<String>> dependencies = Map.of(
            "user-service", Set.of("payment-service"),
            "payment-service", Set.of(),
            "inventory-service", Set.of()
    );

    public boolean hasDependency(
            String service,
            String dependency) {

        return dependencies
                .getOrDefault(service, Set.of())
                .contains(dependency);
    }
}