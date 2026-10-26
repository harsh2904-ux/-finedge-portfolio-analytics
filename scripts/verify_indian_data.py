import sqlite3

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

print("--- Assets ---")
for r in cursor.execute("SELECT id, symbol, name, currency, exchange FROM assets").fetchall():
    print(r)

print("\n--- Portfolio 1 Holdings ---")
for r in cursor.execute("""
    SELECT h.id, a.symbol, h.quantity, h.average_cost, h.target_weight
    FROM portfolio_holdings h
    JOIN assets a ON h.asset_id = a.id
    WHERE h.portfolio_id = 1
""").fetchall():
    print(r)

print("\n--- Portfolio 2 Holdings ---")
for r in cursor.execute("""
    SELECT h.id, a.symbol, h.quantity, h.average_cost, h.target_weight
    FROM portfolio_holdings h
    JOIN assets a ON h.asset_id = a.id
    WHERE h.portfolio_id = 2
""").fetchall():
    print(r)

print("\n--- Market Prices Count ---")
print(cursor.execute("SELECT count(*) FROM market_prices").fetchone()[0])

conn.close()
