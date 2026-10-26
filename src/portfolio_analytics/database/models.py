"""
SQLAlchemy 2.x Normalized Database Models for Multi-Asset Portfolio Analytics
"""

from datetime import datetime, timezone
from typing import List, Optional
from sqlalchemy import (
    String,
    Float,
    Boolean,
    DateTime,
    ForeignKey,
    Index,
    UniqueConstraint,
    Integer,
    Text,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship
from portfolio_analytics.database.connection import Base


from sqlalchemy.types import TypeDecorator

def utc_now() -> datetime:
    return datetime.now(timezone.utc)

class SafeDateTime(TypeDecorator):
    impl = String
    cache_ok = True

    def process_result_value(self, value, dialect):
        if value is None:
            return None
        if isinstance(value, datetime):
            return value
        if isinstance(value, (int, float)):
            ts = value / 1000.0 if value > 1e11 else float(value)
            return datetime.fromtimestamp(ts, tz=timezone.utc)
        if isinstance(value, str):
            try:
                return datetime.fromisoformat(value)
            except Exception:
                try:
                    return datetime.strptime(value, "%Y-%m-%d %H:%M:%S.%f")
                except Exception:
                    return datetime.strptime(value, "%Y-%m-%d %H:%M:%S")
        return value

    def process_bind_param(self, value, dialect):
        if value is None:
            return None
        if isinstance(value, datetime):
            return value.strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]
        return str(value)


class Asset(Base):
    """
    Financial Asset (Equities, ETFs, Bonds, Commodities, REITs).
    """
    __tablename__ = "assets"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    symbol: Mapped[str] = mapped_column(String(32), unique=True, nullable=False, index=True)
    provider_symbol: Mapped[str] = mapped_column(String(64), nullable=False, index=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    asset_class: Mapped[str] = mapped_column(String(64), nullable=False, index=True)
    sector: Mapped[Optional[str]] = mapped_column(String(128), nullable=True)
    currency: Mapped[str] = mapped_column(String(8), default="INR", nullable=False)
    exchange: Mapped[Optional[str]] = mapped_column(String(64), nullable=True)
    country: Mapped[Optional[str]] = mapped_column(String(64), nullable=True)
    is_active: Mapped[bool] = mapped_column(Boolean, default=True, nullable=False)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)
    updated_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now, onupdate=utc_now)

    # Relationships
    market_prices: Mapped[List["MarketPrice"]] = relationship(
        "MarketPrice", back_populates="asset", cascade="all, delete-orphan"
    )
    holdings: Mapped[List["PortfolioHolding"]] = relationship("PortfolioHolding", back_populates="asset")
    transactions: Mapped[List["Transaction"]] = relationship("Transaction", back_populates="asset")

    def __repr__(self) -> str:
        return f"<Asset(id={self.id}, symbol='{self.symbol}', name='{self.name}')>"


class MarketPrice(Base):
    """
    Historical and Intraday Market Prices (OHLCV).
    """
    __tablename__ = "market_prices"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    asset_id: Mapped[int] = mapped_column(Integer, ForeignKey("assets.id", ondelete="CASCADE"), nullable=False, index=True)
    timestamp: Mapped[datetime] = mapped_column(SafeDateTime(), nullable=False, index=True)
    open: Mapped[float] = mapped_column(Float, nullable=False)
    high: Mapped[float] = mapped_column(Float, nullable=False)
    low: Mapped[float] = mapped_column(Float, nullable=False)
    close: Mapped[float] = mapped_column(Float, nullable=False)
    adjusted_close: Mapped[Optional[float]] = mapped_column(Float, nullable=True)
    volume: Mapped[float] = mapped_column(Float, default=0.0, nullable=False)
    source: Mapped[str] = mapped_column(String(64), default="twelve_data", nullable=False)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)

    # Relationships
    asset: Mapped["Asset"] = relationship("Asset", back_populates="market_prices")

    __table_args__ = (
        UniqueConstraint("asset_id", "timestamp", "source", name="uq_asset_time_source"),
        Index("ix_market_prices_asset_timestamp", "asset_id", "timestamp"),
    )

    def __repr__(self) -> str:
        return f"<MarketPrice(asset_id={self.asset_id}, timestamp='{self.timestamp}', close={self.close})>"


class User(Base):
    """
    Application User for Authentication and Authorization.
    """
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), unique=True, nullable=False, index=True)
    password_hash: Mapped[str] = mapped_column(String(255), nullable=False)
    role: Mapped[str] = mapped_column(String(32), default="USER", nullable=False)
    is_active: Mapped[bool] = mapped_column("active", Boolean, default=True, nullable=False)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)
    updated_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now, onupdate=utc_now)

    # Relationships
    portfolios: Mapped[List["Portfolio"]] = relationship(
        "Portfolio", back_populates="user", cascade="all, delete-orphan"
    )

    def __repr__(self) -> str:
        return f"<User(id={self.id}, email='{self.email}', role='{self.role}')>"


class Portfolio(Base):
    """
    Investment Portfolio container.
    """
    __tablename__ = "portfolios"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[Optional[int]] = mapped_column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=True, index=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False, index=True)
    base_currency: Mapped[str] = mapped_column(String(8), default="INR", nullable=False)
    benchmark_asset_id: Mapped[Optional[int]] = mapped_column(Integer, ForeignKey("assets.id", ondelete="SET NULL"), nullable=True)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)
    updated_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now, onupdate=utc_now)

    # Relationships
    user: Mapped[Optional["User"]] = relationship("User", back_populates="portfolios")
    benchmark_asset: Mapped[Optional["Asset"]] = relationship("Asset", foreign_keys=[benchmark_asset_id])
    holdings: Mapped[List["PortfolioHolding"]] = relationship(
        "PortfolioHolding", back_populates="portfolio", cascade="all, delete-orphan"
    )
    transactions: Mapped[List["Transaction"]] = relationship(
        "Transaction", back_populates="portfolio", cascade="all, delete-orphan"
    )

    def __repr__(self) -> str:
        return f"<Portfolio(id={self.id}, name='{self.name}', currency='{self.base_currency}')>"


class PortfolioHolding(Base):
    """
    Current holdings of an asset in a portfolio.
    """
    __tablename__ = "portfolio_holdings"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    portfolio_id: Mapped[int] = mapped_column(Integer, ForeignKey("portfolios.id", ondelete="CASCADE"), nullable=False, index=True)
    asset_id: Mapped[int] = mapped_column(Integer, ForeignKey("assets.id", ondelete="RESTRICT"), nullable=False, index=True)
    quantity: Mapped[float] = mapped_column(Float, nullable=False)
    average_cost: Mapped[float] = mapped_column(Float, nullable=False)
    target_weight: Mapped[Optional[float]] = mapped_column(Float, nullable=True)
    purchase_date: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)
    updated_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now, onupdate=utc_now)

    # Relationships
    portfolio: Mapped["Portfolio"] = relationship("Portfolio", back_populates="holdings")
    asset: Mapped["Asset"] = relationship("Asset", back_populates="holdings")

    __table_args__ = (
        UniqueConstraint("portfolio_id", "asset_id", name="uq_portfolio_asset"),
    )

    def __repr__(self) -> str:
        return f"<PortfolioHolding(portfolio_id={self.portfolio_id}, asset_id={self.asset_id}, quantity={self.quantity})>"


class Transaction(Base):
    """
    Record of asset transactions (BUY, SELL, DIVIDEND, FEE).
    """
    __tablename__ = "transactions"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    portfolio_id: Mapped[int] = mapped_column(Integer, ForeignKey("portfolios.id", ondelete="CASCADE"), nullable=False, index=True)
    asset_id: Mapped[int] = mapped_column(Integer, ForeignKey("assets.id", ondelete="RESTRICT"), nullable=False, index=True)
    transaction_type: Mapped[str] = mapped_column(String(16), nullable=False)  # BUY, SELL, DIVIDEND, FEE
    quantity: Mapped[float] = mapped_column(Float, nullable=False)
    price: Mapped[float] = mapped_column(Float, nullable=False)
    transaction_cost: Mapped[float] = mapped_column(Float, default=0.0, nullable=False)
    transaction_date: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now, index=True)
    currency: Mapped[str] = mapped_column(String(8), default="INR", nullable=False)
    created_at: Mapped[datetime] = mapped_column(SafeDateTime(), default=utc_now)

    # Relationships
    portfolio: Mapped["Portfolio"] = relationship("Portfolio", back_populates="transactions")
    asset: Mapped["Asset"] = relationship("Asset", back_populates="transactions")

    def __repr__(self) -> str:
        return f"<Transaction(id={self.id}, type='{self.transaction_type}', asset_id={self.asset_id}, qty={self.quantity})>"
