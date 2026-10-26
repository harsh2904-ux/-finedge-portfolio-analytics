package com.portfolio.analytics.asset;

import com.portfolio.analytics.asset.dto.AssetResponse;
import com.portfolio.analytics.asset.dto.CreateAssetRequest;
import com.portfolio.analytics.asset.dto.UpdateAssetRequest;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets", description = "Endpoints for managing multi-asset universe (Equities, Bonds, Commodities, REITs)")
@SecurityRequirement(name = "Bearer Authentication")
public class AssetController {

    private final AssetService assetService;
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }


    @GetMapping
    @Operation(summary = "List all assets", description = "Filter assets by assetClass, country, exchange with pagination support.")
    public ResponseEntity<ApiResponse<PagedResponse<AssetResponse>>> getAssets(
            @RequestParam(required = false) String assetClass,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String exchange,
            @PageableDefault(size = 20, sort = "symbol", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<AssetResponse> response = assetService.getAssets(assetClass, country, exchange, pageable);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID")
    public ResponseEntity<ApiResponse<AssetResponse>> getAssetById(@PathVariable Long id) {
        AssetResponse response = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new asset (Admin only)")
    public ResponseEntity<ApiResponse<AssetResponse>> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        AssetResponse response = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update asset details (Admin only)")
    public ResponseEntity<ApiResponse<AssetResponse>> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssetRequest request) {
        AssetResponse response = assetService.updateAsset(id, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete asset (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
