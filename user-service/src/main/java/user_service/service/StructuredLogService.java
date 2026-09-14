package user_service.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StructuredLogService {

    public void log(
            String level,
            String message,
            Integer latencyMs,
            Integer statusCode) {

        String json =
                "{"
                        + "\"service\":\"user-service\","
                        + "\"timestamp\":\""
                        + LocalDateTime.now()
                        + "\","
                        + "\"level\":\""
                        + escape(level)
                        + "\","
                        + "\"message\":\""
                        + escape(message)
                        + "\","
                        + "\"latencyMs\":"
                        + valueOrNull(latencyMs)
                        + ","
                        + "\"statusCode\":"
                        + valueOrNull(statusCode)
                        + "}";

        System.out.println(json);
    }

    private String valueOrNull(Integer value) {

        return value == null
                ? "null"
                : value.toString();
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}