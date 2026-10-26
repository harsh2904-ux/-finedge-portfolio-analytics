package com.portfolio.analytics.analytics.client;

import com.portfolio.analytics.analytics.dto.*;
import com.portfolio.analytics.exception.AnalyticsServiceUnavailableException;
import com.portfolio.analytics.exception.BusinessValidationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PythonAnalyticsClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PythonAnalyticsClient.class);


    private final RestClient pythonAnalyticsRestClient;
    public PythonAnalyticsClient(RestClient pythonAnalyticsRestClient) {
        this.pythonAnalyticsRestClient = pythonAnalyticsRestClient;
    }


    public PerformanceMetricsDto getPerformance(Long portfolioId, String lookback) {
        log.info("Requesting quantitative performance for portfolio ID={}, lookback={}", portfolioId, lookback);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "lookback", lookback != null ? lookback : "1Y");
        return executePost("/analytics/performance", req, PerformanceMetricsDto.class);
    }

    public RiskMetricsDto getRisk(Long portfolioId, String lookback) {
        log.info("Requesting quantitative risk metrics for portfolio ID={}, lookback={}", portfolioId, lookback);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "lookback", lookback != null ? lookback : "1Y");
        return executePost("/analytics/risk", req, RiskMetricsDto.class);
    }

    public DrawdownDto getDrawdown(Long portfolioId, String lookback) {
        log.info("Requesting drawdown series for portfolio ID={}, lookback={}", portfolioId, lookback);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "lookback", lookback != null ? lookback : "1Y");
        return executePost("/analytics/drawdown", req, DrawdownDto.class);
    }

    public VarDto getVaR(Long portfolioId, String lookback, Double confidence) {
        log.info("Requesting VaR for portfolio ID={}, lookback={}, confidence={}", portfolioId, lookback, confidence);
        Map<String, Object> req = new HashMap<>();
        req.put("portfolioId", portfolioId);
        req.put("lookback", lookback != null ? lookback : "1Y");
        req.put("confidenceLevel", confidence != null ? confidence : 0.95);
        return executePost("/analytics/var", req, VarDto.class);
    }

    public CorrelationDto getCorrelation(Long portfolioId, String lookback) {
        log.info("Requesting correlation matrix for portfolio ID={}, lookback={}", portfolioId, lookback);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "lookback", lookback != null ? lookback : "1Y");
        return executePost("/analytics/correlation", req, CorrelationDto.class);
    }

    public RiskAttributionDto getRiskAttribution(Long portfolioId, String lookback) {
        log.info("Requesting Euler risk attribution for portfolio ID={}, lookback={}", portfolioId, lookback);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "lookback", lookback != null ? lookback : "1Y");
        return executePost("/analytics/risk-contribution", req, RiskAttributionDto.class);
    }

    public OptimizationResultDto getOptimization(Long portfolioId, String method, String lookback, Double maxWeight) {
        log.info("Requesting Markowitz optimization for portfolio ID={}, method={}, maxWeight={}", portfolioId, method, maxWeight);
        Map<String, Object> req = new HashMap<>();
        req.put("portfolioId", portfolioId);
        req.put("method", method != null ? method : "MAX_SHARPE");
        req.put("lookback", lookback != null ? lookback : "3Y");
        req.put("maxWeight", maxWeight != null ? maxWeight : 1.0);
        return executePost("/analytics/optimization", req, OptimizationResultDto.class);
    }

    public EfficientFrontierResultDto getEfficientFrontier(Long portfolioId, Integer simulations, String lookback) {
        log.info("Requesting Efficient Frontier for portfolio ID={}, simulations={}", portfolioId, simulations);
        Map<String, Object> req = new HashMap<>();
        req.put("portfolioId", portfolioId);
        req.put("simulations", simulations != null ? simulations : 2000);
        req.put("lookback", lookback != null ? lookback : "3Y");
        return executePost("/analytics/efficient-frontier", req, EfficientFrontierResultDto.class);
    }

    public MonteCarloResultDto getMonteCarlo(Long portfolioId, Integer simulations, Integer horizonDays) {
        log.info("Requesting Monte Carlo simulation for portfolio ID={}, simulations={}, horizonDays={}", portfolioId, simulations, horizonDays);
        Map<String, Object> req = new HashMap<>();
        req.put("portfolioId", portfolioId);
        req.put("simulations", simulations != null ? simulations : 5000);
        req.put("horizonDays", horizonDays != null ? horizonDays : 252);
        return executePost("/analytics/monte-carlo", req, MonteCarloResultDto.class);
    }

    public ScenarioResultDto runScenario(Long portfolioId, Map<String, Double> shocks) {
        log.info("Requesting custom scenario shock for portfolio ID={}, shocks={}", portfolioId, shocks);
        Map<String, Object> req = Map.of("portfolioId", portfolioId, "shocks", shocks);
        return executePost("/analytics/scenario", req, ScenarioResultDto.class);
    }

    public List<StressTestResultDto> runStressTest(Long portfolioId, List<String> scenarios) {
        log.info("Requesting stress tests for portfolio ID={}, scenarios={}", portfolioId, scenarios);
        Map<String, Object> req = new HashMap<>();
        req.put("portfolioId", portfolioId);
        if (scenarios != null && !scenarios.isEmpty()) {
            req.put("scenarios", scenarios);
        }
        return executePostForList("/analytics/stress-test", req, new ParameterizedTypeReference<List<StressTestResultDto>>() {});
    }

    private <T> T executePost(String uri, Object body, Class<T> responseType) {
        long start = System.currentTimeMillis();
        try {
            T response = pythonAnalyticsRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        String errMsg = new String(res.getBody().readAllBytes());
                        log.warn("Analytics service returned client error {}: {}", res.getStatusCode(), errMsg);
                        throw new BusinessValidationException("Analytics calculation failed: " + errMsg);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        String errMsg = new String(res.getBody().readAllBytes());
                        log.error("Analytics service internal error {}: {}", res.getStatusCode(), errMsg);
                        throw new AnalyticsServiceUnavailableException("The Python analytics service encountered an error.");
                    })
                    .body(responseType);

            long elapsed = System.currentTimeMillis() - start;
            log.info("Analytics call to {} completed in {} ms", uri, elapsed);
            return response;
        } catch (BusinessValidationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to communicate with Python analytics service at {}: {}", uri, e.getMessage());
            throw new AnalyticsServiceUnavailableException("The analytics service is temporarily unavailable.", e);
        }
    }

    private <T> T executePostForList(String uri, Object body, ParameterizedTypeReference<T> responseType) {
        long start = System.currentTimeMillis();
        try {
            T response = pythonAnalyticsRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        String errMsg = new String(res.getBody().readAllBytes());
                        throw new BusinessValidationException("Analytics calculation failed: " + errMsg);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new AnalyticsServiceUnavailableException("The Python analytics service encountered an error.");
                    })
                    .body(responseType);

            long elapsed = System.currentTimeMillis() - start;
            log.info("Analytics list call to {} completed in {} ms", uri, elapsed);
            return response;
        } catch (BusinessValidationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to communicate with Python analytics service at {}: {}", uri, e.getMessage());
            throw new AnalyticsServiceUnavailableException("The analytics service is temporarily unavailable.", e);
        }
    }
}
