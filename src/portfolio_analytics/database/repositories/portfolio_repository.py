"""
Portfolio Repository for portfolios, holdings, and transaction management
"""

from datetime import datetime, timezone
from typing import List, Optional
from sqlalchemy import select, and_
from sqlalchemy.orm import Session, selectinload
from portfolio_analytics.database.models import Portfolio, PortfolioHolding, Transaction, Asset
from portfolio_analytics.config.logging_config import logger


class PortfolioRepository:
    """Repository handling Portfolio, Holdings, and Transaction operations."""

    def __init__(self, session: Session):
        self.session = session

    def create_portfolio(
        self,
        name: str,
        base_currency: str = "INR",
        benchmark_asset_id: Optional[int] = None,
    ) -> Portfolio:
        portfolio = Portfolio(
            name=name,
            base_currency=base_currency,
            benchmark_asset_id=benchmark_asset_id,
        )
        self.session.add(portfolio)
        self.session.flush()
        return portfolio

    def get_portfolio(self, portfolio_id: int) -> Optional[Portfolio]:
        stmt = (
            select(Portfolio)
            .options(
                selectinload(Portfolio.holdings).selectinload(PortfolioHolding.asset),
                selectinload(Portfolio.benchmark_asset),
            )
            .where(Portfolio.id == portfolio_id)
        )
        return self.session.scalar(stmt)

    def list_portfolios(self) -> List[Portfolio]:
        stmt = select(Portfolio).order_by(Portfolio.id)
        return list(self.session.scalars(stmt).all())

    def add_or_update_holding(
        self,
        portfolio_id: int,
        asset_id: int,
        quantity: float,
        average_cost: float,
        target_weight: Optional[float] = None,
        purchase_date: Optional[datetime] = None,
    ) -> PortfolioHolding:
        stmt = select(PortfolioHolding).where(
            and_(
                PortfolioHolding.portfolio_id == portfolio_id,
                PortfolioHolding.asset_id == asset_id,
            )
        )
        holding = self.session.scalar(stmt)
        if holding:
            holding.quantity = quantity
            holding.average_cost = average_cost
            if target_weight is not None:
                holding.target_weight = target_weight
            if purchase_date:
                holding.purchase_date = purchase_date
        else:
            holding = PortfolioHolding(
                portfolio_id=portfolio_id,
                asset_id=asset_id,
                quantity=quantity,
                average_cost=average_cost,
                target_weight=target_weight,
                purchase_date=purchase_date or datetime.now(timezone.utc),
            )
            self.session.add(holding)
        self.session.flush()
        return holding

    def record_transaction(
        self,
        portfolio_id: int,
        asset_id: int,
        transaction_type: str,
        quantity: float,
        price: float,
        transaction_cost: float = 0.0,
        transaction_date: Optional[datetime] = None,
        currency: str = "INR",
    ) -> Transaction:
        tx = Transaction(
            portfolio_id=portfolio_id,
            asset_id=asset_id,
            transaction_type=transaction_type.upper(),
            quantity=quantity,
            price=price,
            transaction_cost=transaction_cost,
            transaction_date=transaction_date or datetime.now(timezone.utc),
            currency=currency,
        )
        self.session.add(tx)
        self.session.flush()
        return tx

    def get_transactions(self, portfolio_id: int) -> List[Transaction]:
        stmt = (
            select(Transaction)
            .where(Transaction.portfolio_id == portfolio_id)
            .order_by(Transaction.transaction_date.desc())
        )
        return list(self.session.scalars(stmt).all())
