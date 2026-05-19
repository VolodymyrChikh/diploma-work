ALTER TABLE posts
    ADD COLUMN IF NOT EXISTS slug varchar(255);

UPDATE posts
SET slug = id::text
WHERE slug IS NULL;

ALTER TABLE posts
    ALTER COLUMN slug SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_post_slug ON posts (slug);
