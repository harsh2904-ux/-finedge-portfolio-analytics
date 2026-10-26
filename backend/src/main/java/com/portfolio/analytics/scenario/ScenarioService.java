package com.portfolio.analytics.scenario;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.ScenarioResultDto;
import com.portfolio.analytics.analytics.dto.StressTestResultDto;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.scenario.dto.ScenarioApiRequest;
import com.portfolio.analytics.scenario.dto.StressTestApiRequest;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScenarioService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScenarioService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public ScenarioService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    public ScenarioResultDto runCustomScenario(Long portfolioId, ScenarioApiRequest request, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Running custom scenario shock for portfolio ID={}", portfolioId);
        return analyticsClient.runScenario(portfolioId, request.getShocks());
    }

    @Cacheable(value = "stress_tests", key = "#portfolioId")
    public CachedAnalyticsResult<List<StressTestResultDto>> runPredefinedStressTests(Long portfolioId, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Running all predefined stress tests for portfolio ID={}", portfolioId);
        List<StressTestResultDto> results = analyticsClient.runStressTest(portfolioId, null);
        return CachedAnalyticsResult.of(results);
    }

    public List<StressTestResultDto> runSelectedStressTests(Long portfolioId, StressTestApiRequest request, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        log.info("Running selected stress tests for portfolio ID={}", portfolioId);
        return analyticsClient.runStressTest(portfolioId, request.getScenarios());
    }
}
