package com.portfolio.analytics.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RiskAttributionDto {

    @JsonProperty("total_portfolio_risk")
    private Double totalPortfolioRisk;

    private List<ComponentRiskDto> contributions;

    public RiskAttributionDto() {
    }

    public RiskAttributionDto(Double totalPortfolioRisk, List<ComponentRiskDto> contributions) {
        this.totalPortfolioRisk = totalPortfolioRisk;
        this.contributions = contributions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getTotalPortfolioRisk() {
        return totalPortfolioRisk;
    }

    public void setTotalPortfolioRisk(Double totalPortfolioRisk) {
        this.totalPortfolioRisk = totalPortfolioRisk;
    }

    public List<ComponentRiskDto> getContributions() {
        return contributions;
    }

    public void setContributions(List<ComponentRiskDto> contributions) {
        this.contributions = contributions;
    }

    public static class Builder {
        private Double totalPortfolioRisk;
        private List<ComponentRiskDto> contributions;

        public Builder totalPortfolioRisk(Double totalPortfolioRisk) {
            this.totalPortfolioRisk = totalPortfolioRisk;
            return this;
        }

        public Builder contributions(List<ComponentRiskDto> contributions) {
            this.contributions = contributions;
            return this;
        }

        public RiskAttributionDto build() {
            return new RiskAttributionDto(totalPortfolioRisk, contributions);
        }
    }

    public static class ComponentRiskDto {
        private String symbol;
        private Double weight;

        @JsonProperty("marginal_risk_contribution")
        private Double marginalRiskContribution;

        @JsonProperty("component_risk_contribution")
        private Double componentRiskContribution;

        @JsonProperty("percent_risk_contribution")
        private Double percentRiskContribution;

        public ComponentRiskDto() {
        }

        public ComponentRiskDto(String symbol, Double weight, Double marginalRiskContribution,
                                Double componentRiskContribution, Double percentRiskContribution) {
            this.symbol = symbol;
            this.weight = weight;
            this.marginalRiskContribution = marginalRiskContribution;
            this.componentRiskContribution = componentRiskContribution;
            this.percentRiskContribution = percentRiskContribution;
        }

        public static ComponentRiskDtoBuilder builder() {
            return new ComponentRiskDtoBuilder();
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Double getMarginalRiskContribution() {
            return marginalRiskContribution;
        }

        public void setMarginalRiskContribution(Double marginalRiskContribution) {
            this.marginalRiskContribution = marginalRiskContribution;
        }

        public Double getComponentRiskContribution() {
            return componentRiskContribution;
        }

        public void setComponentRiskContribution(Double componentRiskContribution) {
            this.componentRiskContribution = componentRiskContribution;
        }

        public Double getPercentRiskContribution() {
            return percentRiskContribution;
        }

        public void setPercentRiskContribution(Double percentRiskContribution) {
            this.percentRiskContribution = percentRiskContribution;
        }

        public static class ComponentRiskDtoBuilder {
            private String symbol;
            private Double weight;
            private Double marginalRiskContribution;
            private Double componentRiskContribution;
            private Double percentRiskContribution;

            public ComponentRiskDtoBuilder symbol(String symbol) {
                this.symbol = symbol;
                return this;
            }

            public ComponentRiskDtoBuilder weight(Double weight) {
                this.weight = weight;
                return this;
            }

            public ComponentRiskDtoBuilder marginalRiskContribution(Double marginalRiskContribution) {
                this.marginalRiskContribution = marginalRiskContribution;
                return this;
            }

            public ComponentRiskDtoBuilder componentRiskContribution(Double componentRiskContribution) {
                this.componentRiskContribution = componentRiskContribution;
                return this;
            }

            public ComponentRiskDtoBuilder percentRiskContribution(Double percentRiskContribution) {
                this.percentRiskContribution = percentRiskContribution;
                return this;
            }

            public ComponentRiskDto build() {
                return new ComponentRiskDto(symbol, weight, marginalRiskContribution,
                        componentRiskContribution, percentRiskContribution);
            }
        }
    }
}
