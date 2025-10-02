-- Script de inicialización mínimo para AbarreyApp
-- Crea la base de datos, tablas necesarias y un usuario administrador de ejemplo

CREATE DATABASE IF NOT EXISTS abarrey_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE abarrey_db;

-- tabla de sucursales (branches)
CREATE TABLE IF NOT EXISTS branches (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- tabla de productos
CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(255),
  price DECIMAL(10,2) DEFAULT 0.0,
  stock VARCHAR(64),
  branch_id INT NOT NULL,
  FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100),
  email VARCHAR(255),
  phone VARCHAR(64),
  password_hash TEXT,
  branch_id INT NOT NULL,
  FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- datos de ejemplo
INSERT INTO branches (name) VALUES ('Sucursal Central') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO branches (name) VALUES ('Sucursal Norte') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO branches (name) VALUES ('Sucursal Sur') ON DUPLICATE KEY UPDATE name = name;

-- usuario admin de ejemplo (sin hash -> fuerza migración al iniciar sesión con '1234')
INSERT INTO users (name, role, email, phone, password_hash, branch_id) VALUES
('admin', 'Administrador', 'admin@example.com', '0000000000', NULL, 1)
ON DUPLICATE KEY UPDATE name = name;

-- ejemplo de productos
INSERT INTO products (name, category, price, stock, branch_id) VALUES
('Manzana', 'Fruta', 10.50, '100', 1),
('Lechuga', 'Verdura', 5.00, '50', 1)
ON DUPLICATE KEY UPDATE name = name;
