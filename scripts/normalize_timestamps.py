import sqlite3
import re

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

def clean_ts(val):
    if val is None:
        return None
    val = str(val)
    if val.isdigit():
        return val # Milliseconds integer is fine
    # Replace 'T' with ' ' and remove '+00:00' or 'Z'
    val = val.replace('T', ' ').replace('+00:00', '').replace('Z', '')
    # Ensure there is fractional seconds
    if '.' not in val:
        val = val + '.000'
    else:
        # truncate or pad to 3 digits
        parts = val.split('.')
        val = parts[0] + '.' + parts[1][:3].ljust(3, '0')
    return val

tables_and_cols = [
    ('portfolios', ['created_at', 'updated_at']),
    ('assets', ['created_at', 'updated_at']),
    ('portfolio_holdings', ['purchase_date', 'created_at', 'updated_at']),
    ('transactions', ['transaction_date', 'created_at']),
    ('market_prices', ['timestamp', 'created_at']),
]

for table, cols in tables_and_cols:
    rows = cursor.execute(f"SELECT id, {', '.join(cols)} FROM {table}").fetchall()
    for row in rows:
        row_id = row[0]
        cleaned_values = [clean_ts(v) for v in row[1:]]
        set_clause = ', '.join([f"{c} = ?" for c in cols])
        cursor.execute(f"UPDATE {table} SET {set_clause} WHERE id = ?", (*cleaned_values, row_id))

conn.commit()
print("All timestamp columns normalized.")

for table, cols in tables_and_cols:
    row = cursor.execute(f"SELECT {', '.join(cols)} FROM {table} LIMIT 1").fetchone()
    print(f"{table}: {row}")

conn.close()
