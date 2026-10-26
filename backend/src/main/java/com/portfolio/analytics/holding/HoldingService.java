package com.portfolio.analytics.holding;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetRepository;
import com.portfolio.analytics.exception.BusinessValidationException;
import com.portfolio.analytics.exception.DuplicateResourceException;
import com.portfolio.analytics.exception.ResourceNotFoundException;
import com.portfolio.analytics.holding.dto.CreateHoldingRequest;
import com.portfolio.analytics.holding.dto.HoldingResponse;
import com.portfolio.analytics.holding.dto.UpdateHoldingRequest;
import com.portfolio.analytics.portfolio.Portfolio;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
public class HoldingService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HoldingService.class);


    private final PortfolioHoldingRepository holdingRepository;
    private final PortfolioService portfolioService;
    private final AssetRepository assetRepository;
    public HoldingService(PortfolioHoldingRepository holdingRepository, PortfolioService portfolioService, AssetRepository assetRepository) {
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
        this.assetRepository = assetRepository;
    }


    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldings(Long portfolioId, UserPrincipal principal) {
        Portfolio portfolio = portfolioService.getPortfolioEntityById(portfolioId, principal);
        List<PortfolioHolding> list = holdingRepository.findByPortfolioId(portfolio.getId());
        return list.stream().map(HoldingResponse::fromEntity).toList();
    }

    @Transactional
    public HoldingResponse addHolding(Long portfolioId, CreateHoldingRequest request, UserPrincipal principal) {
        Portfolio portfolio = portfolioService.getPortfolioEntityById(portfolioId, principal);

        Asset asset = null;
        if (request.getAssetId() != null) {
            asset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + request.getAssetId()));
        } else if (StringUtils.hasText(request.getSymbol())) {
            asset = assetRepository.findBySymbol(request.getSymbol().trim().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found with symbol: " + request.getSymbol()));
        } else {
            throw new BusinessValidationException("Either assetId or symbol must be provided.");
        }

        if (holdingRepository.existsByPortfolioIdAndAssetId(portfolio.getId(), asset.getId())) {
            throw new DuplicateResourceException("Holding already exists for symbol: " + asset.getSymbol() + ". Use update or transaction instead.");
        }

        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio)
                .asset(asset)
                .quantity(request.getQuantity())
                .averageCost(request.getAverageCost())
                .targetWeight(request.getTargetWeight())
                .purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : Instant.now())
                .build();

        PortfolioHolding saved = holdingRepository.save(holding);
        log.info("Added holding ID={} symbol={} to portfolio ID={}", saved.getId(), asset.getSymbol(), portfolio.getId());
        return HoldingResponse.fromEntity(saved);
    }

    @Transactional
    public HoldingResponse updateHolding(Long portfolioId, Long holdingId, UpdateHoldingRequest request, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal); // validates portfolio ownership

        PortfolioHolding holding = holdingRepository.findByIdAndPortfolioId(holdingId, portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with ID: " + holdingId));

        if (request.getQuantity() != null) {
            holding.setQuantity(request.getQuantity());
        }
        if (request.getAverageCost() != null) {
            holding.setAverageCost(request.getAverageCost());
        }
        if (request.getTargetWeight() != null) {
            holding.setTargetWeight(request.getTargetWeight());
        }

        PortfolioHolding updated = holdingRepository.save(holding);
        log.info("Updated holding ID={}", updated.getId());
        return HoldingResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteHolding(Long portfolioId, Long holdingId, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);

        PortfolioHolding holding = holdingRepository.findByIdAndPortfolioId(holdingId, portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with ID: " + holdingId));

        holdingRepository.delete(holding);
        log.info("Deleted holding ID={} from portfolio ID={}", holdingId, portfolioId);
    }
}
