package com.portfolio.analytics.diversification;

import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.CorrelationDto;
import com.portfolio.analytics.analytics.dto.RiskAttributionDto;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.portfolio.dto.PortfolioValuationResponse;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiversificationService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DiversificationService.class);


    private final PortfolioService portfolioService;
    private final PythonAnalyticsClient analyticsClient;
    public DiversificationService(PortfolioService portfolioService, PythonAnalyticsClient analyticsClient) {
        this.portfolioService = portfolioService;
        this.analyticsClient = analyticsClient;
    }


    public Map<String, Object> getAllocation(Long portfolioId, UserPrincipal principal) {
        PortfolioValuationResponse valuation = portfolioService.getPortfolioValuation(portfolioId, principal);

        List<Map<String, Object>> assetList = valuation.getPositions().stream()
                .map(pos -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("symbol", pos.getSymbol());
                    item.put("name", pos.getName());
                    item.put("assetClass", pos.getAssetClass());
                    item.put("weight", pos.getWeight());
                    item.put("marketValue", pos.getMarketValue());
                    return item;
                })
                .toList();

        return Map.of(
                "portfolioId", portfolioId,
                "portfolioValue", valuation.getPortfolioValue(),
                "currency", valuation.getCurrency(),
                "assets", assetList
        );
    }

    public CorrelationDto getCorrelation(Long portfolioId, String lookback, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        return analyticsClient.getCorrelation(portfolioId, lookback != null ? lookback : "1Y");
    }

    public RiskAttributionDto getRiskContribution(Long portfolioId, String lookback, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        return analyticsClient.getRiskAttribution(portfolioId, lookback != null ? lookback : "1Y");
    }
}
