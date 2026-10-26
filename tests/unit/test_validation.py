"""
Unit Tests for Data Validation and Quality Assurance (Section 36 Requirements)
"""

from datetime import datetime, timezone
from portfolio_analytics.market_data.base_provider import RawPriceRecord
from portfolio_analytics.market_data.validator import (
    PriceValidator,
    ValidationStatus,
)


def test_valid_price_record():
    rec = RawPriceRecord(
        symbol="AAPL",
        timestamp=datetime.now(timezone.utc),
        open=150.0,
        high=155.0,
        low=148.0,
        close=152.0,
        volume=1000000,
    )
    res = PriceValidator.validate_record(rec)
    assert res.status == ValidationStatus.VALID
    assert res.is_usable is True
    assert len(res.errors) == 0


def test_negative_price_rejected():
    rec = RawPriceRecord(
        symbol="AAPL",
        timestamp=datetime.now(timezone.utc),
        open=-150.0,
        high=155.0,
        low=148.0,
        close=152.0,
        volume=1000,
    )
    res = PriceValidator.validate_record(rec)
    assert res.status == ValidationStatus.INVALID
    assert res.is_usable is False
    assert any("Non-positive open" in err for err in res.errors)


def test_impossible_ohlc_high_lower_than_low():
    rec = RawPriceRecord(
        symbol="AAPL",
        timestamp=datetime.now(timezone.utc),
        open=150.0,
        high=140.0,  # High lower than Low
        low=145.0,
        close=142.0,
        volume=1000,
    )
    res = PriceValidator.validate_record(rec)
    assert res.status == ValidationStatus.INVALID
    assert any("lower than Low" in err for err in res.errors)


def test_negative_volume_rejected():
    rec = RawPriceRecord(
        symbol="AAPL",
        timestamp=datetime.now(timezone.utc),
        open=150.0,
        high=155.0,
        low=148.0,
        close=152.0,
        volume=-500,
    )
    res = PriceValidator.validate_record(rec)
    assert res.status == ValidationStatus.INVALID
    assert any("Negative volume" in err for err in res.errors)


def test_duplicate_timestamp_batch_detection():
    dt = datetime(2024, 1, 1, 10, 0, 0, tzinfo=timezone.utc)
    rec1 = RawPriceRecord(symbol="AAPL", timestamp=dt, open=100, high=105, low=98, close=102, volume=10)
    rec2 = RawPriceRecord(symbol="AAPL", timestamp=dt, open=100, high=105, low=98, close=102, volume=10)

    usable, reports = PriceValidator.validate_batch([rec1, rec2])
    assert len(usable) == 1
    assert any("Duplicate timestamp" in rep.errors[0] for rep in reports if rep.errors)
