package incident_core.severity;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CoreServiceConfig {

    private final Set<String> coreServices = Set.of(
            "payment-service"
    );

    public boolean isCoreService(String serviceName) {
        return coreServices.contains(serviceName);
    }
}