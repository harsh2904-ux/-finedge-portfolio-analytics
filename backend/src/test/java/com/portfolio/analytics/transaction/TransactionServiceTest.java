package com.portfolio.analytics.transaction;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetRepository;
import com.portfolio.analytics.exception.BusinessValidationException;
import com.portfolio.analytics.holding.PortfolioHolding;
import com.portfolio.analytics.holding.PortfolioHoldingRepository;
import com.portfolio.analytics.portfolio.Portfolio;
import com.portfolio.analytics.portfolio.PortfolioRepository;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.transaction.dto.CreateTransactionRequest;
import com.portfolio.analytics.transaction.dto.TransactionResponse;
import com.portfolio.analytics.user.Role;
import com.portfolio.analytics.user.User;
import com.portfolio.analytics.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioHoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User user;
    private UserPrincipal principal;
    private Portfolio portfolio;
    private Asset asset;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        holdingRepository.deleteAll();
        portfolioRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("Trader Harsh")
                .email("trader@example.com")
                .passwordHash("hashed")
                .role(Role.USER)
                .active(true)
                .build());

        principal = UserPrincipal.create(user);

        asset = assetRepository.save(Asset.builder()
                .symbol("NVDA")
                .providerSymbol("NVDA")
                .name("NVIDIA Corp")
                .assetClass("EQUITY")
                .currency("USD")
                .isActive(true)
                .build());

        portfolio = portfolioRepository.save(Portfolio.builder()
                .user(user)
                .name("Trading Portfolio")
                .baseCurrency("USD")
                .build());
    }

    @Test
    @Transactional
    void testBuyAndRecalculateAverageCost() {
        // 1. Initial Buy: 10 shares @ 100
        CreateTransactionRequest buy1 = CreateTransactionRequest.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.BUY)
                .quantity(10.0)
                .price(100.0)
                .build();
        transactionService.recordTransaction(portfolio.getId(), buy1, principal);

        PortfolioHolding holding1 = holdingRepository.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId()).orElseThrow();
        assertEquals(10.0, holding1.getQuantity());
        assertEquals(100.0, holding1.getAverageCost());

        // 2. Second Buy: 10 shares @ 200 -> Average cost should be 150.0
        CreateTransactionRequest buy2 = CreateTransactionRequest.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.BUY)
                .quantity(10.0)
                .price(200.0)
                .build();
        transactionService.recordTransaction(portfolio.getId(), buy2, principal);

        PortfolioHolding holding2 = holdingRepository.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId()).orElseThrow();
        assertEquals(20.0, holding2.getQuantity());
        assertEquals(150.0, holding2.getAverageCost());
    }

    @Test
    @Transactional
    void testSellReducesHoldingAndRejectsOverselling() {
        // Buy 10 shares
        CreateTransactionRequest buy = CreateTransactionRequest.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.BUY)
                .quantity(10.0)
                .price(100.0)
                .build();
        transactionService.recordTransaction(portfolio.getId(), buy, principal);

        // Sell 4 shares -> 6 remaining
        CreateTransactionRequest sell4 = CreateTransactionRequest.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.SELL)
                .quantity(4.0)
                .price(120.0)
                .build();
        TransactionResponse txResp = transactionService.recordTransaction(portfolio.getId(), sell4, principal);
        assertNotNull(txResp);
        assertEquals(TransactionType.SELL, txResp.getTransactionType());

        PortfolioHolding holding = holdingRepository.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId()).orElseThrow();
        assertEquals(6.0, holding.getQuantity());

        // Try to sell 10 shares when only 6 are held -> BusinessValidationException!
        CreateTransactionRequest oversell = CreateTransactionRequest.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.SELL)
                .quantity(10.0)
                .price(130.0)
                .build();

        assertThrows(BusinessValidationException.class, () ->
                transactionService.recordTransaction(portfolio.getId(), oversell, principal));
    }
}
