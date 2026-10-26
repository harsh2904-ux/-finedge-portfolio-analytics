-- V3__seed_initial_data.sql
-- Seeds default users and initial ownership mapping

INSERT INTO users (id, name, email, password_hash, role, active, created_at, updated_at)
VALUES 
    (1, 'Harsh', 'harsh@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Standard User', 'user@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Associate initial portfolio with user 1 if portfolio exists without user
UPDATE portfolios SET user_id = 1 WHERE user_id IS NULL;
