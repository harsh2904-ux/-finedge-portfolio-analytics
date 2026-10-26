"""
Robust POJO Generator for Java DTOs
"""

import os
import re

def process_file(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    if "lombok" not in content:
        return

    # Check for inner static classes
    # If the file contains inner static classes (like PositionValuationDto or ErrorDetails),
    # we should handle them carefully.
    print(f"Processing: {file_path}")

process_file("backend/src/main/java/com/portfolio/analytics/auth/dto/RegisterRequest.java")
