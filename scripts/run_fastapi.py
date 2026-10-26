"""
FastAPI Server Launcher
Runs the Quantitative Financial Analytics Microservice on port 8001.
"""

import os
import sys

# Ensure src is in python path
current_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.dirname(current_dir)
src_dir = os.path.join(project_root, "src")
if src_dir not in sys.path:
    sys.path.insert(0, src_dir)

import uvicorn

if __name__ == "__main__":
    port = int(os.environ.get("ANALYTICS_PORT", "8001"))
    host = os.environ.get("ANALYTICS_HOST", "0.0.0.0")
    print(f"Starting Quantitative Analytics Engine FastAPI service on http://{host}:{port}")
    uvicorn.run("portfolio_analytics.api.fastapi_app:app", host=host, port=port, log_level="info")
