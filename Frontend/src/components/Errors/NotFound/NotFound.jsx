import { useNavigate } from 'react-router-dom';
import styles from './NotFound.module.css';
import Header from '../../Header/Header';
import Footer from '../../Footer/Footer';

function NotFound() {
    const navigate = useNavigate();

    return (
        <div className={styles.notFoundPage}>
            <Header />
            <div className={styles.content}>
                <h1>404</h1>
                <h2>Упс! Сторінку не знайдено</h2>
                <p>Здається, ви заблукали в коридорах факультету прикладної математики...</p>
                <button onClick={() => navigate('/main')} className={styles.backButton}>
                    Повернутися на головну
                </button>
            </div>
            <Footer />
        </div>
    );
}

export default NotFound;