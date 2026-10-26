package com.portfolio.analytics.risk;

import com.portfolio.analytics.analytics.dto.DrawdownDto;
import com.portfolio.analytics.analytics.dto.RiskMetricsDto;
import com.portfolio.analytics.analytics.dto.VarDto;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{id}/risk")
@Tag(name = "Risk", description = "Endpoints for institutional risk analytics (Volatility, Sharpe, Sortino, VaR, CVaR, Drawdown)")
@SecurityRequirement(name = "Bearer Authentication")
public class RiskController {

    private final RiskService riskService;
    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }


    @GetMapping
    @Operation(summary = "Get portfolio quantitative risk metrics")
    public ResponseEntity<ApiResponse<RiskMetricsDto>> getRisk(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        RiskMetricsDto response = riskService.getRisk(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get historical risk metrics")
    public ResponseEntity<ApiResponse<RiskMetricsDto>> getRiskHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        RiskMetricsDto response = riskService.getRisk(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/drawdown")
    @Operation(summary = "Get peak-to-trough drawdown analysis and maximum drawdown metrics")
    public ResponseEntity<ApiResponse<DrawdownDto>> getDrawdown(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        DrawdownDto response = riskService.getDrawdown(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/var")
    @Operation(summary = "Get Historical and Parametric Value at Risk (VaR) and Expected Shortfall")
    public ResponseEntity<ApiResponse<VarDto>> getVaR(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @RequestParam(defaultValue = "0.95") Double confidence,
            @AuthenticationPrincipal UserPrincipal principal) {
        VarDto response = riskService.getVaR(id, lookback, confidence, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
