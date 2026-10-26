"""
Automated DTO converter from Lombok to Pure Java POJOs
"""

import os
import re

DTO_FILES = [
    "backend/src/main/java/com/portfolio/analytics/common/ApiResponse.java",
    "backend/src/main/java/com/portfolio/analytics/common/ApiErrorResponse.java",
    "backend/src/main/java/com/portfolio/analytics/common/PagedResponse.java",
    "backend/src/main/java/com/portfolio/analytics/auth/dto/RegisterRequest.java",
    "backend/src/main/java/com/portfolio/analytics/auth/dto/LoginRequest.java",
    "backend/src/main/java/com/portfolio/analytics/auth/dto/RefreshTokenRequest.java",
    "backend/src/main/java/com/portfolio/analytics/auth/dto/UserResponse.java",
    "backend/src/main/java/com/portfolio/analytics/auth/dto/AuthResponse.java",
    "backend/src/main/java/com/portfolio/analytics/asset/dto/CreateAssetRequest.java",
    "backend/src/main/java/com/portfolio/analytics/asset/dto/UpdateAssetRequest.java",
    "backend/src/main/java/com/portfolio/analytics/asset/dto/AssetResponse.java",
    "backend/src/main/java/com/portfolio/analytics/market/dto/MarketPriceDto.java",
    "backend/src/main/java/com/portfolio/analytics/market/dto/LatestPriceResponse.java",
    "backend/src/main/java/com/portfolio/analytics/market/dto/MarketHistoryResponse.java",
    "backend/src/main/java/com/portfolio/analytics/portfolio/dto/CreatePortfolioRequest.java",
    "backend/src/main/java/com/portfolio/analytics/portfolio/dto/UpdatePortfolioRequest.java",
    "backend/src/main/java/com/portfolio/analytics/portfolio/dto/PortfolioResponse.java",
    "backend/src/main/java/com/portfolio/analytics/portfolio/dto/PortfolioValuationResponse.java",
    "backend/src/main/java/com/portfolio/analytics/holding/dto/CreateHoldingRequest.java",
    "backend/src/main/java/com/portfolio/analytics/holding/dto/UpdateHoldingRequest.java",
    "backend/src/main/java/com/portfolio/analytics/holding/dto/HoldingResponse.java",
    "backend/src/main/java/com/portfolio/analytics/transaction/dto/CreateTransactionRequest.java",
    "backend/src/main/java/com/portfolio/analytics/transaction/dto/TransactionResponse.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/PerformanceMetricsDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/RiskMetricsDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/DrawdownDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/VarDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/CorrelationDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/RiskAttributionDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/OptimizationResultDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/EfficientFrontierResultDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/MonteCarloResultDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/ScenarioResultDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/StressTestResultDto.java",
    "backend/src/main/java/com/portfolio/analytics/analytics/dto/CachedAnalyticsResult.java",
    "backend/src/main/java/com/portfolio/analytics/optimization/dto/OptimizationApiRequest.java",
    "backend/src/main/java/com/portfolio/analytics/optimization/dto/EfficientFrontierApiRequest.java",
    "backend/src/main/java/com/portfolio/analytics/optimization/dto/OptimizationApiResponse.java",
    "backend/src/main/java/com/portfolio/analytics/simulation/dto/MonteCarloApiRequest.java",
    "backend/src/main/java/com/portfolio/analytics/scenario/dto/ScenarioApiRequest.java",
    "backend/src/main/java/com/portfolio/analytics/scenario/dto/StressTestApiRequest.java"
]

print(f"Total DTO files to process: {len(DTO_FILES)}")
