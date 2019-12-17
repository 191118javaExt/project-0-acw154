DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS account CASCADE;
SET search_path TO project0,postgres;


CREATE TABLE IF NOT EXISTS users (
	username VARCHAR(20) PRIMARY KEY,
	sha256_password VARCHAR(100) not null,
	role VARCHAR(20) not null,
	approval_status INTEGER not null default 0,
	account_id INTEGER
);

CREATE TABLE IF NOT EXISTS account (
	account_id SERIAL PRIMARY KEY,
	balance numeric(30, 2) not null default 0,
	transCounter INTEGER not null default 0
);
ALTER SEQUENCE account_account_id_seq RESTART WITH 1000 INCREMENT BY 2;
ALTER TABLE users ADD FOREIGN KEY (account_id) REFERENCES account (account_id);

DROP FUNCTION IF EXISTS SelectUsersByStatus;

CREATE OR REPLACE FUNCTION GetUsersByStatus (a_status INTEGER) RETURNS SETOF project0.users
AS
$$
	SELECT * FROM project0.users WHERE approval_status = a_status;
$$
LANGUAGE SQL;

UPDATE users SET approval_status = 1 WHERE username = 'superuser';
SELECT * FROM users;
SELECT * FROM account;