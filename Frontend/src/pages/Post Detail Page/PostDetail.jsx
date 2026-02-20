import { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styles from './PostDetail.module.css';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import anonymousAvatar from '../../assets/images/anonymous.jpg';
import { AuthContext } from '../../context/AuthContext';
import Comment from '../../components/Comment/Comment';

function PostDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { isAuthenticated, user, getAuthInfo } = useContext(AuthContext);
    
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [commentError, setCommentError] = useState(null);
    const [newComment, setNewComment] = useState('');
    const [submittingComment, setSubmittingComment] = useState(false);
    const [authDebugInfo, setAuthDebugInfo] = useState({});

    useEffect(() => {
        const authInfo = getAuthInfo();
        console.log("Auth state:", { isAuthenticated, user, authInfo });
        
        const storedUserData = localStorage.getItem('userData');
        if (storedUserData) {
            try {
                const userData = JSON.parse(storedUserData);
                console.log("Stored user data:", userData);
                if (!user && userData && isAuthenticated) {
                    console.log("Using stored userData instead of context");
                }
            } catch (err) {
                console.error("Failed to parse stored userData:", err);
            }
        }
        
        setAuthDebugInfo({
            ...authInfo,
            user: user,
            storedUserData: storedUserData ? 'exists' : 'none'
        });
    }, [isAuthenticated, user, getAuthInfo]);
    
    useEffect(() => {
        const intervalId = setInterval(() => {
            if (id) {
                console.log("Auto-refreshing comments...");
                fetchComments();
            }
        }, 30000);
        
        return () => clearInterval(intervalId);
    }, [id]);

    const fetchComments = async () => {
        try {
            const response = await fetch(`http://localhost:9000/comments/post/${id}?size=1000&sort=createdAt,desc`);
            if (!response.ok) {
                throw new Error('Failed to load comments');
            }
            const data = await response.json();
            console.log("Fetched comments data:", data);
            
            const processedComments = data.content || [];
            setComments(processedComments);
            
        } catch (err) {
            console.error("Error fetching comments:", err);
            setError(err.message);
        }
    };
    
    useEffect(() => {
        if (id) {
            async function fetchPostData() {
                try {
                    const response = await fetch(`http://localhost:9000/posts/${id}`);
                    if (!response.ok) {
                        throw new Error('Failed to load post');
                    }
                    const data = await response.json();
                    setPost(data);
                } catch (err) {
                    setError(err.message);
                } finally {
                    setLoading(false);
                }
            }
            
            fetchPostData();
            fetchComments();
        }
    }, [id]);

    function getRelativeTime(dateStr) {
        if (!dateStr) return 'Невідомий час';
        
        const postDate = new Date(dateStr);
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

    function handleCommentChange(e) {
        setNewComment(e.target.value);

        if (commentError) {
            setCommentError(null);
        }
    }

    async function handleSubmitComment(e) {
        e.preventDefault();
        
        if (!isAuthenticated) {
            navigate('/signin', { state: { from: `/post/${id}` } });
            return;
        }

        if (!newComment.trim()) return;

        setSubmittingComment(true);
        setCommentError(null);

        try {
            const token = localStorage.getItem('token') || localStorage.getItem('authToken');
            console.log("Using token:", token);
            
            if (!token) {
                throw new Error("Authentication token not found");
            }
            
            let userId = null;
            let userData = null;
            
            if (user && user.id) {
                userId = user.id;
                userData = user;
            } else {
                const storedUserData = localStorage.getItem('userData');
                if (storedUserData) {
                    try {
                        userData = JSON.parse(storedUserData);
                        if (userData && userData.id) {
                            userId = userData.id;
                            console.log("Using userId from localStorage:", userId);
                        }
                    } catch (err) {
                        console.error("Failed to parse stored userData:", err);
                    }
                }
            }
            
            if (!userId) {
                throw new Error("User ID is required but not available");
            }
            
            const commentData = {
                content: newComment,
                postId: parseInt(id),
                userId: userId,
            };
            
            console.log("Submitting comment data:", commentData);
            
            const response = await fetch('http://localhost:9000/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(commentData),
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error("Server error:", errorText);
                throw new Error(`Failed to post comment: ${response.status} ${errorText}`);
            }

            const newCommentData = await response.json();
            console.log("New comment created successfully:", newCommentData);
            
            setNewComment('');
            await fetchComments(); // Refresh comments
            
        } catch (err) {
            console.error("Error posting comment:", err);
            setCommentError(err.message);
        } finally {
            setSubmittingComment(false);
        }
    }

    const handleEditComment = async (commentId, newContent) => {
        if (!isAuthenticated) {
            navigate('/signin', { state: { from: `/post/${id}` } });
            return;
        }

        try {
            const token = localStorage.getItem('token') || localStorage.getItem('authToken');
            if (!token) {
                throw new Error("Authentication token not found");
            }

            const response = await fetch(`http://localhost:9000/comments/${commentId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ content: newContent }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to edit comment: ${response.status} ${errorText}`);
            }

            await fetchComments();
        } catch (err) {
            console.error("Error editing comment:", err);
            alert('Помилка при редагуванні коментаря');
        }
    };

    const handleDeleteComment = async (commentId) => {
        if (!isAuthenticated) {
            navigate('/signin', { state: { from: `/post/${id}` } });
            return;
        }

        try {
            const token = localStorage.getItem('token') || localStorage.getItem('authToken');
            if (!token) {
                throw new Error("Authentication token not found");
            }

            const response = await fetch(`http://localhost:9000/comments/${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to delete comment: ${response.status} ${errorText}`);
            }

            await fetchComments();
        } catch (err) {
            console.error("Error deleting comment:", err);
            alert('Помилка при видаленні коментаря');
        }
    };

    function getCommentSuffix(count) {
        if (count % 10 === 1 && count % 100 !== 11) return "ій";
        if ([2, 3, 4].includes(count % 10) && ![12, 13, 14].includes(count % 100)) return "і";
        return "ів";
    }

    if (loading) return (
        <>
            <Header />
            <div className={styles.loadingContainer}>
                <p>Завантаження...</p>
            </div>
            <Footer />
        </>
    );

    if (error || !post) return (
        <>
            <Header />
            <div className={styles.errorContainer}>
                <p>Помилка: {error || 'Пост не знайдено'}</p>
                <button className={styles.backButton} onClick={() => navigate('/forum')}>
                    Повернутися до форуму
                </button>
            </div>
            <Footer />
        </>
    );

    const userName = post.isAnonymous 
        ? 'Анонім'
        : (post.userResponse?.firstName && post.userResponse?.lastName) 
            ? `${post.userResponse.firstName} ${post.userResponse.lastName}` 
            : (post.userResponse?.firstName || post.userResponse?.lastName) 
                ? `${post.userResponse?.firstName || ''} ${post.userResponse?.lastName || ''}`.trim()
                : 'Анонім';

    const categoryName = post.categoryResponse?.name || 'Без категорії';
    const postTime = getRelativeTime(post.createdAt);

    return (
        <div className={styles.postDetailPage}>
            <Header />

            <div className={styles.buttonBackContainer}>
                <a href="/forum" className={styles.buttonBack}> 
                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 25 25" fill="none">
                        <circle cx="12.5" cy="12.5" r="11.75" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                        <path d="M13 18L7 12.5L13 7" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                        <path d="M7 12L18 12" stroke="#333333" stroke-opacity="0.65" stroke-width="1.5"/>
                    </svg>
                </a>
            </div>
            
            <div className={styles.postDetailContainer}>
                
                <article className={styles.postContent}>
                    <header className={styles.postHeader}>
                        <div className={styles.userInfo}>
                            <img 
                                className={`${styles.userAvatar} ${post.isAnonymous ? styles.anonymousAvatar : ''}`} 
                                src={post.isAnonymous ? anonymousAvatar : (post.userResponse?.avatarLink || anonymousAvatar)} 
                                alt="аватар" 
                            />
                            <div className={styles.userDetails}>
                                <span className={styles.userName}>{userName}</span>
                                <span className={styles.separator}>|</span>
                                <span className={styles.postTime}>{postTime}</span>
                            </div>
                        </div>
                        <div className={styles.categoryTag}>{categoryName}</div>
                    </header>
                    
                    <h1 className={styles.postTitle}>{post.title}</h1>
                    
                    <div className={styles.postBody}>
                        {post.content}
                    </div>
                    
                    <div className={styles.postStats}>
                        <div className={styles.likesContainer}>
                            <svg className={styles.like} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#9B4D57">
                                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="none" stroke="#6B6B6B" strokeWidth="2"/>
                            </svg>
                            <span className={styles.likesCount}>{post.likes || 0} вподобайок</span>
                        </div>
                        <div className={styles.commentsContainer}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M21 11.5C21.0034 12.8199 20.6951 14.1219 20.1 15.3C19.3944 16.7118 18.3098 17.8992 16.9674 18.7293C15.6251 19.5594 14.0782 19.9994 12.5 20C11.1801 20.0035 9.87812 19.6951 8.7 19.1L3 21L4.9 15.3C4.30493 14.1219 3.99656 12.8199 4 11.5C4.00061 9.92179 4.44061 8.37488 5.27072 7.03258C6.10083 5.69028 7.28825 4.6056 8.7 3.90003C9.87812 3.30496 11.1801 2.99659 12.5 3.00003H13C15.0843 3.11502 17.053 3.99479 18.5291 5.47089C20.0052 6.94699 20.885 8.91568 21 11V11.5Z" stroke="#6B6B6B" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                            <span className={styles.commentsCount}>{comments.length} коментар{getCommentSuffix(comments.length)}</span>
                        </div>
                    </div>
                </article>
                
                <section className={styles.commentsSection}>
                    <h2>Коментарі ({comments.length})</h2>
                    
                    <form className={styles.commentForm} onSubmit={handleSubmitComment}>
                        {commentError && (
                            <div className={styles.errorMessage}>
                                <strong>Помилка:</strong> {commentError}
                            </div>
                        )}
                        <div className={styles.commentInputWrapper}>
                            <textarea
                                className={styles.commentInput}
                                placeholder={isAuthenticated ? "Напишіть свій коментар..." : "Авторизуйтесь, щоб залишити коментар"}
                                value={newComment}
                                onChange={handleCommentChange}
                                disabled={!isAuthenticated}
                            />
                            <button 
                                type="submit" 
                                className={styles.submitCommentButton}
                                disabled={!isAuthenticated || !newComment.trim() || submittingComment}
                            >
                                {submittingComment ? (
                                    <span className={styles.loadingIndicator}>⏳</span>
                                ) : (
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                        <path d="M22 2L11 13" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                        <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    </svg>
                                )}
                            </button>
                        </div>
                    </form>
                    
                    <div className={styles.commentsList}>
                        {comments.length === 0 && (
                            <p className={styles.noComments}>Немає коментарів. Будьте першим, хто залишить коментар!</p>
                        )}
                        
                        {comments.map((comment) => {
                            if (!comment || !comment.id) {
                                console.warn("Invalid comment data:", comment);
                                return null;
                            }
                                
                            const commentUserName = 
                                (comment.userResponse?.firstName && comment.userResponse?.lastName) 
                                    ? `${comment.userResponse.firstName} ${comment.userResponse.lastName}`
                                : (comment.user?.firstName && comment.user?.lastName)
                                    ? `${comment.user.firstName} ${comment.user.lastName}`
                                : (comment.userFirstName || comment.userLastName)
                                    ? `${comment.userFirstName || ''} ${comment.userLastName || ''}`.trim()
                                : comment.userName || comment.username || 'Користувач';
                                    
                            const commentAvatar = 
                                comment.userResponse?.avatarLink || 
                                comment.user?.avatarLink || 
                                comment.userAvatarLink || 
                                comment.avatarLink || 
                                anonymousAvatar;

                            const isOwnComment = user && comment.userResponse && user.id === comment.userResponse.id;
                            
                            return (
                                <Comment 
                                    key={comment.id}
                                    commentId={comment.id}
                                    authorAvatar={commentAvatar} 
                                    author={commentUserName} 
                                    creationDate={getRelativeTime(comment.createdAt)} 
                                    commentContent={comment.content}
                                    isOwnComment={isOwnComment}
                                    onEdit={handleEditComment}
                                    onDelete={handleDeleteComment}
                                />
                            );
                        })}
                    </div>
                </section>
            </div>
            <Footer />
        </div>
    );
}

export default PostDetail;
