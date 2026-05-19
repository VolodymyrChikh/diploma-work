ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS type VARCHAR(32);

UPDATE categories
SET type = 'FORUM'
WHERE name IN ('Навчання', 'Події', 'Поради', 'FAQ', 'Гумор');

UPDATE categories
SET type = 'MEDIA'
WHERE name IN ('Документи', 'Кар''єра', 'Медіатека');

UPDATE categories
SET type = 'GENERAL'
WHERE type IS NULL;

ALTER TABLE categories
    ALTER COLUMN type SET DEFAULT 'GENERAL',
    ALTER COLUMN type SET NOT NULL;

DROP INDEX IF EXISTS idx_categories_name;

CREATE UNIQUE INDEX IF NOT EXISTS idx_categories_name_type
    ON categories (name, type);
