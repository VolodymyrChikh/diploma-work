CREATE TABLE lessons (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20),
    semester VARCHAR(10),
    day_of_week VARCHAR(20) NOT NULL,
    pair_number INTEGER,
    pair_label VARCHAR(10),
    time_start VARCHAR(10),
    time_end VARCHAR(10),
    week_type VARCHAR(20) NOT NULL,
    subject_name VARCHAR(255),
    lesson_type VARCHAR(50),
    room VARCHAR(50),
    teachers TEXT,
    raw_text TEXT,
    source_file VARCHAR(255)
);