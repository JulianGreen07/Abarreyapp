-- Extended initialization: users, products, mermas tables
CREATE DATABASE IF NOT EXISTS abarrey_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE abarrey_db;

-- Branches table
CREATE TABLE IF NOT EXISTS branches (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO branches (code, name) VALUES
('admin1','Sucursal Admin 1'),
('admin2','Sucursal Admin 2'),
('admin3','Sucursal Admin 3');

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100) NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(50),
  branch_id INT NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(100),
  price DECIMAL(10,2),
  stock VARCHAR(255),
  branch_id INT NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mermas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  weight DECIMAL(8,2) NOT NULL,
  branch_id INT NOT NULL DEFAULT 1,
  recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (name, role, email, phone) VALUES
('Juan Pérez', 'Administrador', 'juan.perez@example.com', '662123456'),
('María López', 'Usuario', 'maria.lopez@example.com', '662987654');

INSERT INTO products (name, category, price, stock) VALUES
('Manzana', 'Fruta', 25.50, '100 kg'),
('Plátano', 'Fruta', 15.00, '150 kg'),
('Tomate', 'Verdura', 30.00, '80 kg'),
('Lechuga', 'Verdura', 12.00, '50 pz');

INSERT INTO mermas (product_id, weight) VALUES
(1, 0.5),(2,0.3),(3,0.2);

-- Set foreign keys for branch relations (if not already present)
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE;
ALTER TABLE products ADD CONSTRAINT IF NOT EXISTS fk_products_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE;
ALTER TABLE mermas ADD CONSTRAINT IF NOT EXISTS fk_mermas_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE;
