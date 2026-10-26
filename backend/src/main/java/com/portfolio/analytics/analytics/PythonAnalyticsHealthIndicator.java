package com.portfolio.analytics.analytics;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class PythonAnalyticsHealthIndicator implements HealthIndicator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PythonAnalyticsHealthIndicator.class);


    private final RestClient pythonAnalyticsRestClient;
    public PythonAnalyticsHealthIndicator(RestClient pythonAnalyticsRestClient) {
        this.pythonAnalyticsRestClient = pythonAnalyticsRestClient;
    }


    @Override
    public Health health() {
        try {
            Map<?, ?> response = pythonAnalyticsRestClient.get()
                    .uri("/health")
                    .retrieve()
                    .body(Map.class);

            return Health.up()
                    .withDetail("service", "Python Quantitative Analytics Engine")
                    .withDetail("status", response != null ? response.get("status") : "UP")
                    .build();
        } catch (Exception e) {
            log.warn("Health check failed for Python analytics engine: {}", e.getMessage());
            return Health.down()
                    .withDetail("service", "Python Quantitative Analytics Engine")
                    .withDetail("error", "Service unreachable: " + e.getMessage())
                    .build();
        }
    }
}
