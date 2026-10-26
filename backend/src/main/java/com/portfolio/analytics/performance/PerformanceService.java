package com.portfolio.analytics.performance;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.PerformanceMetricsDto;
import com.portfolio.analytics.portfolio.Portfolio;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PerformanceService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PerformanceService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public PerformanceService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    public PerformanceMetricsDto getPerformance(Long portfolioId, String lookback, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal); // ownership check
        return analyticsClient.getPerformance(portfolioId, lookback != null ? lookback : "1Y");
    }

    public Map<String, Object> getPerformanceSummary(Long portfolioId, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        PerformanceMetricsDto perf = analyticsClient.getPerformance(portfolioId, "1Y");

        return Map.of(
                "totalReturn", perf.getTotalReturn(),
                "cagr", perf.getCagr(),
                "annualizedReturn", perf.getAnnualizedReturn(),
                "benchmarkReturn", perf.getBenchmarkReturn(),
                "excessReturn", perf.getExcessReturn()
        );
    }

    public Map<String, Object> getBenchmarkComparison(Long portfolioId, String lookback, UserPrincipal principal) {
        Portfolio p = portfolioService.getPortfolioEntityById(portfolioId, principal);
        PerformanceMetricsDto perf = analyticsClient.getPerformance(portfolioId, lookback != null ? lookback : "1Y");

        String benchmark = p.getBenchmarkAsset() != null ? p.getBenchmarkAsset().getSymbol() : "NIFTYBEES";
        return Map.of(
                "portfolioId", portfolioId,
                "benchmarkSymbol", benchmark,
                "portfolioReturn", perf.getTotalReturn(),
                "benchmarkReturn", perf.getBenchmarkReturn(),
                "excessReturn", perf.getExcessReturn(),
                "trackingError", perf.getTrackingError(),
                "informationRatio", perf.getInformationRatio()
        );
    }
}
