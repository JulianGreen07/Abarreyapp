-- init_migration_safe.sql
CREATE DATABASE IF NOT EXISTS abarrey_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE abarrey_db;

-- Crear tabla branches si no existe
CREATE TABLE IF NOT EXISTS branches (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserts seguros (si ya existen, los ignora)
INSERT IGNORE INTO branches (name) VALUES ('admin1'), ('admin2'), ('admin3');

-- Añadir columna branch_id a users si la tabla existe y la columna no existe
SELECT COUNT(*) INTO @tbl_exists FROM INFORMATION_SCHEMA.TABLES
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='users';
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='users' AND COLUMN_NAME='branch_id';
SET @sql = IF(@tbl_exists>0 AND @col_exists=0,
  'ALTER TABLE users ADD COLUMN branch_id INT NOT NULL DEFAULT 1',
  'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
-- Asegurar que todas las filas tengan branch_id
SET @update_sql = IF(@tbl_exists>0, 'UPDATE users SET branch_id = 1 WHERE branch_id IS NULL', 'SELECT "skip"');
PREPARE st2 FROM @update_sql; EXECUTE st2; DEALLOCATE PREPARE st2;

-- Añadir columna password_hash a users si no existe (para almacenar contraseñas hashed)
SELECT COUNT(*) INTO @col_pwd_exists FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='users' AND COLUMN_NAME='password_hash';
SET @sql = IF(@tbl_exists>0 AND @col_pwd_exists=0,
  'ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NULL',
  'SELECT "skip"');
PREPARE stmtpwd FROM @sql; EXECUTE stmtpwd; DEALLOCATE PREPARE stmtpwd;

-- Añadir columna branch_id a products si la tabla existe y la columna no existe
SELECT COUNT(*) INTO @tbl_exists FROM INFORMATION_SCHEMA.TABLES
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='products';
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='products' AND COLUMN_NAME='branch_id';
SET @sql = IF(@tbl_exists>0 AND @col_exists=0,
  'ALTER TABLE products ADD COLUMN branch_id INT NOT NULL DEFAULT 1',
  'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @update_sql = IF(@tbl_exists>0, 'UPDATE products SET branch_id = 1 WHERE branch_id IS NULL', 'SELECT "skip"');
PREPARE st3 FROM @update_sql; EXECUTE st3; DEALLOCATE PREPARE st3;

-- Añadir columna branch_id a mermas si la tabla existe y la columna no existe
SELECT COUNT(*) INTO @tbl_exists FROM INFORMATION_SCHEMA.TABLES
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='mermas';
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='mermas' AND COLUMN_NAME='branch_id';
SET @sql = IF(@tbl_exists>0 AND @col_exists=0,
  'ALTER TABLE mermas ADD COLUMN branch_id INT NOT NULL DEFAULT 1',
  'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @update_sql = IF(@tbl_exists>0, 'UPDATE mermas SET branch_id = 1 WHERE branch_id IS NULL', 'SELECT "skip"');
PREPARE st4 FROM @update_sql; EXECUTE st4; DEALLOCATE PREPARE st4;

-- Añadir claves foráneas si no existen (users, products, mermas)
-- users -> branches
SELECT COUNT(*) INTO @fk_exists FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='users' AND CONSTRAINT_NAME='fk_users_branch';
SET @sql = IF(@fk_exists=0 AND (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='users' AND COLUMN_NAME='branch_id')>0,
 'ALTER TABLE users ADD CONSTRAINT fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id)',
 'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- products -> branches
SELECT COUNT(*) INTO @fk_exists FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='products' AND CONSTRAINT_NAME='fk_products_branch';
SET @sql = IF(@fk_exists=0 AND (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='products' AND COLUMN_NAME='branch_id')>0,
 'ALTER TABLE products ADD CONSTRAINT fk_products_branch FOREIGN KEY (branch_id) REFERENCES branches(id)',
 'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- mermas -> branches
SELECT COUNT(*) INTO @fk_exists FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
 WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='mermas' AND CONSTRAINT_NAME='fk_mermas_branch';
SET @sql = IF(@fk_exists=0 AND (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='abarrey_db' AND TABLE_NAME='mermas' AND COLUMN_NAME='branch_id')>0,
 'ALTER TABLE mermas ADD CONSTRAINT fk_mermas_branch FOREIGN KEY (branch_id) REFERENCES branches(id)',
 'SELECT "skip"');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FIN
