-- Set PBKDF2 hash for users that currently have NULL or empty password_hash
UPDATE users
SET password_hash = '65536:c86nuVHknH+gY1LkyhNCVg==:GBbMoAzKZsYJ3ZXmZtgrVIon3zrGNLAlb/UHeTnNRko='
WHERE password_hash IS NULL OR password_hash = '';
