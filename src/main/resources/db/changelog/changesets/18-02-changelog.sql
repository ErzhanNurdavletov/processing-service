-- liquibase formatted sql

-- changeset Эржан:1781784958973-1
ALTER TABLE accounts
    ALTER COLUMN account_number TYPE VARCHAR(22) USING (account_number::VARCHAR(22));

