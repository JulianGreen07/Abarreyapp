CREATE DATABASE IF NOT EXISTS abarrey_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE abarrey_db;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100) NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1,
  password_hash VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(255),
  stock VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mermas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  weight DECIMAL(8,2),
  recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL DEFAULT 1,
  KEY idx_mermas_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (name, role, email, phone) VALUES
('Juan Pérez', 'Administrador', 'juan.perez@example.com', '662123456'),
('María López', 'Usuario', 'maria.lopez@example.com', '662987654');

INSERT INTO products (name, category, stock) VALUES
('Manzana', 'Fruta', '100 kg'),
('Plátano', 'Fruta', '150 kg'),
('Tomate', 'Verdura', '80 kg'),
('Lechuga', 'Verdura', '50 pz');

INSERT INTO mermas (product_id, weight) VALUES (1, 0.5),(2,0.3),(3,0.2);
