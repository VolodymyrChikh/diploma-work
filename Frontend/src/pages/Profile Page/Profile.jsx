import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Profile.module.css";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";

function Profile() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem("userData"));
    const authToken = localStorage.getItem("authToken");
    
    if (!authToken) {
      alert("Будь ласка, увійдіть для перегляду профілю");
      navigate('/signin');
      return;
    }
    
    if (storedUser) {
      setUser(storedUser);
    }
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 300);
    return () => clearTimeout(timer);
  }, [navigate]);

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/main"; 
  }

  return (
    <>
      <Header />
      <div className={styles.container}>
        <div className={styles.innerContainer}>
          <div className={styles.profileContainer}>
            <h1 className={styles.profileTitle}>Профіль користувача</h1>
            
            {isLoading ? (
              <div className={styles.loaderContainer}>
                <div className={styles.loader}></div>
                <p>Завантаження профілю...</p>
              </div>
            ) : (
              user && (
                <div className={styles.profileContent}>
                  <div className={styles.profileHeader}>
                    <div className={styles.avatarContainer}>
                      <img
                        src={user.avatarLink || "https://t4.ftcdn.net/jpg/05/89/93/27/360_F_589932782_vQAEAZhHnq1QCGu5ikwrYaQD0Mmurm0N.jpg"}
                        alt="Profile"
                        className={styles.profileImage}
                      />
                      <div className={styles.avatarOverlay}>
                        <span className={styles.avatarEditIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M12 20h9"></path>
                            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                          </svg>
                        </span>
                      </div>
                    </div>
                    <div className={styles.userNameContainer}>
                      <h2 className={styles.profileName}>
                        {user.firstName} {user.lastName}
                      </h2>
                      <span className={styles.userRole}>{user.specialtyResponse?.name === "Абітурієнт" ? "Абітурієнт" : "Студент"}</span>
                    </div>
                  </div>
                  
                  <div className={styles.profileInfoCard}>
                    <div className={styles.cardHeader}>
                      <h3>Інформація профілю</h3>
                    </div>
                    
                    <div className={styles.profileDetailsForm}>
                      <div className={styles.profileDetail}>
                        <div className={styles.detailIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path>
                          </svg>
                        </div>
                        <div className={styles.detailContent}>
                          <label className={styles.detailLabel}>Моя спеціальність</label>
                          <span className={styles.detailValue}>{user.specialtyResponse?.name || "Не вказано"}</span>
                        </div>
                      </div>
                      
                      <div className={styles.profileDetail}>
                        <div className={styles.detailIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                            <polyline points="22,6 12,13 2,6"></polyline>
                          </svg>
                        </div>
                        <div className={styles.detailContent}>
                          <label className={styles.detailLabel}>Email</label>
                          <span className={styles.detailValue}>{user.email}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div className={styles.buttonsContainer}>
                    <button className={styles.exitButton} onClick={handleLogout}>
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                        <polyline points="16 17 21 12 16 7"></polyline>
                        <line x1="21" y1="12" x2="9" y2="12"></line>
                      </svg>
                      Вийти
                    </button>
                    <button className={styles.editButton}>
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                      </svg>
                      Редагувати
                    </button>
                  </div>
                </div>
              )
            )}
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
}

export default Profile;
