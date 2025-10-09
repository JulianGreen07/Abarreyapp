-- init.sql: crea la base de datos y la tabla de usuarios
CREATE DATABASE IF NOT EXISTS abarrey_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE abarrey_db;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100) NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (name, role, email, phone) VALUES
('Juan Pérez', 'Administrador', 'juan.perez@example.com', '662123456'),
('María López', 'Usuario', 'maria.lopez@example.com', '662987654');
