"""
Database Migration & Seeding Script: Transition to Indian Equities (NSE) & INR.
Populates realistic historical prices, Indian benchmark (NIFTYBEES), equities,
and portfolios for demo@portfolio.com and harsh.demo@example.com.
"""

import sys
import os
from pathlib import Path
from datetime import datetime, timedelta, timezone
import numpy as np

# Ensure project root is in python path
ROOT_DIR = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT_DIR / "src"))

from sqlalchemy import delete
from portfolio_analytics.database.connection import SessionFactory, create_all_tables
from portfolio_analytics.database.models import (
    Asset,
    MarketPrice,
    Portfolio,
    PortfolioHolding,
    Transaction,
    User,
)
from portfolio_analytics.database.repositories.asset_repository import AssetRepository
from portfolio_analytics.database.repositories.price_repository import PriceRepository
from portfolio_analytics.portfolio.portfolio_service import PortfolioService
from portfolio_analytics.config.logging_config import logger


def migrate():
    logger.info("Initializing Indian Market & INR Migration...")
    create_all_tables()
    session = SessionFactory()

    try:
        # Step 1: Clean existing data (holdings, transactions, prices, portfolios, assets)
        logger.info("Purging old US equity market records...")
        session.execute(delete(Transaction))
        session.execute(delete(PortfolioHolding))
        session.execute(delete(MarketPrice))
        session.execute(delete(Portfolio))
        session.execute(delete(Asset))
        session.commit()
        logger.info("Old market records cleared successfully.")

        # Step 2: Ensure default users exist
        user_demo = session.query(User).filter(User.email == "demo@portfolio.com").first()
        if not user_demo:
            user_demo = User(
                id=2,
                name="Portfolio Demo Analyst",
                email="demo@portfolio.com",
                password_hash="$2a$10$sUAlgTdB/XJfwPIblceZDOF8aYgEjqG/NC2.d9AqkypYVGccaxZf.",
                role="USER",
                is_active=True,
            )
            session.add(user_demo)

        user_harsh = session.query(User).filter(User.email == "harsh.demo@example.com").first()
        if not user_harsh:
            user_harsh = User(
                id=1,
                name="Harsh Test",
                email="harsh.demo@example.com",
                password_hash="$2a$10$sUAlgTdB/XJfwPIblceZDOF8aYgEjqG/NC2.d9AqkypYVGccaxZf.",
                role="USER",
                is_active=True,
            )
            session.add(user_harsh)

        session.commit()

        # Step 3: Seed Indian Assets from config/default_assets.json
        asset_repo = AssetRepository(session)
        assets = asset_repo.seed_from_config("config/default_assets.json")
        logger.info(f"Seeded {len(assets)} Indian NSE assets in INR.")

        # Step 4: Generate 500 trading days of realistic INR market prices
        price_repo = PriceRepository(session)
        np.random.seed(42)
        end_date = datetime.now(timezone.utc)
        dates = [end_date - timedelta(days=x) for x in range(750, 0, -1)]
        trading_dates = [d for d in dates if d.weekday() < 5][-500:]

        base_specs = {
            "NIFTYBEES": (245.0, 0.00045, 0.009),   # Nifty 50 Benchmark ETF
            "RELIANCE": (2850.0, 0.00055, 0.013),   # Reliance Industries
            "TCS": (3820.0, 0.00045, 0.012),        # TCS
            "HDFCBANK": (1590.0, 0.00040, 0.012),   # HDFC Bank
            "INFY": (1750.0, 0.00050, 0.015),       # Infosys
            "ICICIBANK": (1180.0, 0.00060, 0.014),  # ICICI Bank
            "TATAMOTORS": (920.0, 0.00075, 0.021),  # Tata Motors
            "ITC": (470.0, 0.00035, 0.010),         # ITC Ltd
            "BHARTIARTL": (1600.0, 0.00065, 0.013), # Bharti Airtel
            "LT": (3550.0, 0.00050, 0.014),         # L&T
            "GOLDBEES": (64.0, 0.00030, 0.008),     # Nippon Gold ETF
        }

        price_records = []
        for asset in assets:
            sym = asset.symbol
            base_p, drift, vol = base_specs.get(sym, (500.0, 0.0004, 0.012))
            current_p = base_p

            for d in trading_dates:
                shock = np.random.normal(drift, vol)
                current_p = max(1.0, current_p * (1.0 + shock))
                high_p = current_p * (1.0 + abs(np.random.normal(0, vol * 0.4)))
                low_p = current_p * (1.0 - abs(np.random.normal(0, vol * 0.4)))
                open_p = (high_p + low_p) / 2.0

                price_records.append({
                    "asset_id": asset.id,
                    "timestamp": d,
                    "open": round(open_p, 2),
                    "high": round(high_p, 2),
                    "low": round(low_p, 2),
                    "close": round(current_p, 2),
                    "adjusted_close": round(current_p, 2),
                    "volume": float(np.random.randint(250000, 10000000)),
                    "source": "market_feed_inr",
                })

        price_repo.insert_prices(price_records)
        session.commit()
        logger.info(f"Populated {len(price_records)} historical INR price points.")

        # Step 5: Create Sample Portfolios & Holdings in INR
        niftybees = asset_repo.get_by_symbol("NIFTYBEES")
        niftybees_id = niftybees.id if niftybees else None

        # Portfolio 1: India Bluechip Alpha Fund (assigned to demo user, ID=2)
        p1 = Portfolio(
            name="India Bluechip Alpha Fund",
            base_currency="INR",
            benchmark_asset_id=niftybees_id,
            user_id=user_demo.id,
        )
        session.add(p1)
        session.flush()

        port_service = PortfolioService(session)
        p1_holdings = [
            ("RELIANCE", 100, 2850.0, 0.22),
            ("TCS", 75, 3800.0, 0.22),
            ("HDFCBANK", 150, 1580.0, 0.19),
            ("INFY", 120, 1720.0, 0.16),
            ("TATAMOTORS", 150, 920.0, 0.12),
            ("GOLDBEES", 1500, 64.0, 0.09),
        ]

        tx_date = datetime.now(timezone.utc) - timedelta(days=120)
        for sym, qty, cost, wt in p1_holdings:
            ast = asset_repo.get_by_symbol(sym)
            if ast:
                h = PortfolioHolding(
                    portfolio_id=p1.id,
                    asset_id=ast.id,
                    quantity=qty,
                    average_cost=cost,
                    target_weight=wt,
                    purchase_date=tx_date,
                )
                session.add(h)
                tx = Transaction(
                    portfolio_id=p1.id,
                    asset_id=ast.id,
                    transaction_type="BUY",
                    quantity=qty,
                    price=cost,
                    transaction_cost=150.0,
                    transaction_date=tx_date,
                    currency="INR",
                )
                session.add(tx)

        # Portfolio 2: Harsh's Bharat Wealth Portfolio (assigned to user 1)
        p2 = Portfolio(
            name="Harsh's Bharat Wealth Portfolio",
            base_currency="INR",
            benchmark_asset_id=niftybees_id,
            user_id=user_harsh.id,
        )
        session.add(p2)
        session.flush()

        p2_holdings = [
            ("RELIANCE", 50, 2850.0, 0.25),
            ("ICICIBANK", 120, 1180.0, 0.22),
            ("BHARTIARTL", 80, 1600.0, 0.20),
            ("LT", 30, 3550.0, 0.18),
            ("ITC", 200, 470.0, 0.10),
            ("GOLDBEES", 800, 64.0, 0.05),
        ]

        for sym, qty, cost, wt in p2_holdings:
            ast = asset_repo.get_by_symbol(sym)
            if ast:
                h = PortfolioHolding(
                    portfolio_id=p2.id,
                    asset_id=ast.id,
                    quantity=qty,
                    average_cost=cost,
                    target_weight=wt,
                    purchase_date=tx_date,
                )
                session.add(h)
                tx = Transaction(
                    portfolio_id=p2.id,
                    asset_id=ast.id,
                    transaction_type="BUY",
                    quantity=qty,
                    price=cost,
                    transaction_cost=100.0,
                    transaction_date=tx_date,
                    currency="INR",
                )
                session.add(tx)

        session.commit()
        logger.info(f"Created Portfolio 1 (ID={p1.id}) and Portfolio 2 (ID={p2.id}) with INR holdings.")

        print("\n=== MIGRATION TO INDIAN MARKET & INR COMPLETE ===")
        print(f"Total Assets: {len(assets)} (Exchange: NSE, Currency: INR)")
        print(f"Total Price Points: {len(price_records)}")
        print(f"Portfolios Created: '{p1.name}' (User={p1.user_id}), '{p2.name}' (User={p2.user_id})")
        print("Base Currency: INR (₹)")
        print("Benchmark: NIFTYBEES")

    except Exception as e:
        session.rollback()
        logger.error(f"Migration error: {e}", exc_info=True)
        raise
    finally:
        session.close()


if __name__ == "__main__":
    migrate()
