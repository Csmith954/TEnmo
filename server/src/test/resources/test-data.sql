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

alter table transactions add constraint fk_user_1 foreign key (user_1_id) references tenmo_user(user_id);
alter table transactions add constraint fk_user_2 foreign key (user_2_id) references tenmo_user(user_id);

INSERT INTO tenmo_user (username, password_hash)
VALUES ('bob', '$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2'),
       ('user', '$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy');

INSERT INTO account (user_id, balance)
VALUES (1001, 1000),
        (1002, 1000);

INSERT INTO transactions (user_1_id, user_2_id, transfer_ammount, transfer_status)
VALUES (1001, 1002, 200, 'Approved'),
        (1002, 1001, 100, 'Approved'),
        (1001, 1002, 100, 'pending');


COMMIT;