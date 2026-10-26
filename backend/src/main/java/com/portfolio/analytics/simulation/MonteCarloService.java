package com.portfolio.analytics.simulation;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.MonteCarloResultDto;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.simulation.dto.MonteCarloApiRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MonteCarloService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonteCarloService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public MonteCarloService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    @Cacheable(value = "monte_carlo", key = "#portfolioId + '_' + #request.simulations + '_' + #request.horizonDays")
    public CachedAnalyticsResult<MonteCarloResultDto> simulate(
            Long portfolioId,
            MonteCarloApiRequest request,
            UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Running Monte Carlo simulation for portfolio ID={}, simulations={}, horizon={}",
                portfolioId, request.getSimulations(), request.getHorizonDays());

        MonteCarloResultDto result = analyticsClient.getMonteCarlo(
                portfolioId,
                request.getSimulations(),
                request.getHorizonDays()
        );

        return CachedAnalyticsResult.of(result);
    }
}
