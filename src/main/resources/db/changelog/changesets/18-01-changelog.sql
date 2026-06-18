-- liquibase formatted sql

-- changeset Эржан:1781784173460-1
CREATE TABLE accounts
(
    id             UUID         NOT NULL,
    client_id      UUID         NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    cards          UUID[],
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

-- changeset Эржан:1781784173460-2
CREATE TABLE payments
(
    id                UUID                        NOT NULL,
    debit_account_id  UUID                        NOT NULL,
    credit_account_id UUID                        NOT NULL,
    amount            DECIMAL(2, 15)              NOT NULL,
    currency          VARCHAR(255)                NOT NULL,
    status            VARCHAR(255)                NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment           VARCHAR(255),
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

-- changeset Эржан:1781784173460-3
CREATE TABLE transactions
(
    id                UUID           NOT NULL,
    debit_account_id  UUID           NOT NULL,
    credit_account_id UUID           NOT NULL,
    amount            DECIMAL(2, 15) NOT NULL,
    currency          VARCHAR(255)   NOT NULL,
    status            SMALLINT       NOT NULL,
    comment           VARCHAR(255),
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

-- changeset Эржан:1781784173460-4
ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_CREDIT_ACCOUNT FOREIGN KEY (credit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781784173460-5
ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_DEBIT_ACCOUNT FOREIGN KEY (debit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781784173460-6
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CREDIT_ACCOUNT FOREIGN KEY (credit_account_id) REFERENCES accounts (id);

-- changeset Эржан:1781784173460-7
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_DEBIT_ACCOUNT FOREIGN KEY (debit_account_id) REFERENCES accounts (id);

