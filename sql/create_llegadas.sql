-- Migration: create 'llegadas' table used by ArrivalDAO
-- Run this against your MySQL database (e.g. mysql -u root -p abarrey_db < create_llegadas.sql)

CREATE TABLE IF NOT EXISTS llegadas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  quantity DOUBLE NOT NULL,
  recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  branch_id INT NOT NULL,
  CONSTRAINT fk_llegadas_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_llegadas_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT ON UPDATE CASCADE
);
