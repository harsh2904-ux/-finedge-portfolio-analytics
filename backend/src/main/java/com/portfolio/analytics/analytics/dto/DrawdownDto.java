package com.portfolio.analytics.analytics.dto;

import java.util.List;

public class DrawdownDto {

    private Double maxDrawdown;
    private String peakDate;
    private String troughDate;
    private String recoveryDate;
    private List<DrawdownPoint> history;

    public DrawdownDto() {
    }

    public DrawdownDto(Double maxDrawdown, String peakDate, String troughDate,
                       String recoveryDate, List<DrawdownPoint> history) {
        this.maxDrawdown = maxDrawdown;
        this.peakDate = peakDate;
        this.troughDate = troughDate;
        this.recoveryDate = recoveryDate;
        this.history = history;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(Double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public String getPeakDate() {
        return peakDate;
    }

    public void setPeakDate(String peakDate) {
        this.peakDate = peakDate;
    }

    public String getTroughDate() {
        return troughDate;
    }

    public void setTroughDate(String troughDate) {
        this.troughDate = troughDate;
    }

    public String getRecoveryDate() {
        return recoveryDate;
    }

    public void setRecoveryDate(String recoveryDate) {
        this.recoveryDate = recoveryDate;
    }

    public List<DrawdownPoint> getHistory() {
        return history;
    }

    public void setHistory(List<DrawdownPoint> history) {
        this.history = history;
    }

    public static class Builder {
        private Double maxDrawdown;
        private String peakDate;
        private String troughDate;
        private String recoveryDate;
        private List<DrawdownPoint> history;

        public Builder maxDrawdown(Double maxDrawdown) {
            this.maxDrawdown = maxDrawdown;
            return this;
        }

        public Builder peakDate(String peakDate) {
            this.peakDate = peakDate;
            return this;
        }

        public Builder troughDate(String troughDate) {
            this.troughDate = troughDate;
            return this;
        }

        public Builder recoveryDate(String recoveryDate) {
            this.recoveryDate = recoveryDate;
            return this;
        }

        public Builder history(List<DrawdownPoint> history) {
            this.history = history;
            return this;
        }

        public DrawdownDto build() {
            return new DrawdownDto(maxDrawdown, peakDate, troughDate, recoveryDate, history);
        }
    }

    public static class DrawdownPoint {
        private String date;
        private Double drawdown;

        public DrawdownPoint() {
        }

        public DrawdownPoint(String date, Double drawdown) {
            this.date = date;
            this.drawdown = drawdown;
        }

        public static DrawdownPointBuilder builder() {
            return new DrawdownPointBuilder();
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Double getDrawdown() {
            return drawdown;
        }

        public void setDrawdown(Double drawdown) {
            this.drawdown = drawdown;
        }

        public static class DrawdownPointBuilder {
            private String date;
            private Double drawdown;

            public DrawdownPointBuilder date(String date) {
                this.date = date;
                return this;
            }

            public DrawdownPointBuilder drawdown(Double drawdown) {
                this.drawdown = drawdown;
                return this;
            }

            public DrawdownPoint build() {
                return new DrawdownPoint(date, drawdown);
            }
        }
    }
}
