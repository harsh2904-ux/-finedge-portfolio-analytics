package com.portfolio.analytics.analytics.dto;

import java.util.List;

public class ScenarioResultDto {

    private boolean hypothetical;
    private Double currentValue;
    private Double scenarioValue;
    private Double absoluteChange;
    private Double percentageChange;
    private List<AssetImpactDto> assetImpacts;

    public ScenarioResultDto() {
    }

    public ScenarioResultDto(boolean hypothetical, Double currentValue, Double scenarioValue,
                             Double absoluteChange, Double percentageChange, List<AssetImpactDto> assetImpacts) {
        this.hypothetical = hypothetical;
        this.currentValue = currentValue;
        this.scenarioValue = scenarioValue;
        this.absoluteChange = absoluteChange;
        this.percentageChange = percentageChange;
        this.assetImpacts = assetImpacts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isHypothetical() {
        return hypothetical;
    }

    public void setHypothetical(boolean hypothetical) {
        this.hypothetical = hypothetical;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getScenarioValue() {
        return scenarioValue;
    }

    public void setScenarioValue(Double scenarioValue) {
        this.scenarioValue = scenarioValue;
    }

    public Double getAbsoluteChange() {
        return absoluteChange;
    }

    public void setAbsoluteChange(Double absoluteChange) {
        this.absoluteChange = absoluteChange;
    }

    public Double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(Double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public List<AssetImpactDto> getAssetImpacts() {
        return assetImpacts;
    }

    public void setAssetImpacts(List<AssetImpactDto> assetImpacts) {
        this.assetImpacts = assetImpacts;
    }

    public static class Builder {
        private boolean hypothetical;
        private Double currentValue;
        private Double scenarioValue;
        private Double absoluteChange;
        private Double percentageChange;
        private List<AssetImpactDto> assetImpacts;

        public Builder hypothetical(boolean hypothetical) {
            this.hypothetical = hypothetical;
            return this;
        }

        public Builder currentValue(Double currentValue) {
            this.currentValue = currentValue;
            return this;
        }

        public Builder scenarioValue(Double scenarioValue) {
            this.scenarioValue = scenarioValue;
            return this;
        }

        public Builder absoluteChange(Double absoluteChange) {
            this.absoluteChange = absoluteChange;
            return this;
        }

        public Builder percentageChange(Double percentageChange) {
            this.percentageChange = percentageChange;
            return this;
        }

        public Builder assetImpacts(List<AssetImpactDto> assetImpacts) {
            this.assetImpacts = assetImpacts;
            return this;
        }

        public ScenarioResultDto build() {
            return new ScenarioResultDto(hypothetical, currentValue, scenarioValue,
                    absoluteChange, percentageChange, assetImpacts);
        }
    }

    public static class AssetImpactDto {
        private String symbol;
        private String assetClass;
        private Double originalValue;
        private Double shockPercentage;
        private Double shockedValue;
        private Double pnl;

        public AssetImpactDto() {
        }

        public AssetImpactDto(String symbol, String assetClass, Double originalValue,
                              Double shockPercentage, Double shockedValue, Double pnl) {
            this.symbol = symbol;
            this.assetClass = assetClass;
            this.originalValue = originalValue;
            this.shockPercentage = shockPercentage;
            this.shockedValue = shockedValue;
            this.pnl = pnl;
        }

        public static AssetImpactDtoBuilder builder() {
            return new AssetImpactDtoBuilder();
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getAssetClass() {
            return assetClass;
        }

        public void setAssetClass(String assetClass) {
            this.assetClass = assetClass;
        }

        public Double getOriginalValue() {
            return originalValue;
        }

        public void setOriginalValue(Double originalValue) {
            this.originalValue = originalValue;
        }

        public Double getShockPercentage() {
            return shockPercentage;
        }

        public void setShockPercentage(Double shockPercentage) {
            this.shockPercentage = shockPercentage;
        }

        public Double getShockedValue() {
            return shockedValue;
        }

        public void setShockedValue(Double shockedValue) {
            this.shockedValue = shockedValue;
        }

        public Double getPnL() {
            return pnl;
        }

        public Double getPnl() {
            return pnl;
        }

        public void setPnl(Double pnl) {
            this.pnl = pnl;
        }

        public static class AssetImpactDtoBuilder {
            private String symbol;
            private String assetClass;
            private Double originalValue;
            private Double shockPercentage;
            private Double shockedValue;
            private Double pnl;

            public AssetImpactDtoBuilder symbol(String symbol) {
                this.symbol = symbol;
                return this;
            }

            public AssetImpactDtoBuilder assetClass(String assetClass) {
                this.assetClass = assetClass;
                return this;
            }

            public AssetImpactDtoBuilder originalValue(Double originalValue) {
                this.originalValue = originalValue;
                return this;
            }

            public AssetImpactDtoBuilder shockPercentage(Double shockPercentage) {
                this.shockPercentage = shockPercentage;
                return this;
            }

            public AssetImpactDtoBuilder shockedValue(Double shockedValue) {
                this.shockedValue = shockedValue;
                return this;
            }

            public AssetImpactDtoBuilder pnl(Double pnl) {
                this.pnl = pnl;
                return this;
            }

            public AssetImpactDto build() {
                return new AssetImpactDto(symbol, assetClass, originalValue, shockPercentage, shockedValue, pnl);
            }
        }
    }
}
