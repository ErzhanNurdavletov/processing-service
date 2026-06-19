-- liquibase formatted sql

-- changeset Эржан:1781850817137-1
ALTER TABLE payments
    ALTER COLUMN amount TYPE DECIMAL(15, 2) USING (amount::DECIMAL(15, 2));

-- changeset Эржан:1781850817137-2
ALTER TABLE transactions
    ALTER COLUMN amount TYPE DECIMAL(15, 2) USING (amount::DECIMAL(15, 2));

