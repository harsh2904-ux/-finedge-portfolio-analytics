package com.portfolio.analytics.holding;

import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.holding.dto.CreateHoldingRequest;
import com.portfolio.analytics.holding.dto.HoldingResponse;
import com.portfolio.analytics.holding.dto.UpdateHoldingRequest;
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
@RequestMapping("/api/portfolios/{portfolioId}/holdings")
@Tag(name = "Holdings", description = "Endpoints for managing portfolio asset positions and allocations")
@SecurityRequirement(name = "Bearer Authentication")
public class HoldingController {

    private final HoldingService holdingService;
    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }


    @GetMapping
    @Operation(summary = "List all holdings in portfolio")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getHoldings(
            @PathVariable Long portfolioId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<HoldingResponse> response = holdingService.getHoldings(portfolioId, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping
    @Operation(summary = "Add an asset holding to portfolio")
    public ResponseEntity<ApiResponse<HoldingResponse>> addHolding(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CreateHoldingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        HoldingResponse response = holdingService.addHolding(portfolioId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PutMapping("/{holdingId}")
    @Operation(summary = "Update holding quantity, average cost, or target weight")
    public ResponseEntity<ApiResponse<HoldingResponse>> updateHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId,
            @Valid @RequestBody UpdateHoldingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        HoldingResponse response = holdingService.updateHolding(portfolioId, holdingId, request, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @DeleteMapping("/{holdingId}")
    @Operation(summary = "Remove holding from portfolio")
    public ResponseEntity<ApiResponse<Void>> deleteHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId,
            @AuthenticationPrincipal UserPrincipal principal) {
        holdingService.deleteHolding(portfolioId, holdingId, principal);
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
