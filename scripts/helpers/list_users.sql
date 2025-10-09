SELECT id, name, role, IF(password_hash IS NULL,'<NULL>',CASE WHEN password_hash='' THEN '<EMPTY>' ELSE '<HASHED>' END) AS pwd_status, branch_id FROM users ORDER BY id;
