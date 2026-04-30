import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Profile.module.css";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";

function Profile() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [specialties, setSpecialties] = useState([]);
  const [isSaving, setIsSaving] = useState(false);
  const [formData, setFormData] = useState({
    specialtyId: "",
    groupName: "",
    bio: "",
    githubLink: ""
  });
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const navigate = useNavigate();

  const normalizeGithubUrl = (githubLink) => {
    if (!githubLink) {
      return null;
    }

    return githubLink.startsWith("http://") || githubLink.startsWith("https://")
      ? githubLink
      : `https://${githubLink}`;
  };

  const fetchSpecialties = async () => {
    try {
      const response = await fetch("http://localhost:9000/specialties?size=100");
      if (response.ok) {
        const data = await response.json();
        setSpecialties(data.content || data);
      }
    } catch (error) {
      console.error("Error fetching specialties:", error);
    }
  };

  const startEditing = () => {
    setIsEditing(true);
    if (specialties.length === 0) {
      fetchSpecialties();
    }
  };

  const cancelEditing = () => {
    setIsEditing(false);
    if (user) {
      setFormData({
        specialtyId: user.specialtyResponse?.id || "",
        groupName: user.groupName || "",
        bio: user.bio || "",
        githubLink: user.githubLink || ""
      });
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    setIsSaving(true);
    const token = localStorage.getItem("authToken") || localStorage.getItem("token");
    try {
      const finalGithubLink = formData.githubLink ? normalizeGithubUrl(formData.githubLink.trim()) : null;

      const requestBody = {
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        specialtyId: formData.specialtyId ? Number(formData.specialtyId) : null,
        groupName: formData.groupName?.trim() || null,
        bio: formData.bio?.trim() || null,
        githubLink: finalGithubLink,
        status: user.status || "offline",
        avatarLink: user.avatarLink
      };

      const response = await fetch(`http://localhost:9000/users/${user.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(requestBody)
      });

      if (response.ok) {
        const updatedUser = await response.json();
        setUser(updatedUser);
        localStorage.setItem("userData", JSON.stringify(updatedUser));
        setIsEditing(false);
        setShowSuccessPopup(true);
        setTimeout(() => setShowSuccessPopup(false), 2000);
      } else {
        const errorData = await response.text();
        console.error("Backend validation error response:", errorData);
        alert("Помилка (400). Відкрийте консоль бота (F12) та подивіться на 'Backend validation error response', щоб зрозуміти, яке поле не пройшло перевірку бекенду.");
      }
    } catch (error) {
      console.error("Error saving profile:", error);
      alert("Помилка з'єднання");
    } finally {
      setIsSaving(false);
    }
  };

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem("userData"));
    const authToken = localStorage.getItem("authToken") || localStorage.getItem("token");
    
    if (!authToken) {
      alert("Будь ласка, увійдіть для перегляду профілю");
      navigate('/signin');
      return;
    }
    
    if (storedUser) {
      setUser(storedUser);
      setFormData({
        specialtyId: storedUser.specialtyResponse?.id || "",
        groupName: storedUser.groupName || "",
        bio: storedUser.bio || "",
        githubLink: storedUser.githubLink || ""
      });
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
            {showSuccessPopup && (
              <div className={styles.authPopup}>Профіль успішно оновлено!</div>
            )}
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
                          {isEditing ? (
                            <select
                              name="specialtyId"
                              value={formData.specialtyId}
                              onChange={handleInputChange}
                              className={styles.formSelect}
                            >
                              <option value="">Оберіть спеціальність</option>
                              {specialties.map((spec) => (
                                <option key={spec.id} value={spec.id}>
                                  {spec.name}
                                </option>
                              ))}
                            </select>
                          ) : (
                            <span className={styles.detailValue}>{user.specialtyResponse?.name || "Не вказано"}</span>
                          )}
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

                      <div className={styles.profileDetail}>
                        <div className={styles.detailIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                          </svg>
                        </div>
                        <div className={styles.detailContent}>
                          <label className={styles.detailLabel}>Група</label>
                          {isEditing ? (
                            <input
                              type="text"
                              name="groupName"
                              value={formData.groupName}
                              onChange={handleInputChange}
                              className={styles.formInput}
                              placeholder="Наприклад: КН-11"
                            />
                          ) : (
                            <span className={styles.detailValue}>{user.groupName || "Не вказано"}</span>
                          )}
                        </div>
                      </div>

                      <div className={styles.profileDetail}>
                        <div className={styles.detailIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                          </svg>
                        </div>
                        <div className={styles.detailContent}>
                          <label className={styles.detailLabel}>Про себе</label>
                          {isEditing ? (
                            <textarea
                              name="bio"
                              value={formData.bio}
                              onChange={handleInputChange}
                              className={styles.formTextarea}
                              placeholder="Розкажіть трохи про себе..."
                            />
                          ) : (
                            <span className={`${styles.detailValue} ${styles.multilineValue}`}>{user.bio || "Не вказано"}</span>
                          )}
                        </div>
                      </div>

                      <div className={styles.profileDetail}>
                        <div className={styles.detailIcon}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path>
                            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path>
                          </svg>
                        </div>
                        <div className={styles.detailContent}>
                          <label className={styles.detailLabel}>GitHub</label>
                          {isEditing ? (
                            <input
                              type="text"
                              name="githubLink"
                              value={formData.githubLink}
                              onChange={handleInputChange}
                              className={styles.formInput}
                              placeholder="https://github.com/username"
                            />
                          ) : (
                            user.githubLink ? (
                              <a
                                href={normalizeGithubUrl(user.githubLink)}
                                target="_blank"
                                rel="noreferrer"
                                className={`${styles.detailValue} ${styles.detailLink}`}
                              >
                                {user.githubLink}
                              </a>
                            ) : (
                              <span className={styles.detailValue}>Не вказано</span>
                            )
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div className={styles.buttonsContainer}>
                    {isEditing ? (
                      <>
                        <button className={styles.exitButton} onClick={cancelEditing} disabled={isSaving}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                          </svg>
                          Скасувати
                        </button>
                        <button className={styles.editButton} onClick={handleSave} disabled={isSaving}>
                          {isSaving ? "Збереження..." : (
                            <>
                              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                                <polyline points="17 21 17 13 7 13 7 21"></polyline>
                                <polyline points="7 3 7 8 15 8"></polyline>
                              </svg>
                              Зберегти
                            </>
                          )}
                        </button>
                      </>
                    ) : (
                      <>
                        <button className={styles.exitButton} onClick={handleLogout}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                            <polyline points="16 17 21 12 16 7"></polyline>
                            <line x1="21" y1="12" x2="9" y2="12"></line>
                          </svg>
                          Вийти
                        </button>
                        <button className={styles.editButton} onClick={startEditing}>
                          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={styles.buttonIcon}>
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                          </svg>
                          Редагувати
                        </button>
                      </>
                    )}
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
