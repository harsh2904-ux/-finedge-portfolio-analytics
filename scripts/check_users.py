import sqlite3

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

users = cursor.execute("SELECT id, name, email, password_hash FROM users").fetchall()
for u in users:
    print(u[0], u[1], u[2], u[3])

conn.close()
