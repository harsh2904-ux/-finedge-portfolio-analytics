package com.portfolio.analytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${analytics.python.base-url:http://localhost:8001}")
    private String pythonBaseUrl;

    @Value("${analytics.python.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${analytics.python.read-timeout-ms:15000}")
    private int readTimeoutMs;

    @Bean
    public RestClient pythonAnalyticsRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(pythonBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
