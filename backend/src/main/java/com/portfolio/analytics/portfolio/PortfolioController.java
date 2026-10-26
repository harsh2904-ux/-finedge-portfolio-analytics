package com.portfolio.analytics.portfolio;

import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.portfolio.dto.*;
import com.portfolio.analytics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolios", description = "Endpoints for managing investment portfolios and live mark-to-market valuation")
@SecurityRequirement(name = "Bearer Authentication")
public class PortfolioController {

    private final PortfolioService portfolioService;
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }


    @GetMapping
    @Operation(summary = "Get all portfolios for current user")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfolios(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<PortfolioResponse> response = portfolioService.getPortfoliosForUser(principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping
    @Operation(summary = "Create a new portfolio")
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        PortfolioResponse response = portfolioService.createPortfolio(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get portfolio details by ID")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolioById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        PortfolioResponse response = portfolioService.getPortfolioById(id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update portfolio")
    public ResponseEntity<ApiResponse<PortfolioResponse>> updatePortfolio(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePortfolioRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        PortfolioResponse response = portfolioService.updatePortfolio(id, request, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete portfolio")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        portfolioService.deletePortfolio(id, principal);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @GetMapping("/{id}/valuation")
    @Operation(summary = "Get dynamic mark-to-market portfolio valuation and position breakdowns")
    public ResponseEntity<ApiResponse<PortfolioValuationResponse>> getValuation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        PortfolioValuationResponse response = portfolioService.getPortfolioValuation(id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
