package com.portfolio.analytics.simulation;

import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.MonteCarloResultDto;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.simulation.dto.MonteCarloApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{id}")
@Tag(name = "Simulation", description = "Endpoints for multivariate Monte Carlo Geometric Brownian Motion forward simulations")
@SecurityRequirement(name = "Bearer Authentication")
public class MonteCarloController {

    private final MonteCarloService monteCarloService;
    public MonteCarloController(MonteCarloService monteCarloService) {
        this.monteCarloService = monteCarloService;
    }


    @PostMapping("/monte-carlo")
    @Operation(summary = "Run correlated Monte Carlo wealth trajectory simulation")
    public ResponseEntity<ApiResponse<CachedAnalyticsResult<MonteCarloResultDto>>> runMonteCarlo(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) MonteCarloApiRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        MonteCarloApiRequest req = request != null ? request : new MonteCarloApiRequest();
        CachedAnalyticsResult<MonteCarloResultDto> response = monteCarloService.simulate(id, req, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
