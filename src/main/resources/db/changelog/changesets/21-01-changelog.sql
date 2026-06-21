-- liquibase formatted sql

-- changeset Эржан:1782029187481-1
ALTER TABLE payments
    ADD decline_reason VARCHAR(255);

