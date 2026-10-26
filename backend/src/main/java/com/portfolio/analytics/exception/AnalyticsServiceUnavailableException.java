package com.portfolio.analytics.exception;

public class AnalyticsServiceUnavailableException extends RuntimeException {
    public AnalyticsServiceUnavailableException(String message) {
        super(message);
    }

    public AnalyticsServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
