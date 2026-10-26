"""
Structured Logging Configuration
"""

import logging
import sys
from portfolio_analytics.config.settings import settings


def setup_logging(level: str = None) -> logging.Logger:
    """Configures structured console and file logging."""
    log_level_name = level or settings.LOG_LEVEL
    log_level = getattr(logging, log_level_name.upper(), logging.INFO)

    log_format = "%(asctime)s %(levelname)s %(name)s [%(filename)s:%(lineno)d] - %(message)s"
    date_format = "%Y-%m-%d %H:%M:%S"

    # Configure root logger
    logging.basicConfig(
        level=log_level,
        format=log_format,
        datefmt=date_format,
        handlers=[
            logging.StreamHandler(sys.stdout),
        ],
        force=True
    )

    logger = logging.getLogger("portfolio_analytics")
    logger.setLevel(log_level)
    return logger


logger = setup_logging()
