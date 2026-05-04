ALTER TABLE subjects
    ADD COLUMN IF NOT EXISTS specialty_id BIGINT;

-- Backfill existing subjects with a default specialty (first available) to keep legacy rows linked.
UPDATE subjects s
SET specialty_id = default_specialty.id
FROM (
    SELECT id
    FROM specialties
    ORDER BY id
    LIMIT 1
) default_specialty
WHERE s.specialty_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_subjects_specialty_id ON subjects (specialty_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_subjects_specialty_id'
    ) THEN
        ALTER TABLE subjects
            ADD CONSTRAINT fk_subjects_specialty_id
                FOREIGN KEY (specialty_id)
                    REFERENCES specialties(id)
                    ON DELETE SET NULL;
    END IF;
END $$;

