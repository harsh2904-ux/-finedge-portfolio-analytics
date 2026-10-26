import sqlite3

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

for table in ['users', 'portfolios', 'assets', 'portfolio_holdings', 'transactions']:
    print(f"\n--- {table} ---")
    columns = cursor.execute(f"PRAGMA table_info({table})").fetchall()
    for col in columns:
        print(col)

print("\n--- Users Data ---")
for row in cursor.execute("SELECT * FROM users").fetchall():
    print(row)

print("\n--- Portfolios Data ---")
for row in cursor.execute("SELECT * FROM portfolios").fetchall():
    print(row)

conn.close()
