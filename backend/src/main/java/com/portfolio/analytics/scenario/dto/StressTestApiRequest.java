package com.portfolio.analytics.scenario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class StressTestApiRequest {

    @Schema(description = "List of scenario names to execute (leave empty to execute all)",
            example = "[\"2008 Global Financial Crisis\", \"2020 COVID-19 Liquidity Shock\", \"Tech Stagflation Shock\"]")
    private List<String> scenarios;

    public StressTestApiRequest() {
    }

    public StressTestApiRequest(List<String> scenarios) {
        this.scenarios = scenarios;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<String> scenarios) {
        this.scenarios = scenarios;
    }

    public static class Builder {
        private List<String> scenarios;

        public Builder scenarios(List<String> scenarios) {
            this.scenarios = scenarios;
            return this;
        }

        public StressTestApiRequest build() {
            return new StressTestApiRequest(scenarios);
        }
    }
}
