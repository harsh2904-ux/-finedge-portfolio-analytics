package com.portfolio.analytics.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private boolean success = false;
    private ErrorDetails error;
    private Instant timestamp = Instant.now();

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(boolean success, ErrorDetails error, Instant timestamp) {
        this.success = success;
        this.error = error;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(false, new ErrorDetails(code, message, null), Instant.now());
    }

    public static ApiErrorResponse of(String code, String message, Map<String, String> validationErrors) {
        return new ApiErrorResponse(false, new ErrorDetails(code, message, validationErrors), Instant.now());
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public ErrorDetails getError() { return error; }
    public void setError(ErrorDetails error) { this.error = error; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success = false;
        private ErrorDetails error;
        private Instant timestamp = Instant.now();

        public Builder success(boolean success) { this.success = success; return this; }
        public Builder error(ErrorDetails error) { this.error = error; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(success, error, timestamp);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private Map<String, String> validationErrors;

        public ErrorDetails() {
        }

        public ErrorDetails(String code, String message, Map<String, String> validationErrors) {
            this.code = code;
            this.message = message;
            this.validationErrors = validationErrors;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }

        public static ErrorDetailsBuilder builder() {
            return new ErrorDetailsBuilder();
        }

        public static class ErrorDetailsBuilder {
            private String code;
            private String message;
            private Map<String, String> validationErrors;

            public ErrorDetailsBuilder code(String code) { this.code = code; return this; }
            public ErrorDetailsBuilder message(String message) { this.message = message; return this; }
            public ErrorDetailsBuilder validationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; return this; }

            public ErrorDetails build() {
                return new ErrorDetails(code, message, validationErrors);
            }
        }
    }
}
