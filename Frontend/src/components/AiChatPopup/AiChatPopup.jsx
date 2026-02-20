import React, { useState, useRef, useEffect } from 'react';
import styles from './AiChatPopup.module.css';

const AiChatPopup = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [message, setMessage] = useState('');
    const [chatHistory, setChatHistory] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const messagesEndRef = useRef(null);
    const textareaRef = useRef(null);

    const toggleChat = () => {
        setIsOpen(!isOpen);
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [chatHistory, isOpen]);

    useEffect(() => {
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto';
            textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 120)}px`; // Limit max height
        }
    }, [message]);

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (!message.trim()) return;

        const userMessage = message;
        setChatHistory(prev => [...prev, { type: 'user', text: userMessage }]);
        setMessage('');
        setIsLoading(true);

        try {
            const response = await fetch(`http://localhost:9000/ai/ask?message=${encodeURIComponent(userMessage)}`);
            if (!response.ok) {
                const errorText = await response.text().catch(() => 'Unknown error');
                throw new Error(`Server returned ${response.status}: ${errorText}`);
            }
            const data = await response.text();
            setChatHistory(prev => [...prev, { type: 'bot', text: data }]);
        } catch (error) {
            console.error('Error fetching AI response:', error);
            setChatHistory(prev => [...prev, { type: 'bot', text: `Sorry, something went wrong. (${error.message}). Please ensure your backend is running on port 8080.` }]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage(e);
        }
    };

    return (
        <div className={styles.container}>
            {isOpen && (
                <div className={styles.chatWindow}>
                    <div className={styles.header}>
                        <h3>ШІ-помічник</h3>
                        <button className={styles.closeButton} onClick={toggleChat}>×</button>
                    </div>
                    <div className={styles.messagesContainer}>
                        {chatHistory.length === 0 && (
                            <div className={styles.welcomeMessage}>
                                Вітання! Як я можу вам сьогодні допомогти?
                            </div>
                        )}
                        {chatHistory.map((msg, index) => (
                            <div key={index} className={`${styles.message} ${msg.type === 'user' ? styles.userMessage : styles.botMessage}`}>
                                {msg.text}
                            </div>
                        ))}
                        {isLoading && <div className={`${styles.message} ${styles.botMessage}`}>Набираю...</div>}
                        <div ref={messagesEndRef} />
                    </div>
                    <form className={styles.inputArea} onSubmit={handleSendMessage}>
                        <textarea
                            ref={textareaRef}
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            onKeyDown={handleKeyDown}
                            placeholder="Поставте запитання..."
                            disabled={isLoading}
                            rows={1}
                        />
                        <button type="submit" disabled={isLoading || !message.trim()}>
                            ➤
                        </button>
                    </form>
                </div>
            )}
            <button className={styles.fab} onClick={toggleChat}>
                {isOpen ? '✕' : 'i'}
            </button>
        </div>
    );
};

export default AiChatPopup;
