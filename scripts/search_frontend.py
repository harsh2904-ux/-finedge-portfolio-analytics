import os

results = []
for r, d, files in os.walk('frontend/src'):
    for f in files:
        if f.endswith(('.ts', '.tsx')):
            filepath = os.path.join(r, f)
            with open(filepath, 'r', encoding='utf-8', errors='ignore') as fp:
                for idx, line in enumerate(fp, 1):
                    if 'USD' in line or 'SPY' in line or 'baseCurrency' in line or 'benchmark' in line.lower():
                        results.append(f"{filepath}:{idx}: {line.strip()}")

print(f"Total occurrences: {len(results)}")
for line in results[:40]:
    print(line)
if len(results) > 40:
    print(f"... and {len(results) - 40} more.")
