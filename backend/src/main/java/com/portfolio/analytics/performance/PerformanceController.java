package com.portfolio.analytics.performance;

import com.portfolio.analytics.analytics.dto.PerformanceMetricsDto;
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
@RequestMapping("/api/portfolios/{id}/performance")
@Tag(name = "Performance", description = "Endpoints for portfolio performance analytics (CAGR, total return, excess return, tracking error)")
@SecurityRequirement(name = "Bearer Authentication")
public class PerformanceController {

    private final PerformanceService performanceService;
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }


    @GetMapping
    @Operation(summary = "Get full quantitative performance metrics for portfolio")
    public ResponseEntity<ApiResponse<PerformanceMetricsDto>> getPerformance(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        PerformanceMetricsDto response = performanceService.getPerformance(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get executive performance summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> response = performanceService.getPerformanceSummary(id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get rolling return and performance history")
    public ResponseEntity<ApiResponse<PerformanceMetricsDto>> getHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        PerformanceMetricsDto response = performanceService.getPerformance(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/benchmark")
    @Operation(summary = "Get benchmark relative performance and tracking error")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBenchmark(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1Y") String lookback,
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> response = performanceService.getBenchmarkComparison(id, lookback, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
