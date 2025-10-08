CREATE DATABASE IF NOT EXISTS abarrey_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'abarrey_user'@'localhost' IDENTIFIED BY 'ChangeMe123!';
GRANT ALL PRIVILEGES ON abarrey_db.* TO 'abarrey_user'@'localhost';
FLUSH PRIVILEGES;
