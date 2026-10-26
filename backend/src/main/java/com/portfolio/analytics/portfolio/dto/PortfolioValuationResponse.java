package com.portfolio.analytics.portfolio.dto;

import java.time.Instant;
import java.util.List;

public class PortfolioValuationResponse {

    private Long portfolioId;
    private String portfolioName;
    private Double portfolioValue;
    private Double dailyPnL;
    private Double dailyReturn;
    private Double totalCostBasis;
    private Double totalUnrealizedPnL;
    private Double totalUnrealizedPnLPercent;
    private String currency;
    private Instant asOf;
    private List<PositionValuationDto> positions;

    public PortfolioValuationResponse() {
    }

    public PortfolioValuationResponse(Long portfolioId, String portfolioName, Double portfolioValue,
                                      Double dailyPnL, Double dailyReturn, Double totalCostBasis,
                                      Double totalUnrealizedPnL, Double totalUnrealizedPnLPercent,
                                      String currency, Instant asOf, List<PositionValuationDto> positions) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.portfolioValue = portfolioValue;
        this.dailyPnL = dailyPnL;
        this.dailyReturn = dailyReturn;
        this.totalCostBasis = totalCostBasis;
        this.totalUnrealizedPnL = totalUnrealizedPnL;
        this.totalUnrealizedPnLPercent = totalUnrealizedPnLPercent;
        this.currency = currency;
        this.asOf = asOf;
        this.positions = positions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public Double getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(Double portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public Double getDailyPnL() {
        return dailyPnL;
    }

    public void setDailyPnL(Double dailyPnL) {
        this.dailyPnL = dailyPnL;
    }

    public Double getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(Double dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public Double totalCostBasis() {
        return totalCostBasis;
    }

    public Double getTotalCostBasis() {
        return totalCostBasis;
    }

    public void setTotalCostBasis(Double totalCostBasis) {
        this.totalCostBasis = totalCostBasis;
    }

    public Double getTotalUnrealizedPnL() {
        return totalUnrealizedPnL;
    }

    public void setTotalUnrealizedPnL(Double totalUnrealizedPnL) {
        this.totalUnrealizedPnL = totalUnrealizedPnL;
    }

    public Double getTotalUnrealizedPnLPercent() {
        return totalUnrealizedPnLPercent;
    }

    public void setTotalUnrealizedPnLPercent(Double totalUnrealizedPnLPercent) {
        this.totalUnrealizedPnLPercent = totalUnrealizedPnLPercent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getAsOf() {
        return asOf;
    }

    public void setAsOf(Instant asOf) {
        this.asOf = asOf;
    }

    public List<PositionValuationDto> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionValuationDto> positions) {
        this.positions = positions;
    }

    public static class Builder {
        private Long portfolioId;
        private String portfolioName;
        private Double portfolioValue;
        private Double dailyPnL;
        private Double dailyReturn;
        private Double totalCostBasis;
        private Double totalUnrealizedPnL;
        private Double totalUnrealizedPnLPercent;
        private String currency;
        private Instant asOf;
        private List<PositionValuationDto> positions;

        public Builder portfolioId(Long portfolioId) {
            this.portfolioId = portfolioId;
            return this;
        }

        public Builder portfolioName(String portfolioName) {
            this.portfolioName = portfolioName;
            return this;
        }

        public Builder portfolioValue(Double portfolioValue) {
            this.portfolioValue = portfolioValue;
            return this;
        }

        public Builder dailyPnL(Double dailyPnL) {
            this.dailyPnL = dailyPnL;
            return this;
        }

        public Builder dailyReturn(Double dailyReturn) {
            this.dailyReturn = dailyReturn;
            return this;
        }

        public Builder totalCostBasis(Double totalCostBasis) {
            this.totalCostBasis = totalCostBasis;
            return this;
        }

        public Builder totalUnrealizedPnL(Double totalUnrealizedPnL) {
            this.totalUnrealizedPnL = totalUnrealizedPnL;
            return this;
        }

        public Builder totalUnrealizedPnLPercent(Double totalUnrealizedPnLPercent) {
            this.totalUnrealizedPnLPercent = totalUnrealizedPnLPercent;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder asOf(Instant asOf) {
            this.asOf = asOf;
            return this;
        }

        public Builder positions(List<PositionValuationDto> positions) {
            this.positions = positions;
            return this;
        }

        public PortfolioValuationResponse build() {
            return new PortfolioValuationResponse(portfolioId, portfolioName, portfolioValue,
                    dailyPnL, dailyReturn, totalCostBasis, totalUnrealizedPnL,
                    totalUnrealizedPnLPercent, currency, asOf, positions);
        }
    }

    public static class PositionValuationDto {
        private Long assetId;
        private String symbol;
        private String name;
        private String assetClass;
        private Double quantity;
        private Double averageCost;
        private Double currentPrice;
        private Double marketValue;
        private Double weight;
        private Double costBasis;
        private Double unrealizedPnL;
        private Double unrealizedPnLPercent;
        private Double dailyReturn;

        public PositionValuationDto() {
        }

        public PositionValuationDto(Long assetId, String symbol, String name, String assetClass,
                                    Double quantity, Double averageCost, Double currentPrice,
                                    Double marketValue, Double weight, Double costBasis,
                                    Double unrealizedPnL, Double unrealizedPnLPercent, Double dailyReturn) {
            this.assetId = assetId;
            this.symbol = symbol;
            this.name = name;
            this.assetClass = assetClass;
            this.quantity = quantity;
            this.averageCost = averageCost;
            this.currentPrice = currentPrice;
            this.marketValue = marketValue;
            this.weight = weight;
            this.costBasis = costBasis;
            this.unrealizedPnL = unrealizedPnL;
            this.unrealizedPnLPercent = unrealizedPnLPercent;
            this.dailyReturn = dailyReturn;
        }

        public static PositionValuationDtoBuilder builder() {
            return new PositionValuationDtoBuilder();
        }

        public Long getAssetId() {
            return assetId;
        }

        public void setAssetId(Long assetId) {
            this.assetId = assetId;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAssetClass() {
            return assetClass;
        }

        public void setAssetClass(String assetClass) {
            this.assetClass = assetClass;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public Double getAverageCost() {
            return averageCost;
        }

        public void setAverageCost(Double averageCost) {
            this.averageCost = averageCost;
        }

        public Double getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(Double currentPrice) {
            this.currentPrice = currentPrice;
        }

        public Double getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(Double marketValue) {
            this.marketValue = marketValue;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Double getCostBasis() {
            return costBasis;
        }

        public void setCostBasis(Double costBasis) {
            this.costBasis = costBasis;
        }

        public Double getUnrealizedPnL() {
            return unrealizedPnL;
        }

        public void setUnrealizedPnL(Double unrealizedPnL) {
            this.unrealizedPnL = unrealizedPnL;
        }

        public Double getUnrealizedPnLPercent() {
            return unrealizedPnLPercent;
        }

        public void setUnrealizedPnLPercent(Double unrealizedPnLPercent) {
            this.unrealizedPnLPercent = unrealizedPnLPercent;
        }

        public Double getDailyReturn() {
            return dailyReturn;
        }

        public void setDailyReturn(Double dailyReturn) {
            this.dailyReturn = dailyReturn;
        }

        public static class PositionValuationDtoBuilder {
            private Long assetId;
            private String symbol;
            private String name;
            private String assetClass;
            private Double quantity;
            private Double averageCost;
            private Double currentPrice;
            private Double marketValue;
            private Double weight;
            private Double costBasis;
            private Double unrealizedPnL;
            private Double unrealizedPnLPercent;
            private Double dailyReturn;

            public PositionValuationDtoBuilder assetId(Long assetId) {
                this.assetId = assetId;
                return this;
            }

            public PositionValuationDtoBuilder symbol(String symbol) {
                this.symbol = symbol;
                return this;
            }

            public PositionValuationDtoBuilder name(String name) {
                this.name = name;
                return this;
            }

            public PositionValuationDtoBuilder assetClass(String assetClass) {
                this.assetClass = assetClass;
                return this;
            }

            public PositionValuationDtoBuilder quantity(Double quantity) {
                this.quantity = quantity;
                return this;
            }

            public PositionValuationDtoBuilder averageCost(Double averageCost) {
                this.averageCost = averageCost;
                return this;
            }

            public PositionValuationDtoBuilder currentPrice(Double currentPrice) {
                this.currentPrice = currentPrice;
                return this;
            }

            public PositionValuationDtoBuilder marketValue(Double marketValue) {
                this.marketValue = marketValue;
                return this;
            }

            public PositionValuationDtoBuilder weight(Double weight) {
                this.weight = weight;
                return this;
            }

            public PositionValuationDtoBuilder costBasis(Double costBasis) {
                this.costBasis = costBasis;
                return this;
            }

            public PositionValuationDtoBuilder unrealizedPnL(Double unrealizedPnL) {
                this.unrealizedPnL = unrealizedPnL;
                return this;
            }

            public PositionValuationDtoBuilder unrealizedPnLPercent(Double unrealizedPnLPercent) {
                this.unrealizedPnLPercent = unrealizedPnLPercent;
                return this;
            }

            public PositionValuationDtoBuilder dailyReturn(Double dailyReturn) {
                this.dailyReturn = dailyReturn;
                return this;
            }

            public PositionValuationDto build() {
                return new PositionValuationDto(assetId, symbol, name, assetClass, quantity,
                        averageCost, currentPrice, marketValue, weight, costBasis,
                        unrealizedPnL, unrealizedPnLPercent, dailyReturn);
            }
        }
    }
}
