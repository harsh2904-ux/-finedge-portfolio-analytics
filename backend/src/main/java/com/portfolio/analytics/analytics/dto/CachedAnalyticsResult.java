package com.portfolio.analytics.analytics.dto;

import java.time.Instant;

public class CachedAnalyticsResult<T> {

    private Instant calculatedAt = Instant.now();
    private Instant dataAsOf;
    private T result;

    public CachedAnalyticsResult() {
    }

    public CachedAnalyticsResult(Instant calculatedAt, Instant dataAsOf, T result) {
        this.calculatedAt = calculatedAt != null ? calculatedAt : Instant.now();
        this.dataAsOf = dataAsOf;
        this.result = result;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> CachedAnalyticsResult<T> of(T result) {
        return CachedAnalyticsResult.<T>builder()
                .calculatedAt(Instant.now())
                .dataAsOf(Instant.now())
                .result(result)
                .build();
    }

    public static <T> CachedAnalyticsResult<T> of(T result, Instant dataAsOf) {
        return CachedAnalyticsResult.<T>builder()
                .calculatedAt(Instant.now())
                .dataAsOf(dataAsOf)
                .result(result)
                .build();
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Instant getDataAsOf() {
        return dataAsOf;
    }

    public void setDataAsOf(Instant dataAsOf) {
        this.dataAsOf = dataAsOf;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static class Builder<T> {
        private Instant calculatedAt = Instant.now();
        private Instant dataAsOf;
        private T result;

        public Builder<T> calculatedAt(Instant calculatedAt) {
            this.calculatedAt = calculatedAt;
            return this;
        }

        public Builder<T> dataAsOf(Instant dataAsOf) {
            this.dataAsOf = dataAsOf;
            return this;
        }

        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        public CachedAnalyticsResult<T> build() {
            return new CachedAnalyticsResult<>(calculatedAt, dataAsOf, result);
        }
    }
}
