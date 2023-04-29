BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transactions CASCADE;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transaction_id CASCADE;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transaction_id
	INCREMENT BY 1
	START WITH 3001
	NO MAXVALUE;

CREATE TABLE transactions (
	transaction_id int NOT null DEFAULT nextval('seq_transaction_id'),
	user_1_id int NOT NULL,
	user_2_id int NOT NULL,
	transfer_ammount numeric(13,2) NOT NULL,
	transfer_status varchar(25) NOT NULL,
	CONSTRAINT PK_transaction PRIMARY KEY (transaction_id)
);

--ALTER TABLE table_name CONSTRAINT constraint_name FOREIGN KEY column_name REFERENCES parent_table(parent_column);

<<<<<<< HEAD
alter table transactions add constraint fk_user_1 foreign key (user_1_id) references tenmo_user(user_id); 
alter table transactions add constraint fk_user_2 foreign key (user_2_id) references tenmo_user(user_id);


COMMIT;

SELECT * FROM tenmo_user;
SELECT * FROM account;

DROP TABLE account, tenmo_user CASCADE

SELECT balance from account WHERE account_id = 2002;

SELECT account_id, user_id, balance FROM account WHERE account_id = 2002

SELECT balance - 500 FROM account WHERE user_id = 1002;
SELECT balance + 500 FROM account WHERE user_id = 1003;

UPDATE account SET balance = balance - 500 WHERE user_id = 1001;
UPDATE account SET balance = balance + 500 WHERE user_id = 1002;
SELECT * FROM account;

SELECT user_id FROM tenmo_user WHERE username = 'chris'

SELECT account_id, account.user_id, balance
FROM account 
JOIN tenmo_user ON tenmo_user.user_id = account.user_id 
WHERE tenmo_user.username = 'joy';

INSERT INTO
transactions (transfer_ammount, user_1_id, user_2_id, transfer_status) 
VALUES (300 , (SELECT user_id FROM tenmo_user WHERE username = 'joy'),(SELECT user_id FROM tenmo_user WHERE username = 'mark'), 'approved') RETURNING transaction_id;

SELECT * FROM transactions WHERE user_1_id = 1002 OR user_2_id = 1002;

SELECT * FROM account;

SELECT * FROM tenmo_user WHERE user_id = 1002;

SELECT * FROM transactions;

=======
alter table transactions add constraint fk_user_1 foreign key (user_1_id) references tenmo_user(user_id);
alter table transactions add constraint fk_user_2 foreign key (user_2_id) references tenmo_user(user_id);
>>>>>>> 7746bafcfbba2342a5350b515bd1f8556ada9561

