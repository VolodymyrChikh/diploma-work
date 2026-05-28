-- Populate academic groups from existing user group names so the new relation can be added safely
INSERT INTO groups (name)
SELECT DISTINCT u.group_name
FROM users u
WHERE u.group_name IS NOT NULL
  AND u.group_name <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM groups g
      WHERE g.name = u.group_name
  );

-- Make group names unique because users reference groups.name
ALTER TABLE groups
    ADD CONSTRAINT uq_groups_name UNIQUE (name);

-- Add foreign key from users.group_name to groups.name
ALTER TABLE users
    ADD CONSTRAINT fk_users_group_name
        FOREIGN KEY (group_name)
            REFERENCES groups (name)
            ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_users_group_name ON users (group_name);

