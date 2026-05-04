-- Fix syllabus_link column type from TEXT to VARCHAR(1000)
-- This resolves the mismatch between @Lob annotation removal in Subject entity
ALTER TABLE subjects
    ALTER COLUMN syllabus_link SET DATA TYPE VARCHAR(1000);

