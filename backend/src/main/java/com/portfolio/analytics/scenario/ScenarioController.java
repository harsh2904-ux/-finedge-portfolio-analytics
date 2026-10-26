package com.portfolio.analytics.scenario;

import com.portfolio.analytics.analytics.dto.CachedAnalyticsResult;
import com.portfolio.analytics.analytics.dto.ScenarioResultDto;
import com.portfolio.analytics.analytics.dto.StressTestResultDto;
import com.portfolio.analytics.common.ApiResponse;
import com.portfolio.analytics.scenario.dto.ScenarioApiRequest;
import com.portfolio.analytics.scenario.dto.StressTestApiRequest;
import com.portfolio.analytics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{id}")
@Tag(name = "Scenarios & Stress Testing", description = "Endpoints for hypothetical macroeconomic shock tests and historical stress simulations")
@SecurityRequirement(name = "Bearer Authentication")
public class ScenarioController {

    private final ScenarioService scenarioService;
    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }


    @PostMapping("/scenario")
    @Operation(summary = "Evaluate hypothetical multi-asset market shock scenario")
    public ResponseEntity<ApiResponse<ScenarioResultDto>> runCustomScenario(
            @PathVariable Long id,
            @Valid @RequestBody ScenarioApiRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ScenarioResultDto response = scenarioService.runCustomScenario(id, request, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/stress-tests")
    @Operation(summary = "Run all predefined institutional stress tests (2008 GFC, COVID-19, Stagflation, Meltup)")
    public ResponseEntity<ApiResponse<CachedAnalyticsResult<List<StressTestResultDto>>>> getStressTests(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        CachedAnalyticsResult<List<StressTestResultDto>> response = scenarioService.runPredefinedStressTests(id, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/stress-tests/run")
    @Operation(summary = "Run targeted stress test scenarios")
    public ResponseEntity<ApiResponse<List<StressTestResultDto>>> runSelectedStressTests(
            @PathVariable Long id,
            @RequestBody(required = false) StressTestApiRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        StressTestApiRequest req = request != null ? request : new StressTestApiRequest();
        List<StressTestResultDto> response = scenarioService.runSelectedStressTests(id, req, principal);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
