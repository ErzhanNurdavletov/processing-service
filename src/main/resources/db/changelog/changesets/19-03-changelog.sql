-- liquibase formatted sql

-- changeset Эржан:1781854753250-1
ALTER TABLE accounts
    ADD balance DECIMAL;

-- changeset Эржан:1781854753250-2
ALTER TABLE accounts
    ALTER COLUMN balance SET NOT NULL;

