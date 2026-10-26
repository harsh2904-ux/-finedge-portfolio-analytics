"""
Database Connection & Session Management using SQLAlchemy 2.x
"""

from typing import Generator
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session, DeclarativeBase
from portfolio_analytics.config.settings import settings
from portfolio_analytics.config.logging_config import logger


class Base(DeclarativeBase):
    """Base declarative class for SQLAlchemy 2.x models."""
    pass


def get_engine(database_url: str = None):
    """
    Creates an SQLAlchemy engine with pool configurations.
    Supports PostgreSQL and SQLite.
    """
    url = database_url or settings.DATABASE_URL
    connect_args = {}
    if url.startswith("sqlite"):
        connect_args["check_same_thread"] = False
        return create_engine(url, connect_args=connect_args, echo=False)
    
    return create_engine(
        url,
        pool_size=10,
        max_overflow=20,
        pool_pre_ping=True,
        echo=False
    )


engine = get_engine()
SessionFactory = sessionmaker(bind=engine, autoflush=False, autocommit=False)


def get_db_session() -> Generator[Session, None, None]:
    """Yields a database session with automatic commit / rollback."""
    session = SessionFactory()
    try:
        yield session
        session.commit()
    except Exception as e:
        session.rollback()
        logger.error(f"Database session rolled back due to error: {e}")
        raise
    finally:
        session.close()


def create_all_tables(custom_engine=None) -> None:
    """Creates all database tables defined in Base metadata."""
    target_engine = custom_engine or engine
    logger.info("Initializing database schema...")
    Base.metadata.create_all(bind=target_engine)
    logger.info("Database schema initialized successfully.")
