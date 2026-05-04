ALTER TABLE subjects
    ADD COLUMN IF NOT EXISTS degree_level VARCHAR(32);

UPDATE subjects
SET degree_level = 'BACHELOR'
WHERE degree_level IS NULL;

ALTER TABLE subjects
    ALTER COLUMN degree_level SET NOT NULL;
