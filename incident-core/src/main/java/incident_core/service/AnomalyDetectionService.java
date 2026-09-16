package incident_core.service;

import incident_core.detection.DbFailureRule;
import incident_core.detection.Elevated5xxRule;
import incident_core.detection.RedisFailureRule;
import incident_core.detection.SlowResponseRule;
import incident_core.entity.Anomaly;
import incident_core.entity.AnomalyType;
import incident_core.entity.LogEntry;
import incident_core.repository.AnomalyRepository;
import incident_core.repository.LogEntryRepository;
import incident_core.repository.ServiceRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnomalyDetectionService {

    private final ServiceRepository serviceRepository;
    private final LogEntryRepository logEntryRepository;
    private final AnomalyRepository anomalyRepository;
    private final ServiceStatusService serviceStatusService;

    private final Elevated5xxRule elevated5xxRule =
            new Elevated5xxRule();

    private final SlowResponseRule slowResponseRule =
            new SlowResponseRule();

    private final DbFailureRule dbFailureRule =
            new DbFailureRule();

    private final RedisFailureRule redisFailureRule =
            new RedisFailureRule();

    public AnomalyDetectionService(
            ServiceRepository serviceRepository,
            LogEntryRepository logEntryRepository,
            AnomalyRepository anomalyRepository,
            ServiceStatusService serviceStatusService) {

        this.serviceRepository = serviceRepository;
        this.logEntryRepository = logEntryRepository;
        this.anomalyRepository = anomalyRepository;
        this.serviceStatusService = serviceStatusService;
    }

    public Anomaly detectElevated5xx(
            String serviceName,
            LocalDateTime endTime) {

        incident_core.entity.Service service = serviceRepository
                .findByName(serviceName)
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        LocalDateTime startTime =
                endTime.minusMinutes(5);

        List<LogEntry> logs =
                logEntryRepository.findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
                        service.getId(),
                        startTime,
                        endTime
                );

        if (!elevated5xxRule.isTriggered(logs)) {
            return null;
        }

        long metricValue =
                elevated5xxRule.getMetricValue(logs);

        Anomaly anomaly = new Anomaly(
                service,
                AnomalyType.ELEVATED_5XX,
                LocalDateTime.now(),
                (double) metricValue,
                (double) elevated5xxRule.getThreshold()
        );

        Anomaly savedAnomaly =
                anomalyRepository.save(anomaly);

        serviceStatusService.updateStatus(
                service,
                AnomalyType.ELEVATED_5XX
        );

        return savedAnomaly;
    }

    public Anomaly detectSlowResponse(
            String serviceName,
            LocalDateTime endTime) {

        incident_core.entity.Service service = serviceRepository
                .findByName(serviceName)
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        LocalDateTime startTime =
                endTime.minusMinutes(5);

        List<LogEntry> logs =
                logEntryRepository.findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
                        service.getId(),
                        startTime,
                        endTime
                );

        if (!slowResponseRule.isTriggered(logs)) {
            return null;
        }

        double metricValue =
                slowResponseRule.getMetricValue(logs);

        Anomaly anomaly = new Anomaly(
                service,
                AnomalyType.SLOW_RESPONSE,
                LocalDateTime.now(),
                metricValue,
                slowResponseRule.getThreshold()
        );

        Anomaly savedAnomaly =
                anomalyRepository.save(anomaly);

        serviceStatusService.updateStatus(
                service,
                AnomalyType.SLOW_RESPONSE
        );

        return savedAnomaly;
    }

    public Anomaly detectDbFailure(
            String serviceName,
            LocalDateTime endTime) {

        incident_core.entity.Service service = serviceRepository
                .findByName(serviceName)
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        LocalDateTime startTime =
                endTime.minusMinutes(5);

        List<LogEntry> logs =
                logEntryRepository.findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
                        service.getId(),
                        startTime,
                        endTime
                );

        if (!dbFailureRule.isTriggered(logs)) {
            return null;
        }

        double metricValue = 1.0;

        Anomaly anomaly = new Anomaly(
                service,
                AnomalyType.DB_FAILURE,
                LocalDateTime.now(),
                metricValue,
                1.0
        );

        Anomaly savedAnomaly =
                anomalyRepository.save(anomaly);

        serviceStatusService.updateStatus(
                service,
                AnomalyType.DB_FAILURE
        );

        return savedAnomaly;
    }

    public Anomaly detectRedisFailure(
            String serviceName,
            LocalDateTime endTime) {

        incident_core.entity.Service service = serviceRepository
                .findByName(serviceName)
                .orElseThrow(() ->
                        new RuntimeException("Service not found"));

        LocalDateTime startTime =
                endTime.minusMinutes(5);

        List<LogEntry> logs =
                logEntryRepository.findByServiceIdAndTimestampBetweenOrderByTimestampAsc(
                        service.getId(),
                        startTime,
                        endTime
                );

        if (!redisFailureRule.isTriggered(logs)) {
            return null;
        }

        double metricValue = 1.0;

        Anomaly anomaly = new Anomaly(
                service,
                AnomalyType.REDIS_FAILURE,
                LocalDateTime.now(),
                metricValue,
                1.0
        );

        Anomaly savedAnomaly =
                anomalyRepository.save(anomaly);

        serviceStatusService.updateStatus(
                service,
                AnomalyType.REDIS_FAILURE
        );

        return savedAnomaly;
    }
}