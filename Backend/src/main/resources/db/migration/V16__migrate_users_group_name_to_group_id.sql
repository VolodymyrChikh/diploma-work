-- Move user profile groups to a dedicated foreign key column.
-- Backfill the new relation from the legacy group_name values first.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS group_id BIGINT;

UPDATE users u
SET group_id = g.id
FROM groups g
WHERE u.group_name = g.name
  AND u.group_id IS NULL;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_group_name_check;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS fk_users_group_name;

ALTER TABLE users
    ADD CONSTRAINT fk_users_group_id
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_users_group_id ON users (group_id);
