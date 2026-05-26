-- 1. Створення нової таблиці для вибіркових груп (SelectiveGroup)
CREATE TABLE public.selective_groups
(
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    semester                INT          NOT NULL,
    min_selectable_subjects INT          NOT NULL DEFAULT 1,
    max_selectable_subjects INT          NOT NULL DEFAULT 1,
    specialty_id            BIGINT       NOT NULL, -- Додане поле для зв'язку ManyToOne
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Зовнішній ключ на таблицю specialties із каскадним видаленням
    CONSTRAINT fk_selective_groups_specialty
        FOREIGN KEY (specialty_id)
            REFERENCES public.specialties (id)
            ON DELETE CASCADE
);

-- 2. Оновлення таблиці subjects: додавання зовнішнього ключа на нову таблицю вибіркових груп
ALTER TABLE public.subjects
    ADD COLUMN selective_group_id BIGINT NULL;

-- 3. Створення обмеження зовнішнього ключа для таблиці subjects
-- Використовуємо ON DELETE SET NULL: якщо група вибору буде видалена, предмети не зникнуть,
-- а просто очистять свій лінк і перетворяться на звичайні базові дисципліни
ALTER TABLE public.subjects
    ADD CONSTRAINT fk_subjects_selective_group
        FOREIGN KEY (selective_group_id)
            REFERENCES public.selective_groups (id)
            ON DELETE SET NULL;

-- 4. Створення індексів для оптимізації швидкості SQL-запитів (JOIN та фільтрацій)
CREATE INDEX idx_selective_groups_specialty_id ON public.selective_groups (specialty_id);
CREATE INDEX idx_subjects_selective_group_id ON public.subjects (selective_group_id);