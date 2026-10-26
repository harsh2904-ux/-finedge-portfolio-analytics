package com.portfolio.analytics.risk;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.DrawdownDto;
import com.portfolio.analytics.analytics.dto.RiskMetricsDto;
import com.portfolio.analytics.analytics.dto.VarDto;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class RiskService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RiskService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public RiskService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    public RiskMetricsDto getRisk(Long portfolioId, String lookback, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        return analyticsClient.getRisk(portfolioId, lookback != null ? lookback : "1Y");
    }

    public DrawdownDto getDrawdown(Long portfolioId, String lookback, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        return analyticsClient.getDrawdown(portfolioId, lookback != null ? lookback : "1Y");
    }

    public VarDto getVaR(Long portfolioId, String lookback, Double confidence, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        return analyticsClient.getVaR(portfolioId, lookback != null ? lookback : "1Y", confidence != null ? confidence : 0.95);
    }
}
