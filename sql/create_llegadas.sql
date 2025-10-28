-- Migración: crear la tabla 'llegadas' usada por ArrivalDAO
-- Ejecuta esto en tu base MySQL (p.ej. mysql -u root -p abarrey_db < create_llegadas.sql)

CREATE TABLE IF NOT EXISTS llegadas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  quantity DOUBLE NULL,
  recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NULL,
  KEY idx_llegadas_product (product_id)
);
