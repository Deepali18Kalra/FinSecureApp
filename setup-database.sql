-- FinSecure Database Setup Script
-- Run this script in MySQL to create and initialize the database

-- Create database
CREATE DATABASE IF NOT EXISTS finsecure;

-- Use the database
USE finsecure;

-- Show current tables (for verification)
SHOW TABLES;

-- Note: Hibernate will automatically create the following tables on first run:
-- - app_user
-- - employee
-- - employee_bank_account
-- - employee_card
-- - employee_investment
-- - salary_job
-- - salary_record
-- - company_bank_account
-- - company_mutual_fund
-- - etc.

-- Verify credentials from application.properties:
-- Username: root
-- Password: root
-- Connection URL: jdbc:mysql://127.0.0.1:3306/finsecure

-- Optional: Create a test user (will be encrypted in real app)
-- INSERT INTO app_user (username, password, role, failed_login_attempts_count, is_account_locked)
-- VALUES ('admin', 'hashedPassword', 0, 'ADMIN', 0, false);

-- To verify users after signup/login:
-- SELECT * FROM app_user;
-- SELECT user_id, username, role, is_account_locked FROM app_user;

-- To clear all data and reset (use with caution):
-- DELETE FROM app_user;
-- ALTER TABLE app_user AUTO_INCREMENT = 1;

