-- liquibase formatted sql

-- changeset Эржан:1781868346936-1
CREATE TABLE accounts
(
    id             UUID        NOT NULL,
    client_id      UUID        NOT NULL,
    account_number VARCHAR(22) NOT NULL,
    balance        DECIMAL     NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

-- changeset Эржан:1781868346936-2
CREATE TABLE payments
(
    id                UUID                        NOT NULL,
    debit_account_id  UUID                        NOT NULL,
    credit_account_id UUID                        NOT NULL,
    amount            DECIMAL(15, 2)              NOT NULL,
    currency          VARCHAR(255)                NOT NULL,
    status            VARCHAR(255)                NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment           VARCHAR(255),
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

-- changeset Эржан:1781868346936-3
CREATE TABLE transactions
(
    id                UUID           NOT NULL,
    debit_account_id  UUID           NOT NULL,
    credit_account_id UUID           NOT NULL,
    amount            DECIMAL(15, 2) NOT NULL,
    currency          VARCHAR(255)   NOT NULL,
    status            VARCHAR(255)   NOT NULL,
    comment           VARCHAR(255),
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

-- changeset Эржан:1781868346936-4
ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_CREDIT_ACCOUNT FOREIGN KEY (credit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781868346936-5
ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_DEBIT_ACCOUNT FOREIGN KEY (debit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781868346936-6
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CREDIT_ACCOUNT FOREIGN KEY (credit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781868346936-7
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_DEBIT_ACCOUNT FOREIGN KEY (debit_account_id) REFERENCES accounts (id);

