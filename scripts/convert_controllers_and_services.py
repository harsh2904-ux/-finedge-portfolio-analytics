"""
Script to convert Lombok annotations to pure idiomatic Java POJOs
Ensures seamless compilation on JDK 25.
"""

import os
import re

JAVA_ROOT = os.path.abspath("backend/src")

def get_class_name(content):
    m = re.search(r'public\s+(?:class|enum)\s+(\w+)', content)
    return m.group(1) if m else None

def fix_controllers_and_services(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    class_name = get_class_name(content)
    if not class_name:
        return

    # Check if @Slf4j is present
    if "@Slf4j" in content:
        content = re.sub(r'@Slf4j\s*\n', '', content)
        # Add SLF4J logger field inside class
        logger_decl = f'    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger({class_name}.class);\n'
        content = re.sub(
            r'(public\s+class\s+' + class_name + r'[^{]*\{)',
            r'\1\n' + logger_decl,
            content,
            count=1
        )

    # Check if @RequiredArgsConstructor is present
    if "@RequiredArgsConstructor" in content:
        content = re.sub(r'@RequiredArgsConstructor\s*\n', '', content)
        # Find private final fields
        fields = re.findall(r'private\s+final\s+([\w<>?,\s]+?)\s+(\w+);', content)
        if fields:
            params = ", ".join([f"{f_type.strip()} {f_name}" for f_type, f_name in fields])
            assignments = "\n".join([f"        this.{f_name} = {f_name};" for _, f_name in fields])
            constructor = f"\n    public {class_name}({params}) {{\n{assignments}\n    }}\n"
            # Insert after the last final field
            last_field_pattern = r'(private\s+final\s+[\w<>?,\s]+?\s+\w+;)'
            all_matches = list(re.finditer(last_field_pattern, content))
            if all_matches:
                last_match = all_matches[-1]
                idx = last_match.end()
                content = content[:idx] + constructor + content[idx:]

    # Clean up lombok imports
    content = re.sub(r'import\s+lombok\.[^;]+;\s*\n', '', content)

    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

for root, dirs, files in os.walk(JAVA_ROOT):
    for f in files:
        if f.endswith(".java") and (f.endswith("Controller.java") or f.endswith("Service.java") or f.endswith("Filter.java") or f.endswith("Provider.java") or f.endswith("EntryPoint.java") or f.endswith("ExceptionHandler.java") or f.endswith("Client.java") or f.endswith("Indicator.java")):
            fix_controllers_and_services(os.path.join(root, f))

print("Controllers, Services, and infrastructure components converted successfully!")
