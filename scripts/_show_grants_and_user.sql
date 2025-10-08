SELECT User, Host, plugin, authentication_string FROM mysql.user WHERE User = 'abarrey_user';
SHOW GRANTS FOR 'abarrey_user'@'localhost';
SHOW GRANTS FOR 'abarrey_user'@'127.0.0.1';
SHOW GRANTS FOR 'abarrey_user'@'::1';
SELECT USER(), CURRENT_USER();
