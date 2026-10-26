"""001_initial_schema

Revision ID: 001_initial_schema
Revises: 
Create Date: 2026-10-06 12:00:00.000000

"""
from typing import Sequence, Union
from alembic import op
import sqlalchemy as sa

revision: str = '001_initial_schema'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # 1. assets table
    op.create_table(
        'assets',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('symbol', sa.String(length=32), nullable=False),
        sa.Column('provider_symbol', sa.String(length=64), nullable=False),
        sa.Column('name', sa.String(length=255), nullable=False),
        sa.Column('asset_class', sa.String(length=64), nullable=False),
        sa.Column('sector', sa.String(length=128), nullable=True),
        sa.Column('currency', sa.String(length=8), server_default='USD', nullable=False),
        sa.Column('exchange', sa.String(length=64), nullable=True),
        sa.Column('country', sa.String(length=64), nullable=True),
        sa.Column('is_active', sa.Boolean(), server_default='1', nullable=False),
        sa.Column('created_at', sa.DateTime(timezone=True), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('symbol')
    )
    op.create_index('ix_assets_symbol', 'assets', ['symbol'])
    op.create_index('ix_assets_asset_class', 'assets', ['asset_class'])

    # 2. market_prices table
    op.create_table(
        'market_prices',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('asset_id', sa.Integer(), nullable=False),
        sa.Column('timestamp', sa.DateTime(timezone=True), nullable=False),
        sa.Column('open', sa.Float(), nullable=False),
        sa.Column('high', sa.Float(), nullable=False),
        sa.Column('low', sa.Float(), nullable=False),
        sa.Column('close', sa.Float(), nullable=False),
        sa.Column('adjusted_close', sa.Float(), nullable=True),
        sa.Column('volume', sa.Float(), server_default='0.0', nullable=False),
        sa.Column('source', sa.String(length=64), server_default='twelve_data', nullable=False),
        sa.Column('created_at', sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(['asset_id'], ['assets.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('asset_id', 'timestamp', 'source', name='uq_asset_time_source')
    )
    op.create_index('ix_market_prices_asset_id', 'market_prices', ['asset_id'])
    op.create_index('ix_market_prices_timestamp', 'market_prices', ['timestamp'])
    op.create_index('ix_market_prices_asset_timestamp', 'market_prices', ['asset_id', 'timestamp'])

    # 3. portfolios table
    op.create_table(
        'portfolios',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('name', sa.String(length=255), nullable=False),
        sa.Column('base_currency', sa.String(length=8), server_default='USD', nullable=False),
        sa.Column('benchmark_asset_id', sa.Integer(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(['benchmark_asset_id'], ['assets.id'], ondelete='SET NULL'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index('ix_portfolios_name', 'portfolios', ['name'])

    # 4. portfolio_holdings table
    op.create_table(
        'portfolio_holdings',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('portfolio_id', sa.Integer(), nullable=False),
        sa.Column('asset_id', sa.Integer(), nullable=False),
        sa.Column('quantity', sa.Float(), nullable=False),
        sa.Column('average_cost', sa.Float(), nullable=False),
        sa.Column('target_weight', sa.Float(), nullable=True),
        sa.Column('purchase_date', sa.DateTime(timezone=True), nullable=False),
        sa.Column('created_at', sa.DateTime(timezone=True), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(['asset_id'], ['assets.id'], ondelete='RESTRICT'),
        sa.ForeignKeyConstraint(['portfolio_id'], ['portfolios.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('portfolio_id', 'asset_id', name='uq_portfolio_asset')
    )
    op.create_index('ix_portfolio_holdings_portfolio_id', 'portfolio_holdings', ['portfolio_id'])
    op.create_index('ix_portfolio_holdings_asset_id', 'portfolio_holdings', ['asset_id'])

    # 5. transactions table
    op.create_table(
        'transactions',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('portfolio_id', sa.Integer(), nullable=False),
        sa.Column('asset_id', sa.Integer(), nullable=False),
        sa.Column('transaction_type', sa.String(length=16), nullable=False),
        sa.Column('quantity', sa.Float(), nullable=False),
        sa.Column('price', sa.Float(), nullable=False),
        sa.Column('transaction_cost', sa.Float(), server_default='0.0', nullable=False),
        sa.Column('transaction_date', sa.DateTime(timezone=True), nullable=False),
        sa.Column('currency', sa.String(length=8), server_default='USD', nullable=False),
        sa.Column('created_at', sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(['asset_id'], ['assets.id'], ondelete='RESTRICT'),
        sa.ForeignKeyConstraint(['portfolio_id'], ['portfolios.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index('ix_transactions_portfolio_id', 'transactions', ['portfolio_id'])
    op.create_index('ix_transactions_asset_id', 'transactions', ['asset_id'])
    op.create_index('ix_transactions_transaction_date', 'transactions', ['transaction_date'])


def downgrade() -> None:
    op.drop_table('transactions')
    op.drop_table('portfolio_holdings')
    op.drop_table('portfolios')
    op.drop_table('market_prices')
    op.drop_table('assets')
