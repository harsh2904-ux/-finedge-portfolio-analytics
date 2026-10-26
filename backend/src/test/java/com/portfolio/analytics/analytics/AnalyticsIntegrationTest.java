package com.portfolio.analytics.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.analytics.analytics.client.PythonAnalyticsClient;
import com.portfolio.analytics.analytics.dto.*;
import com.portfolio.analytics.auth.AuthService;
import com.portfolio.analytics.auth.dto.AuthResponse;
import com.portfolio.analytics.auth.dto.RegisterRequest;
import com.portfolio.analytics.exception.AnalyticsServiceUnavailableException;
import com.portfolio.analytics.portfolio.Portfolio;
import com.portfolio.analytics.portfolio.PortfolioRepository;
import com.portfolio.analytics.user.User;
import com.portfolio.analytics.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnalyticsIntegrationTest {

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

    @MockBean
    private PythonAnalyticsClient pythonAnalyticsClient;

    private String userToken;
    private Portfolio portfolio;

    @BeforeEach
    void setup() {
        portfolioRepository.deleteAll();
        userRepository.deleteAll();

        AuthResponse auth = authService.register(RegisterRequest.builder()
                .name("Quant User")
                .email("quant@example.com")
                .password("password123")
                .build());
        userToken = "Bearer " + auth.getAccessToken();

        User user = userRepository.findById(auth.getUser().getId()).orElseThrow();
        portfolio = portfolioRepository.save(Portfolio.builder()
                .user(user)
                .name("Quant Test Portfolio")
                .baseCurrency("USD")
                .build());
    }

    @Test
    void testPerformanceEndpointSuccess() throws Exception {
        PerformanceMetricsDto mockPerf = PerformanceMetricsDto.builder()
                .totalReturn(0.1842)
                .cagr(0.1186)
                .annualizedReturn(0.121)
                .benchmarkReturn(0.153)
                .excessReturn(0.031)
                .trackingError(0.045)
                .build();

        Mockito.when(pythonAnalyticsClient.getPerformance(eq(portfolio.getId()), any()))
                .thenReturn(mockPerf);

        mockMvc.perform(get("/api/portfolios/" + portfolio.getId() + "/performance")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total_return", is(0.1842)))
                .andExpect(jsonPath("$.data.cagr", is(0.1186)));
    }

    @Test
    void testRiskEndpointSuccess() throws Exception {
        RiskMetricsDto mockRisk = RiskMetricsDto.builder()
                .annualizedVolatility(0.1482)
                .sharpeRatio(0.91)
                .sortinoRatio(1.18)
                .beta(0.84)
                .maxDrawdown(-0.1241)
                .dollarVar95(42500.0)
                .dollarExpectedShortfall95(58300.0)
                .build();

        Mockito.when(pythonAnalyticsClient.getRisk(eq(portfolio.getId()), any()))
                .thenReturn(mockRisk);

        mockMvc.perform(get("/api/portfolios/" + portfolio.getId() + "/risk")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.sharpe_ratio", is(0.91)))
                .andExpect(jsonPath("$.data.sortino_ratio", is(1.18)));
    }

    @Test
    void testOptimizationEndpointSuccess() throws Exception {
        OptimizationResultDto mockOpt = OptimizationResultDto.builder()
                .method("Maximum Sharpe")
                .expectedAnnualReturn(0.134)
                .expectedAnnualVolatility(0.127)
                .sharpeRatio(0.96)
                .weights(Map.of("AAPL", 0.6, "MSFT", 0.4))
                .calculatedAt("2026-10-07T10:00:00Z")
                .build();

        Mockito.when(pythonAnalyticsClient.getOptimization(eq(portfolio.getId()), any(), any(), any()))
                .thenReturn(mockOpt);

        mockMvc.perform(post("/api/portfolios/" + portfolio.getId() + "/optimization")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"method\": \"MAX_SHARPE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.method", is("Maximum Sharpe")))
                .andExpect(jsonPath("$.data.result.sharpeRatio", is(0.96)))
                .andExpect(jsonPath("$.data.result.weights", hasSize(2)));
    }

    @Test
    void testPythonAnalyticsServiceUnavailableMapsTo503() throws Exception {
        Mockito.when(pythonAnalyticsClient.getRisk(eq(portfolio.getId()), any()))
                .thenThrow(new AnalyticsServiceUnavailableException("The analytics service is temporarily unavailable."));

        mockMvc.perform(get("/api/portfolios/" + portfolio.getId() + "/risk")
                        .header("Authorization", userToken))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error.code", is("ANALYTICS_SERVICE_UNAVAILABLE")))
                .andExpect(jsonPath("$.error.message", containsString("unavailable")));
    }
}
