package com.portfolio.analytics.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success = true;
    private T data;
    private Instant timestamp = Instant.now();

    public ApiResponse() {
    }

    public ApiResponse(boolean success, T data, Instant timestamp) {
        this.success = success;
        this.data = data;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, Instant.now());
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success = true;
        private T data;
        private Instant timestamp = Instant.now();

        public Builder<T> success(boolean success) { this.success = success; return this; }
        public Builder<T> data(T data) { this.data = data; return this; }
        public Builder<T> timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, data, timestamp);
        }
    }
}
