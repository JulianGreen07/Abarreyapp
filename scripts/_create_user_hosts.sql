-- Create abarrey_user entries for IPv4 and IPv6 loopback addresses
CREATE USER IF NOT EXISTS 'abarrey_user'@'127.0.0.1' IDENTIFIED BY 'ChangeMe123!';
GRANT ALL PRIVILEGES ON abarrey_db.* TO 'abarrey_user'@'127.0.0.1';
CREATE USER IF NOT EXISTS 'abarrey_user'@'::1' IDENTIFIED BY 'ChangeMe123!';
GRANT ALL PRIVILEGES ON abarrey_db.* TO 'abarrey_user'@'::1';
FLUSH PRIVILEGES;
