import sqlite3

conn = sqlite3.connect('portfolio_analytics.db')
cursor = conn.cursor()

# Update password hash for demo and harsh users to 'password123'
hash_val = '$2a$10$ok0UgIR7j0KYrJ6A9eZxAumd1Ot2SJRBTLjJlpR65sahQW8/5Dy0q'

cursor.execute("UPDATE users SET password_hash = ? WHERE email in ('demo@portfolio.com', 'harsh.demo@example.com')", (hash_val,))
conn.commit()

users = cursor.execute("SELECT id, name, email, password_hash, role, active FROM users").fetchall()
print("Updated users:")
for u in users:
    print(u)

conn.close()
