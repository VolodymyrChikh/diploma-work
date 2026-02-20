import styles from './Comment.module.css';
import PropsTypes from 'prop-types';
import { useState, useRef, useEffect } from 'react';

function Comment({ commentId, authorAvatar, author, creationDate, commentContent, isOwnComment, onEdit, onDelete }) {
    const [isEditing, setIsEditing] = useState(false);
    const [editedContent, setEditedContent] = useState(commentContent);
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);

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

    const handleEditSubmit = () => {
        if (editedContent.trim() !== commentContent) {
            onEdit(commentId, editedContent.trim());
        }
        setIsEditing(false);
    };

    const handleDelete = () => {
        if (window.confirm('Ви впевнені, що хочете видалити цей коментар?')) {
            onDelete(commentId);
        }
    };

    const toggleDropdown = (e) => {
        e.stopPropagation();
        setShowDropdown(!showDropdown);
    };

    return (
        <div className={styles.comment}>
            <div className={styles.commentHeader}>
                <img 
                    className={styles.commentAvatar} 
                    src={authorAvatar} 
                    alt="аватар коментатора" 
                />
                <div className={styles.commentMeta}>
                    <span className={styles.commentAuthor}>{author}</span>
                    <span className={styles.commentTime}>{creationDate}</span>
                </div>
                {isOwnComment && (
                    <div className={styles.commentOptions} ref={dropdownRef}>
                        <button className={styles.optionsButton} onClick={toggleDropdown}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M12 13C12.5523 13 13 12.5523 13 12C13 11.4477 12.5523 11 12 11C11.4477 11 11 11.4477 11 12C11 12.5523 11.4477 13 12 13Z" fill="#6B6B6B"/>
                                <path d="M12 6C12.5523 6 13 5.55228 13 5C13 4.44772 12.5523 4 12 4C11.4477 4 11 4.44772 11 5C11 5.55228 11.4477 6 12 6Z" fill="#6B6B6B"/>
                                <path d="M12 20C12.5523 20 13 19.5523 13 19C13 18.4477 12.5523 18 12 18C11.4477 18 11 18.4477 11 19C11 19.5523 11.4477 20 12 20Z" fill="#6B6B6B"/>
                            </svg>
                        </button>
                        {showDropdown && (
                            <div className={styles.dropdownMenu}>
                                <button onClick={() => setIsEditing(true)} className={styles.editButton}>
                                    Редагувати
                                </button>
                                <button onClick={handleDelete} className={styles.deleteButton}>
                                    Видалити
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </div>
            <div className={styles.commentBody}>
                {isEditing ? (
                    <div className={styles.editContainer}>
                        <textarea
                            className={styles.editInput}
                            value={editedContent}
                            onChange={(e) => setEditedContent(e.target.value)}
                        />
                        <div className={styles.editButtons}>
                            <button 
                                className={styles.saveButton} 
                                onClick={handleEditSubmit}
                                disabled={!editedContent.trim() || editedContent.trim() === commentContent}
                            >
                                Зберегти
                            </button>
                            <button 
                                className={styles.cancelButton} 
                                onClick={() => {
                                    setIsEditing(false);
                                    setEditedContent(commentContent);
                                }}
                            >
                                Скасувати
                            </button>
                        </div>
                    </div>
                ) : (
                    <p>{commentContent}</p>
                )}
            </div>
        </div>  
    );
}

Comment.propTypes = {
    commentId: PropsTypes.number.isRequired,
    authorAvatar: PropsTypes.string.isRequired,
    author: PropsTypes.string.isRequired,
    creationDate: PropsTypes.string.isRequired,
    commentContent: PropsTypes.string.isRequired,
    isOwnComment: PropsTypes.bool,
    onEdit: PropsTypes.func,
    onDelete: PropsTypes.func
};

export default Comment;