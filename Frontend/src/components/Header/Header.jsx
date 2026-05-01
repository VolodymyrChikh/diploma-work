import { useState } from 'react';
import styles from './Header.module.css';
import NotificationPopup from '../NotificationPopup/NotificationPopup';
import bellIcon from '../../assets/images/notification-bell.svg';

function Header(){
    const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);

    return(
        <header className={styles.header}>
            <nav className={styles.headerNav}>
                <a href="/main" className={styles.headerLogo}>
                    <h1 className={styles.headerTitle}>
                        <span className={styles.ami}>AMI</span>
                        <span className={styles.portal}>Portal</span>
                    </h1>
                </a>
                <ul className={styles.headerNavLinks}>
                    <li><a href="/main">Головна</a></li>
                    <li><a href="/about-specialties">Про спеціальності</a></li>
                    <li><a href="/media">Медіатека</a></li>
                    <li><a href="/forum">Форум</a></li>                
                    <li><a href="/profile">Профіль</a></li>
                    <li className={styles.notificationContainer}>
                        <button 
                            className={styles.notificationButton} 
                            onClick={() => setIsNotificationsOpen(!isNotificationsOpen)}
                            title="Сповіщення"
                        >
                            <img src={bellIcon} alt="Сповіщення" className={styles.notificationIcon} />
                        </button>
                        <NotificationPopup 
                            isOpen={isNotificationsOpen} 
                            onClose={() => setIsNotificationsOpen(false)} 
                        />
                    </li>
                </ul>
            </nav>
        </header>
    );
}

export default Header;