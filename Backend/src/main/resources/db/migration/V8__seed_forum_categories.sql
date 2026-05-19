INSERT INTO categories (name, created_at, updated_at)
VALUES
    ('Події', now(), now()),
    ('Поради', now(), now()),
    ('FAQ', now(), now()),
    ('Гумор', now(), now())
ON CONFLICT (name) DO UPDATE
SET updated_at = now();
