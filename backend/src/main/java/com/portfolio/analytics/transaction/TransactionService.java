package com.portfolio.analytics.transaction;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetRepository;
import com.portfolio.analytics.exception.BusinessValidationException;
import com.portfolio.analytics.exception.ResourceNotFoundException;
import com.portfolio.analytics.holding.PortfolioHolding;
import com.portfolio.analytics.holding.PortfolioHoldingRepository;
import com.portfolio.analytics.portfolio.Portfolio;
import com.portfolio.analytics.portfolio.PortfolioService;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.transaction.dto.CreateTransactionRequest;
import com.portfolio.analytics.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TransactionService.class);


    private final TransactionRepository transactionRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final PortfolioService portfolioService;
    private final AssetRepository assetRepository;
    public TransactionService(TransactionRepository transactionRepository, PortfolioHoldingRepository holdingRepository, PortfolioService portfolioService, AssetRepository assetRepository) {
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
        this.assetRepository = assetRepository;
    }


    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long portfolioId, UserPrincipal principal) {
        Portfolio portfolio = portfolioService.getPortfolioEntityById(portfolioId, principal);
        List<Transaction> list = transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolio.getId());
        return list.stream().map(TransactionResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long portfolioId, Long id, UserPrincipal principal) {
        portfolioService.getPortfolioEntityById(portfolioId, principal);
        Transaction tx = transactionRepository.findByIdAndPortfolioId(id, portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return TransactionResponse.fromEntity(tx);
    }

    @Transactional
    public TransactionResponse recordTransaction(Long portfolioId, CreateTransactionRequest request, UserPrincipal principal) {
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

        TransactionType type = request.getTransactionType();
        double qty = request.getQuantity();
        double price = request.getPrice();

        Optional<PortfolioHolding> optHolding = holdingRepository.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId());

        if (type == TransactionType.BUY) {
            if (optHolding.isPresent()) {
                PortfolioHolding holding = optHolding.get();
                double oldTotalCost = holding.getQuantity() * holding.getAverageCost();
                double newBuyCost = qty * price;
                double totalQty = holding.getQuantity() + qty;
                double newAvgCost = (oldTotalCost + newBuyCost) / totalQty;

                holding.setQuantity(totalQty);
                holding.setAverageCost(Math.round(newAvgCost * 10000.0) / 10000.0);
                holdingRepository.save(holding);
            } else {
                PortfolioHolding newHolding = PortfolioHolding.builder()
                        .portfolio(portfolio)
                        .asset(asset)
                        .quantity(qty)
                        .averageCost(price)
                        .purchaseDate(request.getTransactionDate() != null ? request.getTransactionDate() : Instant.now())
                        .build();
                holdingRepository.save(newHolding);
            }
        } else if (type == TransactionType.SELL) {
            if (optHolding.isEmpty()) {
                throw new BusinessValidationException("Cannot sell asset " + asset.getSymbol() + " as the portfolio does not hold any shares.");
            }
            PortfolioHolding holding = optHolding.get();
            if (holding.getQuantity() < qty) {
                throw new BusinessValidationException(String.format(
                        "Cannot sell %f shares of %s. Portfolio only owns %f shares.",
                        qty, asset.getSymbol(), holding.getQuantity()
                ));
            }

            double remaining = holding.getQuantity() - qty;
            if (remaining <= 0.000001) {
                holdingRepository.delete(holding);
            } else {
                holding.setQuantity(remaining);
                holdingRepository.save(holding);
            }
        }

        Transaction tx = Transaction.builder()
                .portfolio(portfolio)
                .asset(asset)
                .transactionType(type)
                .quantity(qty)
                .price(price)
                .transactionCost(request.getTransactionCost() != null ? request.getTransactionCost() : 0.0)
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : Instant.now())
                .currency(StringUtils.hasText(request.getCurrency()) ? request.getCurrency().toUpperCase() : portfolio.getBaseCurrency())
                .build();

        Transaction saved = transactionRepository.save(tx);
        log.info("Recorded transaction ID={} type={} symbol={} qty={} portfolio ID={}",
                saved.getId(), type, asset.getSymbol(), qty, portfolio.getId());
        return TransactionResponse.fromEntity(saved);
    }
}
