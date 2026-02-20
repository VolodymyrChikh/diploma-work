import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import styles from './Register.module.css';
import SocialLogin from '../../../components/SocialLogin/SocialLogin';

const Register = () => {
  const [formData, setFormData] = useState({
    lastName: '',
    firstName: '',
    email: '',
    password: '',
    repeatedPassword: '',
    specialty: '',
  });

  const [specialties, setSpecialties] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const [showRepeatPassword, setShowRepeatPassword] = useState(false);
  const navigate = useNavigate();
  const location = useLocation(); // Added hook here

  // Identify source page or default to profile
  const from = location.state?.from || '/profile';

  useEffect(() => {
    // Check for token in URL query params (from social login redirect)
    const params = new URLSearchParams(location.search);
    const token = params.get('token');

    if (token) {
      console.log('Token received from social login on Register page');
      localStorage.setItem('token', token);
      localStorage.setItem('authToken', token);
      
      // Redirect to the intended destination
      window.location.href = from;
    }
  }, [location, from]);

  useEffect(() => {
    const fetchSpecialties = async () => {
      try {
        const response = await axios.get('http://localhost:9000/specialties');
        const sortedSpecialties = response.data.content
          .sort((a, b) => a.number - b.number)
          .map(specialty => specialty.name);
        setSpecialties(sortedSpecialties);
      } catch (error) {
        console.error('Error fetching specialties:', error);
        alert('Не вдалося завантажити спеціальності');
      }
    };

    fetchSpecialties();
  }, []);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (formData.password !== formData.repeatedPassword) {
        alert('Паролі не співпадають!');
        return;
      }

      const { lastName, firstName, email, password, repeatedPassword, specialty } = formData;
      const payload = { lastName, firstName, email, password, repeatedPassword, specialty };
      const response = await axios.post('http://localhost:9000/auth/register', payload);
      console.log('Registration successful:', response.data);

      navigate('/main');
    } catch (error) {
      console.error('Registration error:', error);
      alert('Помилка реєстрації: ' + (error.response?.data?.message || error.message));
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const toggleRepeatPasswordVisibility = () => {
    setShowRepeatPassword(!showRepeatPassword);
  };


  return (
    <div className={styles.formContainer}>
      <h1 className={styles.formTitle}>Створіть особистий акаунт</h1>
      <form className={styles.form} onSubmit={handleSubmit}>
        <div>
          <label className={styles.label}>Прізвище:</label>
          <input
            type="text"
            name="lastName"
            placeholder="Введіть своє прізвище"
            value={formData.lastName}
            onChange={handleChange}
            className={styles.input}
            required
          />
        </div>
        <div>
          <label className={styles.label}>Ім'я:</label>
          <input
            type="text"
            name="firstName"
            placeholder="Введіть своє ім'я"
            value={formData.firstName}
            onChange={handleChange}
            className={styles.input}
            required
          />
        </div>
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
          <div style={{ position: 'relative' }}>
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              placeholder="Введіть свій пароль"
              value={formData.password}
              onChange={handleChange}
              className={styles.input}
              required
            />
            <span
              onClick={togglePasswordVisibility}
              style={{
                position: 'absolute',
                right: '10px',
                top: '50%',
                transform: 'translateY(-50%)',
                cursor: 'pointer',
              }}
            >
              {showPassword ? '👁️' : '🙈'}
            </span>
          </div>
        </div>
        <div>
          <label className={styles.label}>Повторіть пароль:</label>
          <div style={{ position: 'relative' }}>
            <input
              type={showRepeatPassword ? 'text' : 'password'}
              name="repeatedPassword"
              placeholder="Повторіть свій пароль"
              value={formData.repeatedPassword}
              onChange={handleChange}
              className={styles.input}
              required
            />
            <span
              onClick={toggleRepeatPasswordVisibility}
              style={{
                position: 'absolute',
                right: '10px',
                top: '50%',
                transform: 'translateY(-50%)',
                cursor: 'pointer',
              }}
            >
              {showRepeatPassword ? '👁️' : '🙈'}
            </span>
          </div>
        </div>
        <div>
          <label className={styles.label}>Спеціальність:</label>
          <select
            name="specialty"
            value={formData.specialty}
            onChange={handleChange}
            className={styles.select}
            required
          >
            <option value="" disabled>
              Виберіть спеціальність
            </option>
            {specialties.map((specialty) => (
              <option key={specialty} value={specialty}>
                {specialty}
              </option>
            ))}
          </select>
        </div>
        <div className={styles.links}>
          <a href="/signin" className={styles.singIn}>
            Увійти
          </a>
        </div>
        <div className={styles.buttonContainer}>
          <button type="submit" className={styles.button}>
            Зареєструватися
          </button>
        </div>

        <SocialLogin />
      </form>
    </div>
  );
};

export default Register;