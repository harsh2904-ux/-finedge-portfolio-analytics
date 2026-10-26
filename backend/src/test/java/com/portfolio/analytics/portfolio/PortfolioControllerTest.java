package com.portfolio.analytics.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetRepository;
import com.portfolio.analytics.auth.AuthService;
import com.portfolio.analytics.auth.dto.AuthResponse;
import com.portfolio.analytics.auth.dto.RegisterRequest;
import com.portfolio.analytics.holding.PortfolioHolding;
import com.portfolio.analytics.holding.PortfolioHoldingRepository;
import com.portfolio.analytics.market.MarketPrice;
import com.portfolio.analytics.market.MarketPriceRepository;
import com.portfolio.analytics.portfolio.dto.CreatePortfolioRequest;
import com.portfolio.analytics.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioHoldingRepository holdingRepository;

    @Autowired
    private MarketPriceRepository marketPriceRepository;

    private String tokenUserA;
    private String tokenUserB;

    @BeforeEach
    void setup() {
        marketPriceRepository.deleteAll();
        holdingRepository.deleteAll();
        portfolioRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        AuthResponse authA = authService.register(RegisterRequest.builder()
                .name("User A")
                .email("usera@example.com")
                .password("password123")
                .build());
        tokenUserA = "Bearer " + authA.getAccessToken();

        AuthResponse authB = authService.register(RegisterRequest.builder()
                .name("User B")
                .email("userb@example.com")
                .password("password123")
                .build());
        tokenUserB = "Bearer " + authB.getAccessToken();
    }

    @Test
    void testCreateAndGetPortfolio() throws Exception {
        CreatePortfolioRequest createReq = CreatePortfolioRequest.builder()
                .name("Tech Growth 2026")
                .baseCurrency("USD")
                .build();

        String responseStr = mockMvc.perform(post("/api/portfolios")
                        .header("Authorization", tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Tech Growth 2026")))
                .andReturn().getResponse().getContentAsString();

        Long portfolioId = objectMapper.readTree(responseStr).path("data").path("id").asLong();

        // Retrieve by User A -> 200 OK
        mockMvc.perform(get("/api/portfolios/" + portfolioId)
                        .header("Authorization", tokenUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(portfolioId.intValue())));
    }

    @Test
    void testCrossUserAuthorizationDenied() throws Exception {
        CreatePortfolioRequest createReq = CreatePortfolioRequest.builder()
                .name("User A Secret Portfolio")
                .baseCurrency("USD")
                .build();

        String responseStr = mockMvc.perform(post("/api/portfolios")
                        .header("Authorization", tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long portfolioId = objectMapper.readTree(responseStr).path("data").path("id").asLong();

        // User B tries to access User A's portfolio -> 403 Forbidden
        mockMvc.perform(get("/api/portfolios/" + portfolioId)
                        .header("Authorization", tokenUserB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error.code", is("ACCESS_DENIED")));
    }

    @Test
    void testPortfolioValuationCalculation() throws Exception {
        Asset asset = assetRepository.save(Asset.builder()
                .symbol("AAPL")
                .providerSymbol("AAPL")
                .name("Apple Inc.")
                .assetClass("EQUITY")
                .currency("USD")
                .isActive(true)
                .build());

        marketPriceRepository.save(MarketPrice.builder()
                .asset(asset)
                .timestamp(Instant.now().minusSeconds(86400))
                .open(200.0).high(205.0).low(199.0).close(202.0).volume(100000.0)
                .build());

        marketPriceRepository.save(MarketPrice.builder()
                .asset(asset)
                .timestamp(Instant.now())
                .open(202.0).high(210.0).low(201.0).close(208.0).volume(120000.0)
                .build());

        CreatePortfolioRequest createReq = CreatePortfolioRequest.builder()
                .name("Valuation Test Portfolio")
                .baseCurrency("USD")
                .build();

        String portResp = mockMvc.perform(post("/api/portfolios")
                        .header("Authorization", tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long portfolioId = objectMapper.readTree(portResp).path("data").path("id").asLong();
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow();

        holdingRepository.save(PortfolioHolding.builder()
                .portfolio(portfolio)
                .asset(asset)
                .quantity(10.0)
                .averageCost(190.0)
                .purchaseDate(Instant.now())
                .build());

        // Valuation: 10 * 208.0 = 2080.0, CostBasis: 10 * 190.0 = 1900.0, Unrealized: 180.0
        mockMvc.perform(get("/api/portfolios/" + portfolioId + "/valuation")
                        .header("Authorization", tokenUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.portfolioValue", is(2080.0)))
                .andExpect(jsonPath("$.data.totalCostBasis", is(1900.0)))
                .andExpect(jsonPath("$.data.totalUnrealizedPnL", is(180.0)))
                .andExpect(jsonPath("$.data.positions[0].symbol", is("AAPL")))
                .andExpect(jsonPath("$.data.positions[0].marketValue", is(2080.0)))
                .andExpect(jsonPath("$.data.positions[0].weight", is(1.0)));
    }
}
