
-- Terminates active user session on DB mtl 375
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'mtl375'
AND pid <> pg_backend_pid();

-- DROP and CREATE new mtl 375 DB
DROP DATABASE IF EXISTS mtl375;
CREATE DATABASE mtl375;

-- Grant all privileges on DB mtl375 to user postgres
GRANT ALL PRIVILEGES ON DATABASE mtl375 TO myUser;

-- Quit database
\q

