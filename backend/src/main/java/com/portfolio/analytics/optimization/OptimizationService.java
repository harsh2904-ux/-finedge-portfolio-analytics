package com.portfolio.analytics.optimization;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.EfficientFrontierResultDto;
import com.portfolio.analytics.analytics.dto.OptimizationResultDto;
import com.portfolio.analytics.optimization.dto.EfficientFrontierApiRequest;
import com.portfolio.analytics.optimization.dto.OptimizationApiRequest;
import com.portfolio.analytics.optimization.dto.OptimizationApiResponse;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OptimizationService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OptimizationService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public OptimizationService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    @Cacheable(value = "optimization", key = "#portfolioId + '_' + #request.method + '_' + #request.lookback + '_' + #request.maxWeight")
    public CachedAnalyticsResult<OptimizationApiResponse> optimize(
            Long portfolioId,
            OptimizationApiRequest request,
            UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Running optimization for portfolio ID={}, method={}", portfolioId, request.getMethod());

        OptimizationResultDto result = analyticsClient.getOptimization(
                portfolioId,
                request.getMethod(),
                request.getLookback(),
                request.getMaxWeight()
        );

        OptimizationApiResponse apiResponse = OptimizationApiResponse.fromResult(result);
        return CachedAnalyticsResult.of(apiResponse);
    }

    @Cacheable(value = "efficient_frontier", key = "#portfolioId + '_' + #request.simulations + '_' + #request.lookback")
    public CachedAnalyticsResult<EfficientFrontierResultDto> getEfficientFrontier(
            Long portfolioId,
            EfficientFrontierApiRequest request,
            UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Generating efficient frontier for portfolio ID={}, simulations={}", portfolioId, request.getSimulations());

        EfficientFrontierResultDto result = analyticsClient.getEfficientFrontier(
                portfolioId,
                request.getSimulations(),
                request.getLookback()
        );

        return CachedAnalyticsResult.of(result);
    }
}
