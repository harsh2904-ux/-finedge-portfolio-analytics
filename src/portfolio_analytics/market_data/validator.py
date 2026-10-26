"""
Data Validation Engine for Financial Market Prices
"""

from enum import Enum
from typing import Dict, List, Tuple
from portfolio_analytics.market_data.base_provider import RawPriceRecord
from portfolio_analytics.config.logging_config import logger


class ValidationStatus(str, Enum):
    VALID = "VALID"
    WARNING = "WARNING"
    INVALID = "INVALID"


class ValidationResult:
    def __init__(self, record: RawPriceRecord, status: ValidationStatus, errors: List[str]):
        self.record = record
        self.status = status
        self.errors = errors

    @property
    def is_usable(self) -> bool:
        """Returns True if record can be safely ingested into database."""
        return self.status in (ValidationStatus.VALID, ValidationStatus.WARNING)


class PriceValidator:
    """
    Validates OHLCV relationships, numerical boundaries, and timestamps.
    """

    @staticmethod
    def validate_record(record: RawPriceRecord) -> ValidationResult:
        errors: List[str] = []
        is_invalid = False

        # 1. Positive price checks
        if record.open <= 0:
            errors.append(f"Non-positive open price: {record.open}")
            is_invalid = True
        if record.high <= 0:
            errors.append(f"Non-positive high price: {record.high}")
            is_invalid = True
        if record.low <= 0:
            errors.append(f"Non-positive low price: {record.low}")
            is_invalid = True
        if record.close <= 0:
            errors.append(f"Non-positive close price: {record.close}")
            is_invalid = True

        # 2. OHLC Logical Boundaries
        if record.high < record.low:
            errors.append(f"High ({record.high}) is lower than Low ({record.low})")
            is_invalid = True
        if record.high < record.open:
            errors.append(f"High ({record.high}) is lower than Open ({record.open})")
            is_invalid = True
        if record.high < record.close:
            errors.append(f"High ({record.high}) is lower than Close ({record.close})")
            is_invalid = True
        if record.low > record.open:
            errors.append(f"Low ({record.low}) is higher than Open ({record.open})")
            is_invalid = True
        if record.low > record.close:
            errors.append(f"Low ({record.low}) is higher than Close ({record.close})")
            is_invalid = True

        # 3. Volume Check
        if record.volume < 0:
            errors.append(f"Negative volume: {record.volume}")
            is_invalid = True

        # 4. Outlier / Extreme spread warning
        if not is_invalid and record.low > 0:
            spread_ratio = (record.high - record.low) / record.low
            if spread_ratio > 0.50:  # Intraday move > 50%
                errors.append(f"Abnormally wide intraday spread: {spread_ratio:.1%}")
                return ValidationResult(record, ValidationStatus.WARNING, errors)

        if is_invalid:
            return ValidationResult(record, ValidationStatus.INVALID, errors)

        return ValidationResult(record, ValidationStatus.VALID, errors)

    @classmethod
    def validate_batch(cls, records: List[RawPriceRecord]) -> Tuple[List[RawPriceRecord], List[ValidationResult]]:
        """
        Validates a batch of price records and returns (usable_records, validation_reports).
        """
        usable: List[RawPriceRecord] = []
        reports: List[ValidationResult] = []

        seen_timestamps = set()
        for rec in records:
            # Check duplicate timestamp within batch
            ts_key = (rec.symbol, rec.timestamp)
            if ts_key in seen_timestamps:
                res = ValidationResult(rec, ValidationStatus.WARNING, ["Duplicate timestamp in batch"])
                reports.append(res)
                continue
            seen_timestamps.add(ts_key)

            res = cls.validate_record(rec)
            reports.append(res)
            if res.is_usable:
                usable.append(rec)
            else:
                logger.warning(f"Invalid record for {rec.symbol} at {rec.timestamp}: {res.errors}")

        return usable, reports
