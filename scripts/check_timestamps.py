import sqlite3

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

for table in ['portfolios', 'assets', 'portfolio_holdings', 'transactions', 'users']:
    row = cursor.execute(f"SELECT * FROM {table} LIMIT 1").fetchone()
    print(f"{table}:", row)

conn.close()
