-- ============================================
-- RecommendoBot Admin User Seed Script
-- Creates default admin user for testing
-- ============================================

USE laptops_data;

-- Clear existing users (optional - remove if you want to keep users)
-- TRUNCATE TABLE users;

-- ============================================
-- CREATE ADMIN USER
-- Username: admin
-- Password: admin123 (BCrypt hashed)
-- Role: ADMIN
-- ============================================

-- BCrypt hash for "admin123"
-- Generated using: BCryptPasswordEncoder with strength 10
INSERT INTO users (username, password, email, role, enabled, created_at)
VALUES (
    'admin',
    '$2a$10$YQ0XHJZt3AjXYPRhB8YnA.vF8XPFxWmZQqGJhKXxKxC6VvGJFnHNW',
    'admin@recommendobot.com',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP
) ON DUPLICATE KEY UPDATE username = username;

-- ============================================
-- CREATE TEST USER
-- Username: testuser
-- Password: password123 (BCrypt hashed)
-- Role: USER
-- ============================================

-- BCrypt hash for "password123"
INSERT INTO users (username, password, email, role, enabled, created_at)
VALUES (
    'testuser',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'test@recommendobot.com',
    'USER',
    TRUE,
    CURRENT_TIMESTAMP
) ON DUPLICATE KEY UPDATE username = username;

-- ============================================
-- VERIFICATION
-- ============================================
SELECT 
    id,
    username,
    email,
    role,
    enabled,
    created_at
FROM users;

SELECT 'Admin and test users created successfully!' AS Status;
SELECT 'Login with: admin / admin123 (ADMIN)' AS AdminCredentials;
SELECT 'Login with: testuser / password123 (USER)' AS UserCredentials;
