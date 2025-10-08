ALTER USER 'abarrey_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'ChangeMe123!';
ALTER USER 'abarrey_user'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'ChangeMe123!';
ALTER USER 'abarrey_user'@'::1' IDENTIFIED WITH mysql_native_password BY 'ChangeMe123!';
FLUSH PRIVILEGES;
