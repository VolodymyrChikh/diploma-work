import { useState, useContext, useEffect } from 'react';
import styles from './Authenticate.module.css';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../../../context/AuthContext';
import SocialLogin from '../../../components/SocialLogin/SocialLogin';

const Authenticate = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });  const [showPopup, setShowPopup] = useState(false);
  const [loginError, setLoginError] = useState('');

  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useContext(AuthContext);
  
  const from = location.state?.from || '/profile';

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');

    if (token) {
      console.log('Token received from social login');
      localStorage.setItem('token', token);
      localStorage.setItem('authToken', token);
      
      // Force a reload to ensure AuthContext picks up the new token
      window.location.href = from;
    }
  }, [location, from]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setLoginError('');
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoginError('');
    
    try {
      console.log('Attempting login with AuthContext', formData);
      
      const result = await login(formData.email, formData.password);
      
      if (result.success) {
        console.log('Login successful through AuthContext');
        
        console.log('Auth token:', localStorage.getItem('token'));
        console.log('Legacy auth token:', localStorage.getItem('authToken'));
        console.log('User data exists:', !!localStorage.getItem('userData'));
        
        setShowPopup(true);
        setTimeout(() => {
          setShowPopup(false);
          
          const hasToken = localStorage.getItem('token') || localStorage.getItem('authToken');
          
          if (!hasToken) {
            throw new Error('Token not stored properly after login');
          }
          
          console.log('Redirecting to:', from);
          navigate(from, { replace: true });
        }, 1400);
      } else {
        console.error('Login failed:', result.error);
        setLoginError('Невірний email або пароль');
      }
    } catch (error) {
      console.error('Login error:', error);
      setLoginError('Помилка входу: ' + error.message);
    }
  };
  
  return (
    <div className={styles.formContainer}>
      {showPopup && (
        <div className={styles.authPopup}>Успішна автентифікація!</div>
      )}
      <div>
        <h1 className={styles.formTitle}>Увійдіть у свій акаунт</h1><form className={styles.form} onSubmit={handleSubmit}>
          {loginError && (
            <div className={styles.errorMessage}>
              {loginError}
            </div>
          )}
          <div>
            <label className={styles.label}>Електронна пошта:</label>
            <input
              type="email"
              name="email"
              placeholder="Введіть свій email"
              value={formData.email}
              onChange={handleChange}
              className={styles.input}
              required
            />
          </div>
          <div>
            <label className={styles.label}>Пароль:</label>
            <input
              type="password"
              name="password"
              placeholder="Введіть свій пароль"
              value={formData.password}
              onChange={handleChange}
              className={styles.input}
              required
            />
          </div>
          <div className={styles.links}>
            <a href="/forgot-password" className={styles.forgotPassword}>
              Забули пароль?
            </a>
            <a href="/signup" className={styles.singUp}>
              Зареєструватися
            </a>
          </div>          
          <button type="submit" className={styles.button}>
            Увійти
          </button>
          
          <SocialLogin />
        </form>
      </div>
    </div>
  );
};

export default Authenticate;