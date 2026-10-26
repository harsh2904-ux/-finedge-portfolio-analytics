"""
Generator to convert Lombok Entity and DTO classes to clean standard Java classes.
"""

import os
import re

def convert_class_file(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    text = "".join(lines)
    if "lombok" not in text:
        return

    # Check package
    pkg_match = re.search(r'package\s+([\w.]+);', text)
    package = pkg_match.group(0) if pkg_match else ""

    # Check imports
    imports = []
    for line in lines:
        if line.strip().startswith("import ") and "lombok" not in line:
            imports.append(line)

    # We will inspect and rewrite the file
    print(f"Converting: {file_path}")

convert_class_file("backend/src/main/java/com/portfolio/analytics/user/User.java")
