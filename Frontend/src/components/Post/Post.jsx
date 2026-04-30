import styles from "./Post.module.css";
import anonymousAvatar from "../../assets/images/anonymous.jpg";
import PropTypes from 'prop-types'; 
import { useState, useEffect, useContext, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

function Post({ id, slug, title, content, likes: initialLikes, createdAt, categoryResponse, userResponse, isAnonymous, lastComment }) {
    const navigate = useNavigate();
    const { user, isAuthenticated } = useContext(AuthContext);
    const [showDropdown, setShowDropdown] = useState(false);
    const [likes, setLikes] = useState(initialLikes || 0);
    const [isLiked, setIsLiked] = useState(false);
    const dropdownRef = useRef(null);
    const isCurrentUserPost = user && userResponse && user.id === userResponse.id;
    
    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        }

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    useEffect(() => {
        async function checkLikeStatus() {
            if (!isAuthenticated || !user) return;

            try {
                const response = await fetch(`http://localhost:9000/posts/${id}/likes/check?userId=${user.id}`);
                if (response.ok) {
                    const hasLiked = await response.json();
                    setIsLiked(hasLiked);
                }
            } catch (error) {
                console.error('Error checking like status:', error);
            }
        }

        checkLikeStatus();
    }, [id, user, isAuthenticated]);

    const handleLikeToggle = async (e) => {
        e.stopPropagation();

        if (!isAuthenticated) {
            const redirectPath = slug ? `/post/${slug}` : `/post/${id}`;
            navigate('/signin', { state: { from: redirectPath } });
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`http://localhost:9000/posts/${id}/${isLiked ? 'unlike' : 'like'}?userId=${user.id}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const updatedPost = await response.json();
                setLikes(updatedPost.likes || 0);
                setIsLiked(!isLiked);
            }
        } catch (error) {
            console.error('Error toggling like:', error);
        }
    };

    const handleDeletePost = async (e) => {
        e.stopPropagation();
        if (!window.confirm('Ви впевнені, що хочете видалити цей пост?')) {
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`http://localhost:9000/posts/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                window.location.reload();
            } else {
                console.error('Failed to delete post');
                alert('Не вдалося видалити пост');
            }
        } catch (error) {
            console.error('Error deleting post:', error);
            alert('Помилка при видаленні поста');
        }
    };

    const toggleDropdown = (e) => {
        e.stopPropagation();
        setShowDropdown(!showDropdown);
    };

    function getRelativeTime(postDateStr) {
        if (!postDateStr) return 'Невідомий час';
        
        const postDate = new Date(postDateStr);
        const now = new Date();
        const diffMs = now - postDate;
    
        const seconds = Math.floor(diffMs / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        const weeks = Math.floor(days / 7);
        const months = Math.floor(weeks / 4);
        const years = Math.floor(months / 12);
    
        if (years > 0) {
            return `${years} рік${years > 1 ? 'и' : ''} тому`;
        } else if (months > 4) {
            return `${months} місяців тому`;
        } else if (months >= 2) {
            return `${months} місяці тому`;
        } else if (months === 1) {
            return 'місяць тому';
        } else if (weeks >= 2) {
            return `${weeks} тижні тому`;
        } else if (weeks == 1 ) {
            return 'тиждень тому';
        } else if (days >= 5) {
            return `${days} днів тому`;
        } else if (days >= 2 && days < 5) {
            return `${days} дні тому`;
        } else if (days === 1) {
            return 'учора';
        } else if (hours >= 1) {
            return `${hours} год тому`;
        } else if (minutes >= 1) {
            return `${minutes} хв тому`;
        } else {
            return 'щойно';
        }
    }
    const [commentCount, setCommentCount] = useState(0);

    useEffect(() => {
        async function fetchCommentCount() {
            try {
                const response = await fetch(`http://localhost:9000/comments/post/${id}/count`);
                const count = await response.json();
                setCommentCount(count);
            } catch (error) {
                console.error("Failed to fetch comment count:", error);
            }
        }

        if (id) {
            fetchCommentCount();
        }
    }, [id]);

    function getCommentSuffix(count) {
        if (count % 10 === 1 && count % 100 !== 11) return "ій";
        if ([2, 3, 4].includes(count % 10) && ![12, 13, 14].includes(count % 100)) return "і";
        return "ів";
    }
    
    const navigateToPostDetail = () => {
        if (slug) {
            navigate(`/forum/post/${slug}`);
        } else {
            navigate(`/forum/post/${id}`);
        }
    };

    const postTime = createdAt ? getRelativeTime(createdAt) : 'Невідомий час';
    const userFirstName = userResponse?.firstName || 'Анонім';
    const userLastName = userResponse?.lastName || 'Анонім';
    const userName = isAnonymous 
                       ? 'Анонім'
                       : (userResponse?.firstName && userResponse?.lastName) 
                           ? `${userResponse.firstName} ${userResponse.lastName}` 
                           : (userFirstName !== 'Анонім' || userLastName !== 'Анонім') 
                               ? `${userFirstName} ${userLastName}`.trim()
                               : 'Анонім'; 
    const categoryName = categoryResponse?.name ? categoryResponse.name : 'Без категорії';
    const postContent = content || "Вміст відсутній";

    const getCommenterName = () => {
        if (lastComment.userResponse) {
            const firstName = lastComment.userResponse.firstName || '';
            const lastName = lastComment.userResponse.lastName || '';
            
            if (firstName || lastName) {
                return `${firstName} ${lastName}`.trim();
            }
        } else if (lastComment.userFirstName || lastComment.userLastName) {
            return `${lastComment.userFirstName || ''} ${lastComment.userLastName || ''}`.trim();
        } else if (lastComment.userName) {
            return lastComment.userName;
        }
        
        return "Користувач";
    };

    const getCommenterAvatar = () => {
        if (lastComment.userResponse && lastComment.userResponse.avatarLink) {
            return lastComment.userResponse.avatarLink;
        } else if (lastComment.userAvatarLink) {
            return lastComment.userAvatarLink;
        } else if (lastComment.avatarLink) {
            return lastComment.avatarLink;
        }
        
        return anonymousAvatar;
    };

    return (
        <div className={styles.forumPostsContainer}>
            <div className={`${styles.forumPost} ${styles.clickable}`} key={id} onClick={navigateToPostDetail}>
                <div className={styles.forumPostHeader}>                    
                    <div className={styles.userInfo}>
                        <div>
                            <img 
                                className={`${styles.userAvatar} ${isAnonymous ? styles.anonymousAvatar : ''}`} 
                                src={isAnonymous ? anonymousAvatar : (userResponse?.avatarLink || anonymousAvatar)} 
                                alt="аватар" 
                                height="42px" 
                                width="42px"
                            />
                        </div>
                        <div className={styles.userDetails}>
                            <span className={`${styles.userName}`}>{userName}</span>
                            <span className={styles.userGroup}>|</span>
                            <span className={styles.postTime}>{postTime}</span>
                        </div>
                    </div>
                    <div className={styles.postOptions} ref={dropdownRef}>
                        <div className={styles.dotsMenu} onClick={toggleDropdown}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M12 13C12.5523 13 13 12.5523 13 12C13 11.4477 12.5523 11 12 11C11.4477 11 11 11.4477 11 12C11 12.5523 11.4477 13 12 13Z" fill="#6B6B6B"/>
                                <path d="M12 6C12.5523 6 13 5.55228 13 5C13 4.44772 12.5523 4 12 4C11.4477 4 11 4.44772 11 5C11 5.55228 11.4477 6 12 6Z" fill="#6B6B6B"/>
                                <path d="M12 20C12.5523 20 13 19.5523 13 19C13 18.4477 12.5523 18 12 18C11.4477 18 11 18.4477 11 19C11 19.5523 11.4477 20 12 20Z" fill="#6B6B6B"/>
                            </svg>
                        </div>
                        {showDropdown && isCurrentUserPost && (
                            <div className={styles.dropdownMenu}>
                                <button onClick={handleDeletePost} className={styles.deleteButton}>
                                    Видалити пост
                                </button>
                            </div>
                        )}
                    </div>
                </div>
                <div className={styles.forumPostBody}>
                    <div className={styles.categoryTag}>{categoryName}</div>
                    <h2 className={styles.forumPostTitle}>{title}</h2>                    
                    {lastComment && (
                        <div className={styles.lastCommentContainer}>
                            <div className={styles.lastCommentHeader}>
                                <span className={styles.lastCommentTime}>
                                    {lastComment.createdAt ? getRelativeTime(lastComment.createdAt) : ''}
                                </span>
                            </div>
                            <div className={styles.lastCommentContent}>
                                <div className={styles.commentAvatarContainer}>
                                    <img 
                                        className={styles.commentAvatar} 
                                        src={getCommenterAvatar()} 
                                        alt="аватар коментатора" 
                                    />
                                </div>
                                <div className={styles.commentTextContainer}>
                                    <span className={styles.commentAuthor}>
                                        {getCommenterName()}
                                    </span>
                                    <p className={styles.commentText}>{lastComment.content}</p>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
                <div className={styles.forumPostFooter}>
                    <div className={styles.postStats}>
                        <div className={styles.likesContainer}>
                            <button
                                className={`${styles.likeButton} ${isLiked ? styles.liked : ''}`}
                                onClick={handleLikeToggle}
                                title={isAuthenticated ? (isLiked ? 'Забрати вподобайку' : 'Вподобати') : 'Увійдіть, щоб вподобати'}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                                    <path 
                                        d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" 
                                        fill={isLiked ? "#9B4D57" : "none"} 
                                        stroke={isLiked ? "#9B4D57" : "#6B6B6B"} 
                                        strokeWidth="2"
                                    />
                                </svg>
                            </button>
                            <span className={`${styles.likesCount} ${isLiked ? styles.liked : ''}`}>
                                {likes} вподобайок
                            </span>
                        </div>
                        <div className={styles.commentsContainer}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M21 11.5C21.0034 12.8199 20.6951 14.1219 20.1 15.3C19.3944 16.7118 18.3098 17.8992 16.9674 18.7293C15.6251 19.5594 14.0782 19.9994 12.5 20C11.1801 20.0035 9.87812 19.6951 8.7 19.1L3 21L4.9 15.3C4.30493 14.1219 3.99656 12.8199 4 11.5C4.00061 9.92179 4.44061 8.37488 5.27072 7.03258C6.10083 5.69028 7.28825 4.6056 8.7 3.90003C9.87812 3.30496 11.1801 2.99659 12.5 3.00003H13C15.0843 3.11502 17.053 3.99479 18.5291 5.47089C20.0052 6.94699 20.885 8.91568 21 11V11.5Z" stroke="#6B6B6B" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                            <span className={styles.commentsCount}>{commentCount} коментар{getCommentSuffix(commentCount)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

Post.propTypes = {
    id: PropTypes.number.isRequired,
    slug: PropTypes.string,
    title: PropTypes.string.isRequired,
    content: PropTypes.string,
    likes: PropTypes.number,
    createdAt: PropTypes.string,
    isAnonymous: PropTypes.bool,
    categoryResponse: PropTypes.shape({
        name: PropTypes.string
    }),
    comments: PropTypes.array,
    userResponse: PropTypes.shape({
        lastName: PropTypes.string,
        firstName: PropTypes.string,
        avatarLink: PropTypes.string
    }),
    lastComment: PropTypes.shape({
        id: PropTypes.number,
        content: PropTypes.string,
        createdAt: PropTypes.string,
        userId: PropTypes.number,
        postId: PropTypes.number,
        userResponse: PropTypes.shape({
            firstName: PropTypes.string,
            lastName: PropTypes.string,
            avatarLink: PropTypes.string
        }),
        userFirstName: PropTypes.string,
        userLastName: PropTypes.string,
        userAvatarLink: PropTypes.string,
        avatarLink: PropTypes.string,
        userName: PropTypes.string
    }),
};

export default Post;