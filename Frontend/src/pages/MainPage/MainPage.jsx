import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import AboutSpecialtyCard from "../../components/AboutSpecialtiesCard/AboutSpecialtyCard";
import specialty113 from '../../assets/images/113.png';
import specialty122 from '../../assets/images/122.png';
import specialty124 from '../../assets/images/124.png';
import specialty014 from '../../assets/images/014.png';
import specialty125 from '../../assets/images/125.png';
import TransparentButton from "../../components/Button/TransparentButton";
import mainPageImage from '../../assets/images/mainPageImage.png';
import RedButton from "../../components/Button/RedButton";
import styles from './MainPage.module.css';
import { useState, useEffect } from 'react';
import axios from 'axios';

function MainPage() {

  const mainImageAlt = "Студенти факультету прикладної математики та інформатики";

  const [specialties, setSpecialties] = useState([]);
  const [faqs, setFaqs] = useState([]);
  const [openFaqId, setOpenFaqId] = useState(null);

  useEffect(() => {
    const fetchSpecialties = async () => {
      try {
        const response = await axios.get('http://localhost:9000/specialties');
        const filteredSpecialties = response.data.content.filter(
          specialty => specialty.number !== null && specialty.about !== null
        );
        const sortedSpecialties = filteredSpecialties.sort((a, b) => a.number.localeCompare(b.number));
        setSpecialties(sortedSpecialties);
      } catch (error) {
        console.error('Error fetching specialties:', error);
        alert('Не вдалося завантажити спеціальності');
      }
    };

    const fetchFaqs = async () => {
      try {
        const response = await axios.get('http://localhost:9000/faqs');
        setFaqs(response.data);
      } catch (error) {
        console.error('Error fetching FAQs:', error);
      }
    };

    fetchSpecialties();
    fetchFaqs();
  }, []);

  const toggleFaq = (id) => {
    setOpenFaqId(openFaqId === id ? null : id);
  };


  const getSpecialtyImage = (number) => {
    switch (number) {
      case "F1": return specialty113;
      case "F3": return specialty122;
      case "F4": return specialty124;
      case "F5": return specialty125;
      case "A4":  return specialty014;
      default: return null;
    }
  };

  
  return (
    <>
      <Header />
      <div className={styles.imageContainer}>
        <img className={styles.mainPageImage} src={mainPageImage} alt={mainImageAlt}></img>
        <span className={styles.centeredTextFirst}>Твій шлях у світ прикладної математики та інформатики починається тут!</span>
        <span className={styles.centeredTextSecond}>Дізнайся більше про спеціальності, які відкриває наш факультет</span>
        <div className={styles.buttonContainer}>
          <a href="/about-specialties"><RedButton text="Більше" /></a>
        </div>
      </div>

      <div className="aboutSpecialtyContainer">
        <h1 className="aboutSpecialtyTitle">Про спеціальності</h1>        
        <div className={styles.specialtyCardsContainer}>
        {specialties.map(specialty => {
            const formattedNumber = specialty.number;
                return (
              <a 
                key={specialty.number}
                href={`/about-specialties#specialty-${specialty.number}`}
                style={{ textDecoration: 'none', color: 'inherit' }}
              >
                <AboutSpecialtyCard
                  specialtyImage={getSpecialtyImage(specialty.number)}
                  imageTitle={`${formattedNumber} – ${specialty.name}`}
                  imageText={specialty.about}
                />
              </a>
            );
          })}
        </div>
        <div className="transparentButtonContainer">
          <a href="/about-specialties"><TransparentButton className="transparentButton"></TransparentButton></a>
        </div>
      </div>
      <div className={styles.fpmiContainer}>
        <div className={styles.fpmiInnerContainer}>
          <div className={styles.fpmiText}>
            <h1 className={styles.fpmiTitle}>Хочеш бути частиною ФПМІ?</h1>
            <p className={styles.fpmiDescription}>
              Вже студент чи тільки мрієш про це — AMI Portal створено для тебе. Знаходь потрібне, долучайся до спільноти, твори своє майбутнє з нами!            
            </p>
          </div>
          <div className={styles.fpmiButtonContainer}>
            <a href="https://ami.lnu.edu.ua" target="_blank" rel="noopener noreferrer">
              <RedButton text="Приєднатися"/>
            </a>
          </div>
        </div>
      </div>

      <div className={styles.faqContainer}>
        <h1 className={styles.faqTitle}>Часті запитання</h1>
        <div className={styles.faqList}>
          {faqs.map((faq) => (
            <div key={faq.id} className={styles.faqItem}>
              <div 
                className={`${styles.faqQuestion} ${openFaqId === faq.id ? styles.active : ''}`}
                onClick={() => toggleFaq(faq.id)}
              >
                <span>{faq.question}</span>
                <span className={styles.faqIcon}>{openFaqId === faq.id ? '−' : '+'}</span>
              </div>
              <div className={`${styles.faqAnswer} ${openFaqId === faq.id ? styles.show : ''}`}>
                <p>{faq.answer}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      <Footer />
    </>
  );
}

export default MainPage;
