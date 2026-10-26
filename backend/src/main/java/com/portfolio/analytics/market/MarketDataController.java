package com.portfolio.analytics.market;

import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.market.dto.LatestPriceResponse;
import com.portfolio.analytics.market.dto.MarketHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/market")
@Tag(name = "Market Data", description = "Endpoints for querying latest quotes and historical OHLCV market data")
@SecurityRequirement(name = "Bearer Authentication")
public class MarketDataController {

    private final MarketDataService marketDataService;
    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }


    @GetMapping("/{symbol}")
    @Operation(summary = "Get current market quote for symbol")
    public ResponseEntity<ApiResponse<LatestPriceResponse>> getMarketQuote(@PathVariable String symbol) {
        LatestPriceResponse response = marketDataService.getLatestPrice(symbol);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/{symbol}/latest")
    @Operation(summary = "Get latest price for symbol")
    public ResponseEntity<ApiResponse<LatestPriceResponse>> getLatestPrice(@PathVariable String symbol) {
        LatestPriceResponse response = marketDataService.getLatestPrice(symbol);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/{symbol}/history")
    @Operation(summary = "Get historical OHLCV series for symbol with date range and pagination")
    public ResponseEntity<ApiResponse<MarketHistoryResponse>> getHistory(
            @PathVariable String symbol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @PageableDefault(size = 100, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        MarketHistoryResponse response = marketDataService.getHistory(symbol, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
