package com.portfolio.analytics.diversification;

import com.portfolio.analytics.analytics.dto.CorrelationDto;
import com.portfolio.analytics.analytics.dto.RiskAttributionDto;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/portfolios/{id}")
@Tag(name = "Diversification", description = "Endpoints for asset allocation weights, correlation matrix, and Euler risk contribution")
@SecurityRequirement(name = "Bearer Authentication")
public class DiversificationController {

    private final DiversificationService diversificationService;
    public DiversificationController(DiversificationService diversificationService) {
        this.diversificationService = diversificationService;
    }


    @GetMapping("/allocation")
    @Operation(summary = "Get multi-asset portfolio allocation weights and valuations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllocation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> response = diversificationService.getAllocation(id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/correlation")
    @Operation(summary = "Get cross-asset correlation and covariance matrix")
    public ResponseEntity<ApiResponse<CorrelationDto>> getCorrelation(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        CorrelationDto response = diversificationService.getCorrelation(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/risk-contribution")
    @Operation(summary = "Get Euler marginal and component risk decomposition (summing to 100%)")
    public ResponseEntity<ApiResponse<RiskAttributionDto>> getRiskContribution(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        RiskAttributionDto response = diversificationService.getRiskContribution(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
