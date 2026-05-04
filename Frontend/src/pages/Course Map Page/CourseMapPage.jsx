import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import styles from './CourseMapPage.module.css';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';

const API_BASE_URL = 'http://localhost:9000';

const BLOCK_TYPE_LABELS = {
    HUMANITARIAN: 'Гуманітарний блок',
    SCIENTIFIC: 'Природничо-науковий блок',
    PROFESSIONAL: 'Фаховий блок',
    PDFC: 'Фахова дисципліна вільного вибору'
};

const EXAM_TYPE_LABELS = {
    EXAM: 'Іспит',
    CREDIT: 'Залік',
    COURSEWORK: 'Курсова робота',
    DIPLOMA: 'Дипломна робота',
    MAGISTER: 'Магістерська робота',
    DIFFERENTIATED_CREDIT: 'Диференційований залік'
};

function normalizePageData(payload) {
    if (Array.isArray(payload)) {
        return payload;
    }

    if (Array.isArray(payload?.content)) {
        return payload.content;
    }

    return [];
}

function CourseMapPage() {
    const [searchParams] = useSearchParams();
    const levelFromUrl = searchParams.get('level');
    const specialtyFromUrl = searchParams.get('specialty');

    const [degreeLevel, setDegreeLevel] = useState(levelFromUrl === 'master' ? 'master' : 'bachelor');
    const [specialties, setSpecialties] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [selectedSpecialtyId, setSelectedSpecialtyId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        let isMounted = true;

        async function loadData() {
            setLoading(true);
            setError('');

            try {
                const [specialtiesRes, subjectsRes] = await Promise.all([
                    fetch(`${API_BASE_URL}/specialties?size=200`),
                    fetch(`${API_BASE_URL}/subjects?size=2000&sort=semester,asc`)
                ]);

                if (!specialtiesRes.ok) {
                    throw new Error('Не вдалося завантажити спеціальності.');
                }

                if (!subjectsRes.ok) {
                    throw new Error('Не вдалося завантажити предмети.');
                }

                const specialtiesPayload = await specialtiesRes.json();
                const subjectsPayload = await subjectsRes.json();

                if (!isMounted) {
                    return;
                }

                const loadedSpecialties = normalizePageData(specialtiesPayload);
                const loadedSubjects = normalizePageData(subjectsPayload);

                setSpecialties(loadedSpecialties);
                setSubjects(loadedSubjects);

                if (loadedSpecialties.length > 0) {
                    const matchedByQuery = loadedSpecialties.find((specialty) => {
                        const number = String(specialty?.number || '').toLowerCase();
                        const id = String(specialty?.id || '');
                        const name = String(specialty?.name || '').toLowerCase();
                        const searched = String(specialtyFromUrl || '').toLowerCase();

                        return number === searched || id === searched || name === searched;
                    });

                    setSelectedSpecialtyId((matchedByQuery || loadedSpecialties[0]).id);
                }
            } catch (loadError) {
                if (isMounted) {
                    setError(loadError.message || 'Виникла помилка під час завантаження карти курсів.');
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        }

        loadData();

        return () => {
            isMounted = false;
        };
    }, [specialtyFromUrl]);

    const selectedSpecialty = useMemo(
        () => specialties.find((specialty) => specialty.id === selectedSpecialtyId) || null,
        [specialties, selectedSpecialtyId]
    );

    const filteredSubjects = useMemo(() => {
        return subjects
            .filter((subject) => subject.specialtyId === selectedSpecialtyId)
            .filter((subject) => {
                if (degreeLevel === 'master') {
                    return subject.degreeLevel === 'MASTER';
                }

                return subject.degreeLevel !== 'MASTER';
            })
            .sort((a, b) => {
                if ((a.semester || 0) !== (b.semester || 0)) {
                    return (a.semester || 0) - (b.semester || 0);
                }

                return (a.name || '').localeCompare(b.name || '');
            });
    }, [subjects, selectedSpecialtyId, degreeLevel]);

    const subjectsBySemester = useMemo(() => {
        return filteredSubjects.reduce((acc, subject) => {
            const semester = subject.semester || 0;
            if (!acc[semester]) {
                acc[semester] = [];
            }
            acc[semester].push(subject);
            return acc;
        }, {});
    }, [filteredSubjects]);

    const semesters = degreeLevel === 'bachelor'
        ? [1, 2, 3, 4, 5, 6, 7, 8]
        : [1, 2, 3, 4];

    return (
        <>
            <Header />
            <main className={styles.pageWrapper}>
                <section className={styles.hero}>
                    <h1 className={styles.title}>Карта курсів</h1>
                    <p className={styles.subtitle}>
                        Обирай рівень навчання та спеціальність, щоб побачити предмети по семестрах,
                        з типом блоку і формою контролю.
                    </p>
                </section>

                <section className={styles.controls}>
                    <div className={styles.levelSwitch}>
                        <button
                            className={`${styles.levelButton} ${degreeLevel === 'bachelor' ? styles.levelButtonActive : ''}`}
                            onClick={() => setDegreeLevel('bachelor')}
                            type="button"
                        >
                            Бакалавр
                        </button>
                        <button
                            className={`${styles.levelButton} ${degreeLevel === 'master' ? styles.levelButtonActive : ''}`}
                            onClick={() => setDegreeLevel('master')}
                            type="button"
                        >
                            Магістр
                        </button>
                    </div>

                    <div className={styles.specialtySelectWrapper}>
                        <label htmlFor="specialtySelect" className={styles.label}>Спеціальність</label>
                        <select
                            id="specialtySelect"
                            className={styles.specialtySelect}
                            value={selectedSpecialtyId || ''}
                            onChange={(event) => setSelectedSpecialtyId(Number(event.target.value))}
                        >
                            {specialties.map((specialty) => (
                                <option key={specialty.id} value={specialty.id}>
                                    {specialty.number} - {specialty.name}
                                </option>
                            ))}
                        </select>
                    </div>
                </section>

                <section className={styles.legend}>
                    <h2 className={styles.legendTitle}>Легенда блоків</h2>
                    <div className={styles.legendItems}>
                        <span className={`${styles.legendItem} ${styles.humanitarian}`}>Гуманітарний</span>
                        <span className={`${styles.legendItem} ${styles.scientific}`}>Природничо-науковий</span>
                        <span className={`${styles.legendItem} ${styles.professional}`}>Фаховий</span>
                        <span className={`${styles.legendItem} ${styles.pdfc}`}>Вільний вибір</span>
                    </div>
                </section>

                {loading && <p className={styles.stateMessage}>Завантаження карти курсів...</p>}
                {!loading && error && <p className={styles.errorMessage}>{error}</p>}

                {!loading && !error && (
                    <section className={styles.semestersGrid}>
                        {semesters.map((semester) => {
                            const semesterSubjects = subjectsBySemester[semester] || [];

                            return (
                                <article key={semester} className={styles.semesterCard}>
                                    <div className={styles.semesterHeader}>
                                        <h3>Семестр {semester}</h3>
                                        <span>{semesterSubjects.length} предмет(ів)</span>
                                    </div>

                                    {semesterSubjects.length === 0 && (
                                        <p className={styles.emptySemesterText}>
                                            Для цього семестру поки немає доданих предметів.
                                        </p>
                                    )}

                                    {semesterSubjects.length > 0 && (
                                        <ul className={styles.subjectList}>
                                            {semesterSubjects.map((subject) => (
                                                <li key={subject.id} className={styles.subjectItem}>
                                                    <div className={styles.subjectTopLine}>
                                                        <h4>{subject.name}</h4>
                                                        {subject.credits != null && (
                                                            <span className={styles.credits}>{subject.credits} кр.</span>
                                                        )}
                                                    </div>

                                                    <div className={styles.badges}>
                                                        <span
                                                            className={`${styles.badge} ${styles.blockBadge} ${styles[(subject.blockType || '').toLowerCase()] || ''}`}
                                                        >
                                                            {BLOCK_TYPE_LABELS[subject.blockType] || 'Блок не вказано'}
                                                        </span>
                                                        <span className={`${styles.badge} ${styles.examBadge}`}>
                                                            {EXAM_TYPE_LABELS[subject.examType] || 'Тип контролю не вказано'}
                                                        </span>
                                                    </div>

                                                    {subject.taughtBy && (
                                                        <p className={styles.meta}>Викладач: {subject.taughtBy}</p>
                                                    )}

                                                    {subject.description && (
                                                        <p className={styles.description}>{subject.description}</p>
                                                    )}

                                                    {subject.syllabusLink && (
                                                        <a
                                                            href={subject.syllabusLink}
                                                            className={styles.syllabusLink}
                                                            target="_blank"
                                                            rel="noreferrer"
                                                        >
                                                            Силабус
                                                        </a>
                                                    )}
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </article>
                            );
                        })}
                    </section>
                )}

                {!loading && !error && filteredSubjects.length === 0 && selectedSpecialty && (
                    <p className={styles.stateMessage}>
                        Для {selectedSpecialty.name} поки немає предметів на рівні
                        {' '}
                        {degreeLevel === 'bachelor' ? 'бакалавра' : 'магістра'}.
                    </p>
                )}
            </main>
            <Footer />
        </>
    );
}

export default CourseMapPage;
