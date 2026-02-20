import { useEffect, useState, useRef } from 'react';
import styles from './NotificationPopup.module.css';

function NotificationPopup({ isOpen, onClose }) {
    const [notifications, setNotifications] = useState([]);
    const popupRef = useRef(null);

    useEffect(() => {
        if (isOpen) {
            fetchNotifications();
        }

        function handleClickOutside(event) {
            if (popupRef.current && !popupRef.current.contains(event.target)) {
                onClose();
            }
        }

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [isOpen, onClose]);

    const fetchNotifications = async () => {
        try {
            let allNotifications = [];
            let currentPage = 0;
            let isLastPage = false;

            while (!isLastPage) {
                const response = await fetch(`http://localhost:9000/notifications?page=${currentPage}&size=10`);
                if (response.ok) {
                    const data = await response.json();
                    allNotifications = [...allNotifications, ...data.content];
                    isLastPage = data.last;
                    currentPage++;
                } else {
                    break;
                }
            }

            const sortedNotifications = allNotifications.sort((a, b) => 
                new Date(b.createdAt) - new Date(a.createdAt)
            );
            setNotifications(sortedNotifications);
        } catch (error) {
            console.error('Error fetching notifications:', error);
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await fetch(`http://localhost:9000/notifications/${notificationId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    isRead: true
                }),
            });
            fetchNotifications();
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    if (!isOpen) return null;

    return (
        <div className={styles.container} ref={popupRef}>
            <div className={styles.header}>
                <h3>Сповіщення</h3>
                <button className={styles.closeButton} onClick={onClose}>×</button>
            </div>
            <div className={styles.notificationList}>
                {notifications.length === 0 ? (
                    <p className={styles.noNotifications}>Немає нових сповіщень</p>
                ) : (
                    notifications.map((notification) => (
                        <div 
                            key={notification.id} 
                            className={`${styles.notificationItem} ${notification.isRead ? styles.read : styles.unread}`}
                            onClick={() => markAsRead(notification.id)}
                        >
                            <div className={styles.notificationType}>{notification.type}</div>
                            <p className={styles.message}>{notification.message}</p>
                            <div className={styles.timestamp}>
                                {new Date(notification.createdAt).toLocaleString('uk-UA')}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default NotificationPopup;