-- V1__initial_schema.sql
-- Base schema replicating Part 1 tables for PostgreSQL

CREATE TABLE IF NOT EXISTS assets (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL UNIQUE,
    provider_symbol VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    asset_class VARCHAR(64) NOT NULL,
    sector VARCHAR(128),
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    exchange VARCHAR(64),
    country VARCHAR(64),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_assets_symbol ON assets (symbol);
CREATE INDEX IF NOT EXISTS ix_assets_asset_class ON assets (asset_class);

CREATE TABLE IF NOT EXISTS market_prices (
    id SERIAL PRIMARY KEY,
    asset_id INTEGER NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    open DOUBLE PRECISION NOT NULL,
    high DOUBLE PRECISION NOT NULL,
    low DOUBLE PRECISION NOT NULL,
    close DOUBLE PRECISION NOT NULL,
    adjusted_close DOUBLE PRECISION,
    volume DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    source VARCHAR(64) NOT NULL DEFAULT 'twelve_data',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_asset_time_source UNIQUE (asset_id, timestamp, source)
);

CREATE INDEX IF NOT EXISTS ix_market_prices_asset_id ON market_prices (asset_id);
CREATE INDEX IF NOT EXISTS ix_market_prices_timestamp ON market_prices (timestamp);
CREATE INDEX IF NOT EXISTS ix_market_prices_asset_timestamp ON market_prices (asset_id, timestamp);

CREATE TABLE IF NOT EXISTS portfolios (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    base_currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    benchmark_asset_id INTEGER REFERENCES assets(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_portfolios_name ON portfolios (name);

CREATE TABLE IF NOT EXISTS portfolio_holdings (
    id SERIAL PRIMARY KEY,
    portfolio_id INTEGER NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    asset_id INTEGER NOT NULL REFERENCES assets(id) ON DELETE RESTRICT,
    quantity DOUBLE PRECISION NOT NULL,
    average_cost DOUBLE PRECISION NOT NULL,
    target_weight DOUBLE PRECISION,
    purchase_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_portfolio_asset UNIQUE (portfolio_id, asset_id)
);

CREATE INDEX IF NOT EXISTS ix_portfolio_holdings_portfolio_id ON portfolio_holdings (portfolio_id);
CREATE INDEX IF NOT EXISTS ix_portfolio_holdings_asset_id ON portfolio_holdings (asset_id);

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    portfolio_id INTEGER NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    asset_id INTEGER NOT NULL REFERENCES assets(id) ON DELETE RESTRICT,
    transaction_type VARCHAR(16) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    transaction_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    transaction_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_transactions_portfolio_id ON transactions (portfolio_id);
CREATE INDEX IF NOT EXISTS ix_transactions_asset_id ON transactions (asset_id);
CREATE INDEX IF NOT EXISTS ix_transactions_transaction_date ON transactions (transaction_date);
