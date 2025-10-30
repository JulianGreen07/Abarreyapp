-- Script de inicialización mínimo para AbarreyApp
-- Crea la base de datos, tablas necesarias y un usuario administrador de ejemplo

-- init_db.sql (mínimo): sólo tablas que se usan en la app actualmente
-- Mantiene los campos tal como aparecen en tus capturas (users, products, llegadas, mermas)

CREATE DATABASE IF NOT EXISTS abarrey_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE abarrey_db;

-- Productos
CREATE TABLE IF NOT EXISTS products (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(255) NULL,
  stock VARCHAR(255) NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
  -- Nota: sin FK a branches; branch_id es un entero lógico con default 1
);

-- Usuarios
CREATE TABLE IF NOT EXISTS users (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100) NOT NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(50) NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1,
  password_hash VARCHAR(255) NULL,
  PRIMARY KEY (id)
);

-- Llegadas
CREATE TABLE IF NOT EXISTS llegadas (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  quantity DOUBLE NULL,
  recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NULL,
  PRIMARY KEY (id),
  KEY idx_llegadas_product (product_id)
  -- Nota: sin FK a branches; opcionalmente puedes añadir FK a products si lo deseas
);

-- Mermas
CREATE TABLE IF NOT EXISTS mermas (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  weight DECIMAL(8,2) NULL,
  recorded_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_mermas_product (product_id)
);

-- Usuario de ejemplo
INSERT INTO users (name, role, email, phone, password_hash, branch_id)
VALUES ('Administrador', 'Admin', 'admin@example.com', '0000000000', NULL, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ejemplo de productos
INSERT INTO products (name, category, stock, branch_id) VALUES
('Manzana', 'Fruta', '100', 1),
('Lechuga', 'Verdura', '50', 1)
ON DUPLICATE KEY UPDATE name = name;
