package com.portfolio.analytics.optimization;

import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.EfficientFrontierResultDto;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.optimization.dto.EfficientFrontierApiRequest;
import com.portfolio.analytics.optimization.dto.OptimizationApiRequest;
import com.portfolio.analytics.optimization.dto.OptimizationApiResponse;
import com.portfolio.analytics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{id}")
@Tag(name = "Optimization", description = "Endpoints for Markowitz Modern Portfolio Theory optimization and Efficient Frontier calculation")
@SecurityRequirement(name = "Bearer Authentication")
public class OptimizationController {

    private final OptimizationService optimizationService;
    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }


    @PostMapping("/optimization")
    @Operation(summary = "Compute optimal portfolio weights (Maximum Sharpe, Minimum Variance, Equal Weight)")
    public ResponseEntity<ApiResponse<CachedAnalyticsResult<OptimizationApiResponse>>> optimize(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) OptimizationApiRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        OptimizationApiRequest req = request != null ? request : new OptimizationApiRequest();
        CachedAnalyticsResult<OptimizationApiResponse> response = optimizationService.optimize(id, req, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/efficient-frontier")
    @Operation(summary = "Generate Markowitz Efficient Frontier curve simulation")
    public ResponseEntity<ApiResponse<CachedAnalyticsResult<EfficientFrontierResultDto>>> getEfficientFrontier(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) EfficientFrontierApiRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        EfficientFrontierApiRequest req = request != null ? request : new EfficientFrontierApiRequest();
        CachedAnalyticsResult<EfficientFrontierResultDto> response = optimizationService.getEfficientFrontier(id, req, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
