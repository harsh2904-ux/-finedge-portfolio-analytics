package com.portfolio.analytics.scenario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

public class ScenarioApiRequest {

    @NotEmpty(message = "Shocks map cannot be empty")
    @Schema(description = "Map of asset classes or asset symbols to decimal shock values",
            example = "{\"EQUITY\": -0.30, \"GOLD\": 0.15, \"BOND\": 0.05, \"REIT\": -0.20}")
    private Map<String, Double> shocks;

    public ScenarioApiRequest() {
    }

    public ScenarioApiRequest(Map<String, Double> shocks) {
        this.shocks = shocks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Double> getShocks() {
        return shocks;
    }

    public void setShocks(Map<String, Double> shocks) {
        this.shocks = shocks;
    }

    public static class Builder {
        private Map<String, Double> shocks;

        public Builder shocks(Map<String, Double> shocks) {
            this.shocks = shocks;
            return this;
        }

        public ScenarioApiRequest build() {
            return new ScenarioApiRequest(shocks);
        }
    }
}
