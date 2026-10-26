package com.portfolio.analytics.transaction;

import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.security.UserPrincipal;
import com.portfolio.analytics.transaction.dto.CreateTransactionRequest;
import com.portfolio.analytics.transaction.dto.TransactionResponse;
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
@RequestMapping("/api/portfolios/{portfolioId}/transactions")
@Tag(name = "Transactions", description = "Endpoints for trade ledger execution (BUY, SELL, DIVIDEND, FEE)")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping
    @Operation(summary = "Get transaction history for portfolio")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @PathVariable Long portfolioId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TransactionResponse> response = transactionService.getTransactions(portfolioId, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping
    @Operation(summary = "Record a new portfolio transaction", description = "Executes trade and automatically recalculates holdings and average costs.")
    public ResponseEntity<ApiResponse<TransactionResponse>> recordTransaction(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TransactionResponse response = transactionService.recordTransaction(portfolioId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        TransactionResponse response = transactionService.getTransactionById(portfolioId, id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
