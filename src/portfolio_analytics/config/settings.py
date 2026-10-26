"""
Application Settings and Configuration using Pydantic Settings
"""

from typing import Optional
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    # Twelve Data API
    TWELVE_DATA_API_KEY: str = ""
    TWELVE_DATA_BASE_URL: str = "https://api.twelvedata.com"

    # Database
    DATABASE_URL: str = "sqlite:///portfolio_analytics.db"
    POSTGRES_HOST: str = "localhost"
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str = "portfolio_analytics"
    POSTGRES_USER: str = "postgres"
    POSTGRES_PASSWORD: str = "postgres"

    # Operational Parameters
    LOG_LEVEL: str = "INFO"
    DATA_UPDATE_INTERVAL_MINUTES: int = 5
    DEFAULT_RISK_FREE_RATE: float = 0.04
    DEFAULT_CONFIDENCE_LEVEL: float = 0.95
    TRADING_DAYS_PER_YEAR: int = 252


settings = Settings()
