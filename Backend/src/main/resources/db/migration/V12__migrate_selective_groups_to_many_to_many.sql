-- 1. Створення проміжної таблиці для зв'язку ManyToMany між selective_groups та specialties
CREATE TABLE public.selective_groups_specialties
(
    selective_group_id BIGINT NOT NULL,
    specialty_id       BIGINT NOT NULL,

    PRIMARY KEY (selective_group_id, specialty_id),

    CONSTRAINT fk_sel_groups_spec_group
        FOREIGN KEY (selective_group_id)
            REFERENCES public.selective_groups (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_sel_groups_spec_specialty
        FOREIGN KEY (specialty_id)
            REFERENCES public.specialties (id)
            ON DELETE CASCADE
);

-- 2. (Опціонально) Перенесення даних, що існують, зі старої схеми в нову проміжну таблицю
-- Якщо на продакшені/тесті вже були дані, цей крок урятує їх від утрати:
INSERT INTO public.selective_groups_specialties (selective_group_id, specialty_id)
SELECT id, specialty_id
FROM public.selective_groups
WHERE specialty_id IS NOT NULL;

-- 3. Видалення старого обмеження зовнішнього ключа та індексу з таблиці selective_groups
ALTER TABLE public.selective_groups
DROP CONSTRAINT fk_selective_groups_specialty;

DROP INDEX IF EXISTS idx_selective_groups_specialty_id;

-- 4. Видалення полів, які були вилучені з Java-сутності
ALTER TABLE public.selective_groups
DROP COLUMN specialty_id,
    DROP COLUMN min_selectable_subjects,
    DROP COLUMN max_selectable_subjects;

-- 5. Створення індексу для нової проміжної таблиці (для оптимізації зворотних JOIN-ів за specialty_id)
CREATE INDEX idx_sel_groups_spec_specialty_id ON public.selective_groups_specialties (specialty_id);