import { useEffect, useRef } from 'react';
import styles from './AboutSpecialtyPage.module.css';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import AboutSpecialty from '../../components/AboutSpecialtyContainer/AboutSpecialty';

function AboutSpecialtyPage() {
    const specialtyRefs = {
        'A4': useRef(null),
        'F1': useRef(null),
        'F3': useRef(null),
        'F4': useRef(null),
        'F5': useRef(null)
    };

    useEffect(() => {
        const hash = window.location.hash;
        const specialtyNumber = hash.replace('#specialty-', '');
        
        if (specialtyRefs[specialtyNumber]?.current) {
            setTimeout(() => {
                specialtyRefs[specialtyNumber].current.scrollIntoView({ 
                    behavior: 'smooth',
                    block: 'start' 
                });
            }, 100);
        }
    }, []);

    const text014 = (
            <>
                Ця освітня програма спрямована на <span className={styles.highlightedWords}>підготовку фахівців</span>, 
                здатних ефективно поєднувати <span className={styles.highlightedWords}>знання з інформатики та педагогіки</span>. 
                Випускники отримують поглиблені теоретичні та практичні знання, 
                що дозволяють їм викладати інформатику в закладах середньої освіти 
                та впроваджувати інноваційні освітні технології.
            </>
        );

    const text113 = (
            <>
                Ця програма формує мислення аналітика, який може працювати з абстрактними моделями та реальними задачами. 
                Прикладна математика — це серце сучасних технологій, яке дозволяє <span className={styles.highlightedWords}>створювати математичні моделі</span> для розв'язання задач у науці, <span className={styles.highlightedWords}>інженерії</span>, економіці та IT.
            </>
        );

    const text122 = (
            <>
              Мрієш створювати <span className={styles.highlightedWords}>власні програми</span>, розробляти штучний інтелект або працювати з передовими комп'ютерними технологіями?
               Комп'ютерні науки — це ключова <span className={styles.highlightedWords}>основа будь-якої сучасної IT-розробки</span>. Обираючи цю спеціальність, ти отримаєш знання та навички, щоб будувати майбутнє цифрового світу.
            </>
        );

    const text124 = (
            <>
              Системний аналітик — це фахівець, який здатен бачити повну картину складних процесів. Він поєднує глибокі технічні знання з чітким <span className={styles.highlightedWords}>розумінням бізнес-операцій</span>.
               Тут готують <span className={styles.highlightedWords}>аналітиків</span>, здатних ефективно працювати з великими інформаційними системами, обробляти значні обсяги даних та вирішувати складні управлінські завдання.
            </>
        );

    const text125 = (
            <>
              <span>Кібербезпека</span> — це значно більше, ніж просто інформаційні технології. Це динамічна сфера, де щодня ти стоїш на варті цифрових світів, <span>захищаючи їх від різноманітних кібератак</span>.
              Наша програма готує висококваліфікованих фахівців, здатних не лише виявляти та реагувати на загрози, але й активно їх передбачати та ефективно запобігати.            
            </>
        );
    return (
        <>
            <Header />
            <h1 className={styles.mainTitle}>Про спеціальності</h1>
            
            <div ref={specialtyRefs['A4']} id="specialty-014">
                <AboutSpecialty 
                    specialtyNumber="A4"
                    specialtyName="Середня освіта (Інформатика)"
                    text={text014}
                    whatYouLearn={[
                    "Основи програмування мовами Python та Java",
                    "Вивчення структур даних та алгоритмів",
                    "Моделі статистичного навчання",
                    "Динамічна теорія інформації: аналіз інформаційних процесів",
                    "Розробка програмних продуктів",
                    "Методика викладання фахових дисциплін",
                    "Педагогічна практика"

                    ]}
                    careerOpportunities={[
                    "Вчитель інформатики в закладах середньої освіти",
                    "Методист з інформатики в освітніх установах",
                    "Розробник навчальних програм та електронних освітніх ресурсів",
                    "Консультант з впровадження інформаційних технологій в освіті",
                    "Фахівець з цифрової освіти в державних та приватних структурах"
                    ]}
                />
            </div>

            <div ref={specialtyRefs['F1']} id="specialty-113">
                <AboutSpecialty 
                    specialtyNumber="F1"
                    specialtyName="Прикладна математика"
                    text={text113}
                    whatYouLearn={[
                        "Дискретна та прикладна математика",
                        "Теорія ймовірностей і статистика",
                      " Моделювання процесів: створення математичних моделей явищ",
                        "Програмування та обробка даних",
                        "Методи оптимізації та ML, пошук найкращих рішень та навчання машин"
                    ]}
                    careerOpportunities={[
                        "Аналітик даних (Data Analyst)",
                        "Спеціаліст з математичного моделювання",
                        "Фінансовий аналітик",
                        "Розробник алгоритмів",
                        "Дослідник у наукових інститутах або R&D"
                    ]}
                />
            </div>

            <div ref={specialtyRefs['F3']} id="specialty-122">
                <AboutSpecialty 
                    specialtyNumber="F3"
                    specialtyName="Комп'ютерні науки"
                    text={text122}
                    whatYouLearn={[
                      "Алгоритми та дані, як ефективно обробляти інформацію",
                      "Програмування, основи та різні мови для розробки",
                      "Веб та бази даних, створення сайтів і керування інформацією",
                      "Розробка ПЗ, як створювати якісні програми",
                      "Штучний інтелект, навчання машин думати та діяти"
                    ]}
                    careerOpportunities={[
                      "Програміст (Software Engineer)",
                      "Розробник AI/ML",
                      "Веб-розробник (Web Developer)",
                      "Тестувальник ПЗ (QA Engineer)",
                      "Фахівець з DevOps, Data Engineer, Game Developer"
                    ]}
                />
            </div>

            <div ref={specialtyRefs['F4']} id="specialty-124">
                <AboutSpecialty 
                    specialtyNumber="F4"
                    specialtyName="Системний аналіз"
                    text={text124}
                    whatYouLearn={[
                        "Бізнес-аналіз і проектування систем, розуміння потреб бізнесу",
                        "Аналіз даних і управління проєктами",
                        "Математичне моделювання, прогнозування та оптимізації",
                        "Роботу з аналітичними платформами:",
                        "Інтелектуальний аналіз даних (Data Science)"
                    ]}
                    careerOpportunities={[
                        "Системний аналітик",
                        "Бізнес-аналітик",
                        "Data Analyst / Data Scientist",
                        "Менеджер IT-проєктів",
                        "Консультант з цифрової трансформації"
                    ]}
                />
            </div>

            <div ref={specialtyRefs['F5']} id="specialty-125">
                <AboutSpecialty 
                    specialtyNumber="F5"
                    specialtyName="Кібербезпека та захист інформації"
                    text={text125}
                    whatYouLearn={[
                        "Криптографія та захист, методи шифрування даних",
                        "Мережева безпека, захист комп'ютерних мереж",
                        "Етичний хакінг, тестування на проникнення систем",
                        "Системне адміністрування, керування безпекою інфраструктури",
                        "Стандарти безпеки й аудит, норми та перевірка систем"
                    ]}
                    careerOpportunities={[
                        "Фахівець з кібербезпеки",
                        "Information Security Analyst",
                        "Penetration Tester",
                        "DevSecOps Engineer",
                        "IT-аудитор"
                    ]}
                />
            </div>
            
            <Footer />
        </>
    );
}

export default AboutSpecialtyPage;