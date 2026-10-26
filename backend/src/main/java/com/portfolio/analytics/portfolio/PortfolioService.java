package com.portfolio.analytics.portfolio;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetRepository;
import org.springframework.security.access.AccessDeniedException;
import com.portfolio.analytics.exception.ResourceNotFoundException;
import com.portfolio.analytics.holding.PortfolioHolding;
import com.portfolio.analytics.market.MarketPrice;
import com.portfolio.analytics.market.MarketPriceRepository;
import com.portfolio.analytics.portfolio.dto.*;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.user.Role;
import com.portfolio.analytics.user.User;
import com.portfolio.analytics.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PortfolioService.class);


    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final MarketPriceRepository marketPriceRepository;
    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository, AssetRepository assetRepository, MarketPriceRepository marketPriceRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.marketPriceRepository = marketPriceRepository;
    }


    public void checkOwnership(Portfolio portfolio, UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("User is not authenticated.");
        }
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (portfolio.getUser() == null || !portfolio.getUser().getId().equals(principal.getId()))) {
            throw new AccessDeniedException("You do not have permission to access portfolio ID: " + portfolio.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosForUser(UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<Portfolio> list = isAdmin
                ? portfolioRepository.findAll()
                : portfolioRepository.findByUserId(principal.getId());
        return list.stream().map(PortfolioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioById(Long id, UserPrincipal principal) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        checkOwnership(portfolio, principal);
        return PortfolioResponse.fromEntity(portfolio);
    }

    @Transactional(readOnly = true)
    public Portfolio getPortfolioEntityById(Long id, UserPrincipal principal) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        checkOwnership(portfolio, principal);
        return portfolio;
    }

    @Transactional
    public PortfolioResponse createPortfolio(CreatePortfolioRequest request, UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + principal.getId()));

        Asset benchmark = null;
        if (request.getBenchmarkAssetId() != null) {
            benchmark = assetRepository.findById(request.getBenchmarkAssetId())
                    .orElse(null);
        } else if (StringUtils.hasText(request.getBenchmarkSymbol())) {
            benchmark = assetRepository.findBySymbol(request.getBenchmarkSymbol().toUpperCase())
                    .orElse(null);
        }

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .name(request.getName().trim())
                .baseCurrency(StringUtils.hasText(request.getBaseCurrency()) ? request.getBaseCurrency().toUpperCase() : "INR")
                .benchmarkAsset(benchmark)
                .build();

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Created portfolio ID={} for user ID={}", saved.getId(), user.getId());
        return PortfolioResponse.fromEntity(saved);
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long id, UpdatePortfolioRequest request, UserPrincipal principal) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        checkOwnership(portfolio, principal);

        if (StringUtils.hasText(request.getName())) {
            portfolio.setName(request.getName().trim());
        }
        if (StringUtils.hasText(request.getBaseCurrency())) {
            portfolio.setBaseCurrency(request.getBaseCurrency().toUpperCase());
        }
        if (request.getBenchmarkAssetId() != null) {
            Asset benchmark = assetRepository.findById(request.getBenchmarkAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benchmark asset not found with ID: " + request.getBenchmarkAssetId()));
            portfolio.setBenchmarkAsset(benchmark);
        }

        Portfolio updated = portfolioRepository.save(portfolio);
        log.info("Updated portfolio ID={}", updated.getId());
        return PortfolioResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePortfolio(Long id, UserPrincipal principal) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        checkOwnership(portfolio, principal);
        portfolioRepository.delete(portfolio);
        log.info("Deleted portfolio ID={}", id);
    }

    @Transactional(readOnly = true)
    public PortfolioValuationResponse getPortfolioValuation(Long id, UserPrincipal principal) {
        Portfolio portfolio = portfolioRepository.findByIdWithHoldings(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        checkOwnership(portfolio, principal);

        double totalMarketValue = 0.0;
        double totalCostBasis = 0.0;
        double totalDailyPnL = 0.0;
        Instant latestTimestamp = Instant.now();

        List<PortfolioValuationResponse.PositionValuationDto> positionDtos = new ArrayList<>();

        for (PortfolioHolding holding : portfolio.getHoldings()) {
            Asset asset = holding.getAsset();
            List<MarketPrice> latestPrices = marketPriceRepository.findTop2BySymbolOrderByTimestampDesc(asset.getSymbol(), PageRequest.of(0, 2));

            double currentPrice = holding.getAverageCost(); // fallback to cost if no price
            double previousPrice = currentPrice;

            if (!latestPrices.isEmpty()) {
                currentPrice = latestPrices.get(0).getClose();
                latestTimestamp = latestPrices.get(0).getTimestamp();
                if (latestPrices.size() > 1) {
                    previousPrice = latestPrices.get(1).getClose();
                } else {
                    previousPrice = currentPrice;
                }
            }

            double marketValue = holding.getQuantity() * currentPrice;
            double costBasis = holding.getQuantity() * holding.getAverageCost();
            double unrealizedPnL = marketValue - costBasis;
            double unrealizedPnLPercent = costBasis > 0 ? (unrealizedPnL / costBasis) * 100.0 : 0.0;
            double posDailyReturn = previousPrice > 0 ? ((currentPrice - previousPrice) / previousPrice) * 100.0 : 0.0;
            double posDailyPnL = holding.getQuantity() * (currentPrice - previousPrice);

            totalMarketValue += marketValue;
            totalCostBasis += costBasis;
            totalDailyPnL += posDailyPnL;

            positionDtos.add(PortfolioValuationResponse.PositionValuationDto.builder()
                    .assetId(asset.getId())
                    .symbol(asset.getSymbol())
                    .name(asset.getName())
                    .assetClass(asset.getAssetClass())
                    .quantity(holding.getQuantity())
                    .averageCost(Math.round(holding.getAverageCost() * 100.0) / 100.0)
                    .currentPrice(Math.round(currentPrice * 100.0) / 100.0)
                    .marketValue(Math.round(marketValue * 100.0) / 100.0)
                    .costBasis(Math.round(costBasis * 100.0) / 100.0)
                    .unrealizedPnL(Math.round(unrealizedPnL * 100.0) / 100.0)
                    .unrealizedPnLPercent(Math.round(unrealizedPnLPercent * 100.0) / 100.0)
                    .dailyReturn(Math.round(posDailyReturn * 100.0) / 100.0)
                    .build());
        }

        // Calculate actual weights
        for (PortfolioValuationResponse.PositionValuationDto pos : positionDtos) {
            double weight = totalMarketValue > 0 ? pos.getMarketValue() / totalMarketValue : 0.0;
            pos.setWeight(Math.round(weight * 10000.0) / 10000.0);
        }

        double totalUnrealizedPnL = totalMarketValue - totalCostBasis;
        double totalUnrealizedPnLPercent = totalCostBasis > 0 ? (totalUnrealizedPnL / totalCostBasis) * 100.0 : 0.0;
        double dailyReturn = (totalMarketValue - totalDailyPnL) > 0
                ? (totalDailyPnL / (totalMarketValue - totalDailyPnL)) * 100.0
                : 0.0;

        return PortfolioValuationResponse.builder()
                .portfolioId(portfolio.getId())
                .portfolioName(portfolio.getName())
                .portfolioValue(Math.round(totalMarketValue * 100.0) / 100.0)
                .dailyPnL(Math.round(totalDailyPnL * 100.0) / 100.0)
                .dailyReturn(Math.round(dailyReturn * 100.0) / 100.0)
                .totalCostBasis(Math.round(totalCostBasis * 100.0) / 100.0)
                .totalUnrealizedPnL(Math.round(totalUnrealizedPnL * 100.0) / 100.0)
                .totalUnrealizedPnLPercent(Math.round(totalUnrealizedPnLPercent * 100.0) / 100.0)
                .currency(portfolio.getBaseCurrency())
                .asOf(latestTimestamp)
                .positions(positionDtos)
                .build();
    }
}
