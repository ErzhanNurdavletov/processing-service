-- liquibase formatted sql

-- changeset Эржан:1782208000622-1
ALTER TABLE accounts
    ADD closed_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE accounts
    ADD ended_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE accounts
    ADD opened_at TIMESTAMP WITHOUT TIME ZONE;

-- changeset Эржан:1782208000622-4
ALTER TABLE transactions
    ADD payment_id UUID;

-- changeset Эржан:1782208000622-5
ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_account_number UNIQUE (account_number);

-- changeset Эржан:1782208000622-6
ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_client UNIQUE (client_id);

-- changeset Эржан:1782208000622-7
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payments (id);

