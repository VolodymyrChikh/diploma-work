import styles from './CreatePost.module.css';
import Header from '../../components/Header/Header';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function CreatePost() {
    const [title, setTitle] = useState('');
    const [selectedCategoryId, setSelectedCategoryId] = useState(null);
    const [isAnonymous, setIsAnonymous] = useState(false);
    const [userId, setUserId] = useState(null);
    const [token, setToken] = useState(null);
    const maxChars = 255;
    const navigate = useNavigate();    useEffect(() => {
        const storedUserData = localStorage.getItem("userData");
        const storedToken = localStorage.getItem("authToken");
        
        if (!storedToken) {
            alert("Будь ласка, увійдіть для створення постів");
            navigate('/signin');
            return;
        }
        
        if (storedUserData) {
            setUserId(JSON.parse(storedUserData).id);
        }
        if (storedToken) {
            setToken(storedToken);
        }
    }, [navigate]);

    const handleTitleChange = (event) => {
        const inputText = event.target.value;
        if (inputText.length <= maxChars) {
            setTitle(inputText);
        }
    };

    const charsLeft = maxChars - title.length;

    const categories = [
        { id: 1, name: 'Навчання' },
        { id: 2, name: 'Події' },
        { id: 3, name: 'Поради' },
        { id: 4, name: 'FAQ' },
        { id: 5, name: 'Гумор' }
    ];

    const handleCategoryClick = (categoryId) => {
        setSelectedCategoryId(categoryId);
    };

    const handleAnonymousToggle = () => {
        setIsAnonymous(!isAnonymous);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!selectedCategoryId) {
            alert("Будь ласка, оберіть категорію.");
            return;
        }

        if (!userId || !token) {
            alert("Помилка автентифікації. Будь ласка, увійдіть знову.");
            navigate('/signin');
            return;
        }

        const postData = {
            title,
            categoryId: selectedCategoryId,
            isAnonymous,
            userId,
        };

        try {
            await axios.post('http://localhost:9000/posts', postData, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            alert("Пост успішно створено!");
            navigate('/forum');
        } catch (error) {
            console.error('Error creating post:', error);
            alert('Помилка створення поста: ' + (error.response?.data?.message || error.message));
        }
    };

    return (
        <>
            <div className={styles.headerWrapper}>
                <Header />
            </div>
            <div className={styles.buttonBackContainer}>
                <a href="/forum" className={styles.buttonBack}> 
                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 25 25" fill="none">
                        <circle cx="12.5" cy="12.5" r="11.75" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                        <path d="M13 18L7 12.5L13 7" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                        <path d="M7 12L18 12" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                    </svg>
                </a>
            </div>
            <div className={styles.createPostContainer}>
                <div className={styles.createPostInnerContainer}>
                    <h1 className={styles.createPostTitle}>Створіть власне обговорення</h1>
                    <form className={styles.createPostForm} onSubmit={handleSubmit}>
                        <div className={styles.createPostInnerContainers}>
                            <div className={styles.topicTitleContainer}>
                                <label className={styles.label}>Тема обговорення*</label>
                                <label className={styles.labelTextCount}>{charsLeft}</label>
                            </div>
                            <input type="text" placeholder="Заголовок" className={styles.postTitleInput} required value={title} onChange={handleTitleChange} maxLength={maxChars}/>
                        </div>
                        <div className={styles.createPostInnerContainers}>
                            <label className={styles.label}>Оберіть категорію обговорення*</label>
                            <div className={styles.categorySelect}>
                                {categories.map((category) => (
                                    <span 
                                        key={category.id}
                                        className={selectedCategoryId === category.id ? styles.selected : ''}
                                        onClick={() => handleCategoryClick(category.id)}
                                    >
                                        {category.name}
                                    </span>
                                ))}
                            </div>
                        </div>
                        <div className={styles.createPostAnonynmousContainer}>
                            <label className={styles.label}>Опублікувати анонімно</label>
                            <div className={styles.toggleSwitchContainer}>
                                <label className={styles.toggleSwitch}>
                                    <input type="checkbox" checked={isAnonymous} onChange={handleAnonymousToggle} />
                                    <span className={styles.slider}></span>
                                </label>
                            </div>
                        </div>
                        <button type="submit" className={styles.publishButton}>Опублікувати</button>
                    </form>
                </div>
            </div>
        </>
    );
}

export default CreatePost;