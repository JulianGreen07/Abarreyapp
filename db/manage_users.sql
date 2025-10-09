-- manage_users.sql
-- Queries to list users, create admin users, and set password_hash to NULL for legacy login
-- IMPORTANT: replace 'abarrey_db' with your database name if different.

-- 1) List all users with their branch name
SELECT u.id, u.name, u.role, u.email, u.phone, COALESCE(b.name,'(sin branch)') AS branch
FROM users u
LEFT JOIN branches b ON u.branch_id = b.id
ORDER BY b.id, u.name;

-- 2) List users for a specific branch (replace 1 by branch_id you want)
SELECT id, name, role, email, phone FROM users WHERE branch_id = 1 ORDER BY name;

-- 3) Set password_hash to NULL for all users (so legacy "1234" login is used until they login and are migrated)
-- Run this only if you want to force legacy-mode for everyone
UPDATE users SET password_hash = NULL;

-- 4) Set password_hash = NULL for a specific branch
-- UPDATE users SET password_hash = NULL WHERE branch_id = 2;

-- 5) Create admin users for branches 1,2,3 (does not set password_hash -> will be NULL => login with '1234')
INSERT INTO users (name, role, email, phone, branch_id) VALUES
('admin1', 'admin', 'admin1@example.com', NULL, 1),
('admin2', 'admin', 'admin2@example.com', NULL, 2),
('admin3', 'admin', 'admin3@example.com', NULL, 3);

-- 6) (Optional) Check counts per branch
SELECT b.id AS branch_id, b.name AS branch_name, COUNT(u.id) AS users_count
FROM branches b
LEFT JOIN users u ON u.branch_id = b.id
GROUP BY b.id, b.name
ORDER BY b.id;
