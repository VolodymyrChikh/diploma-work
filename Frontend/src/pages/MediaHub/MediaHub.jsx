import { useEffect, useState, useRef } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import MediaCard from "../../components/MediaCard/MediaCard";
import styles from "./MediaHub.module.css";

const PAGE_SIZE = 12;

function MediaHub() {
  const [resources, setResources] = useState([]);
  const [categories, setCategories] = useState([]);
  const [activeCategory, setActiveCategory] = useState("all");
  const [activeType, setActiveType] = useState("all");
  const [activeOrder, setActiveOrder] = useState("newest");
  const [searchQuery, setSearchQuery] = useState("");
  const [searchActive, setSearchActive] = useState(false);
  const [pageInfo, setPageInfo] = useState({
    page: 0,
    totalPages: 0,
    last: true,
    loading: false,
  });
  const searchInputRef = useRef(null);
  const fileInputRef = useRef(null);

  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);
  const [uploadData, setUploadData] = useState({
    title: "",
    description: "",
    type: "DOCUMENT",
    categoryId: ""
  });
  const INITIAL_UPLOAD_STATE = {
    title: "",
    description: "",
    type: "DOCUMENT",
    categoryId: ""
  };
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [fileName, setFileName] = useState("Жоден файл не вибраний");
  const [isUploading, setIsUploading] = useState(false);
  const [fileError, setFileError] = useState("");

  const MAX_FILE_SIZE = {
    DOCUMENT: 50 * 1024 * 1024, // 50MB
    VIDEO_LINK: 50 * 1024 * 1024,   // 50MB
    IMAGE: 50 * 1024 * 1024,    // 50MB
    EXTERNAL_LINK: 0
  };

  const getAcceptAttribute = () => {
    switch (uploadData.type) {
      case "VIDEO_LINK":
        return "video/*";
      case "IMAGE":
        return "image/*";
      case "DOCUMENT":
      default:
        return "";
    }
  };

  const validateFileType = (files) => {
    if (uploadData.type === "DOCUMENT") {
      return validateFileSize(files);
    }

    const allowedTypes = {
      VIDEO_LINK: ["video/mp4", "video/webm", "video/ogg", "video/quicktime", "video/mpeg",
         "video/x-msvideo", "video/mov"],
      IMAGE: ["image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
         "image/avif", "image/heic", "image/heif", "image/tiff", "image/bmp",
          "image/x-icon", "image/vnd.microsoft.icon"],
    };

    const validTypes = allowedTypes[uploadData.type] || [];
    const invalidFiles = [];

    files.forEach((file) => {
      const isValid = validTypes.some((type) => file.type === type || file.type.startsWith(type.split("/")[0] + "/"));
      if (!isValid) {
        invalidFiles.push(file.name);
      }
    });

    if (invalidFiles.length > 0) {
      const typeLabel = uploadData.type === "VIDEO_LINK" ? "відео" : "світлини";
      setFileError(`Невідповідні файли (${typeLabel}): ${invalidFiles.join(", ")}`);
      return false;
    }

    setFileError("");
    return validateFileSize(files);
  };

  const validateFileSize = (files) => {
    const maxSize = MAX_FILE_SIZE[uploadData.type] || MAX_FILE_SIZE.DOCUMENT;
    const oversizedFiles = [];

    files.forEach((file) => {
      if (file.size > maxSize) {
        oversizedFiles.push({
          name: file.name,
          size: (file.size / (1024 * 1024)).toFixed(2),
          max: (maxSize / (1024 * 1024)).toFixed(0)
        });
      }
    });

    if (oversizedFiles.length > 0) {
      const errorMsg = oversizedFiles
        .map(f => `${f.name} (${f.size}MB, макс: ${f.max}MB)`)
        .join(", ");
      setFileError(`Файли занадто великі: ${errorMsg}`);
      return false;
    }

    setFileError("");
    return true;
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files || []);
    
    if (files.length > 0 && !validateFileType(files)) {
      setSelectedFiles([]);
      setFileName("Жоден файл не вибраний");
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
      return;
    }

    setSelectedFiles(files);
    setFileError("");
    if (files.length === 1) {
      setFileName(files[0].name);
    } else if (files.length > 1) {
      setFileName(`${files.length} файлів вибрано`);
    } else {
      setFileName("Жоден файл не вибраний");
    }
  };

  const handleClearFile = () => {
    setSelectedFiles([]);
    setFileName("Жоден файл не вибраний");
    setFileError("");

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleCloseUploadModal = () => {
    setUploadData({ ...INITIAL_UPLOAD_STATE });
    handleClearFile();
    setIsUploadModalOpen(false);
  };

  const handleUploadInputChange = (e) => {
    const { name, value } = e.target;
    setUploadData(prev => ({ ...prev, [name]: value }));
    
    // Clear files and error when type changes
    if (name === "type") {
      handleClearFile();
    }
  };

  const handleUploadSubmit = async (e) => {
    e.preventDefault();
    if (selectedFiles.length === 0 || !uploadData.title || !uploadData.categoryId) {
      alert("Будь ласка, заповніть обов'язкові поля та виберіть хоча б один файл.");
      return;
    }

    const formData = new FormData();
    selectedFiles.forEach((file) => {
      formData.append("files", file);
    });
    formData.append("title", uploadData.title);
    if (uploadData.description) formData.append("description", uploadData.description);
    formData.append("type", uploadData.type);
    formData.append("categoryId", uploadData.categoryId);

    try {
      setIsUploading(true);
      const token = localStorage.getItem("token") || localStorage.getItem("authToken");
      await axios.post("http://localhost:9000/api/media/upload", formData, {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
          "Content-Type": "multipart/form-data",
        },
        timeout: 300000, // 5 minutes timeout for large files
        maxBodyLength: Infinity,
        maxContentLength: Infinity,
      });
      
      setUploadData({ ...INITIAL_UPLOAD_STATE });
      handleClearFile();
      setIsUploadModalOpen(false);
      alert("Матеріал успішно завантажено!");
      fetchResources({ page: 0, append: false });
    } catch (error) {
      console.error("Помилка завантаження матеріалу:", error);
      
      let errorMessage = "Не вдалося завантажити. Спробуйте ще раз.";
      if (error.code === "ECONNABORTED") {
        errorMessage = "Час очікування вичерпаний. Спробуйте завантажити менший файл.";
      } else if (error.message === "Network Error") {
        errorMessage = "Помилка мережі. Перевірте з'єднання та спробуйте ще раз.";
      } else if (error.response?.status === 413) {
        errorMessage = "Файл занадто великий. Використовуйте файл меншого розміру.";
      } else if (error.response?.status === 500) {
        errorMessage = "Помилка сервера. Спробуйте ще раз пізніше.";
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      
      alert(errorMessage);
    } finally {
      setIsUploading(false);
    }
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get("http://localhost:9000/api/media/categories");
        setCategories(response.data || []);
      } catch (error) {
        console.error("Error fetching media categories:", error);
      }
    };

    fetchCategories();
  }, []);

  const updatePageInfo = (pageData, isAppending) => {
    setResources((prev) => {
      const nextResources = isAppending ? [...prev, ...pageData.content] : pageData.content;
      const uniqueResources = [];
      const seenIds = new Set();

      nextResources.forEach((resource) => {
        const key = resource?.id || `${resource?.title || ""}-${resource?.createdAt || ""}`;
        if (seenIds.has(key)) {
          return;
        }

        seenIds.add(key);
        uniqueResources.push(resource);
      });

      return uniqueResources;
    });
    setPageInfo({
      page: pageData.number,
      totalPages: pageData.totalPages,
      last: pageData.last,
      loading: false,
    });
  };

  const fetchResources = async ({ page = 0, append = false } = {}) => {
    try {
      setPageInfo((prev) => ({ ...prev, loading: true }));
      const params = {
        page,
        size: PAGE_SIZE,
        sort: activeOrder === "oldest" || activeOrder === "longAgo" ? "createdAt,asc" : "createdAt,desc",
      };
      if (activeCategory !== "all") {
        params.categoryName = activeCategory;
      }
      if (activeType !== "all") {
        params.type = activeType;
      }

      const response = await axios.get("http://localhost:9000/api/media", { params });
      updatePageInfo(response.data, append);
    } catch (error) {
      console.error("Error fetching media resources:", error);
      setPageInfo((prev) => ({ ...prev, loading: false }));
    }
  };

  const fetchSearchResults = async ({ page = 0, append = false } = {}) => {
    const query = searchQuery.trim();
    if (!query) {
      return;
    }

    try {
      setPageInfo((prev) => ({ ...prev, loading: true }));
      const response = await axios.get("http://localhost:9000/api/media/search", {
        params: {
          query,
          page,
          size: PAGE_SIZE,
          sort: activeOrder === "oldest" || activeOrder === "longAgo" ? "createdAt,asc" : "createdAt,desc",
        },
      });
      updatePageInfo(response.data, append);
    } catch (error) {
      console.error("Error searching media resources:", error);
      setPageInfo((prev) => ({ ...prev, loading: false }));
    }
  };

  useEffect(() => {
    if (searchActive) {
      return;
    }

    fetchResources({ page: 0, append: false });
  }, [activeCategory, activeType, activeOrder, searchActive]);

  useEffect(() => {
    if (searchQuery.trim() === "" && searchActive) {
      setSearchActive(false);
      fetchResources({ page: 0, append: false });
    }
  }, [searchQuery, searchActive]);

  const handleSearchSubmit = (event) => {
    event.preventDefault();
    const trimmed = searchQuery.trim();
    if (!trimmed) {
      setSearchActive(false);
      fetchResources({ page: 0, append: false });
      return;
    }

    setSearchActive(true);
    fetchSearchResults({ page: 0, append: false });
  };

  const handleClearSearch = () => {
    setSearchQuery("");
    setSearchActive(false);
    fetchResources({ page: 0, append: false });
    if (searchInputRef.current) searchInputRef.current.focus();
  };

  const handleLoadMore = () => {
    if (pageInfo.loading || pageInfo.last) {
      return;
    }

    const nextPage = pageInfo.page + 1;
    if (searchActive) {
      fetchSearchResults({ page: nextPage, append: true });
    } else {
      fetchResources({ page: nextPage, append: true });
    }
  };

  const handleOpenResource = async (resource) => {
    const resourceUrl = resource?.url || resource?.fileUrls?.[0];
    if (!resource?.id || !resourceUrl) {
      return;
    }

    try {
      await fetch(`http://localhost:9000/api/media/${resource.id}/increment-views`, {
        method: "POST",
      });
    } catch (error) {
      console.error("Error incrementing views:", error);
    }

    window.open(resourceUrl, "_blank", "noopener,noreferrer");
  };

  const handleCategoryClick = (categoryName) => {
    setActiveCategory(categoryName);
    setSearchActive(false);
  };

  const handleTypeToggle = (type) => {
    setActiveType((prev) => (prev === type ? "all" : type));
    setSearchActive(false);
  };

  const handleOrderSelect = (order) => {
    setActiveOrder(order);
    setSearchActive(false);
  };

  const showEmptyState = !pageInfo.loading && resources.length === 0;

  return (
    <>
      <Header />
      <section className={styles.mediaHub}>
        <div className={styles.hero}>
          <div className={styles.heroText}>
            <h1 className={styles.title}>Медіатека</h1>
            <p className={styles.subtitle}>
              Всі корисні матеріали в одному місці: важливі документи, відео, фото та багато іншого.
            </p>
          </div>
          <button 
            className={styles.uploadButton} 
            onClick={() => setIsUploadModalOpen(true)}
          >
            + Додати матеріал
          </button>
        </div>
        <div className={styles.layout}>
          <aside className={styles.sidebar}>
            <h2 className={styles.sidebarTitle}>Категорії</h2>
            <div className={styles.categoryList}>
              <button
                type="button"
                className={`${styles.categoryItem} ${activeCategory === "all" ? styles.categoryActive : ""}`}
                onClick={() => handleCategoryClick("all")}
              >
                <span className={styles.categoryIcon}>*</span>
                Усі матеріали
              </button>
              {categories.map((category) => (
                <button
                  key={category.id}
                  type="button"
                  className={`${styles.categoryItem} ${activeCategory === category.name ? styles.categoryActive : ""}`}
                  onClick={() => handleCategoryClick(category.name)}
                >
                  <span className={styles.categoryIcon}>{category.name?.charAt(0) || "#"}</span>
                  {category.name}
                </button>
              ))}
            </div>
          </aside>

          <main className={styles.content}>
            <div className={styles.topBar}>
              <form className={styles.searchBar} onSubmit={handleSearchSubmit}>
                <div className={styles.searchInputWrap}>
                  <svg className={styles.searchIcon} aria-hidden="true" viewBox="0 0 24 24" focusable="false">
                    <path
                      d="M11 4a7 7 0 1 1 0 14 7 7 0 0 1 0-14zm0-2a9 9 0 1 0 5.65 16l4.67 4.68 1.42-1.42-4.68-4.67A9 9 0 0 0 11 2z"
                      fill="currentColor"
                    />
                  </svg>
                  <input
                    ref={searchInputRef}
                    type="search"
                    placeholder="Пошук матеріалів..."
                    value={searchQuery}
                    onChange={(event) => setSearchQuery(event.target.value)}
                    className={styles.searchInput}
                  />
                  {searchQuery && (
                    <button
                      type="button"
                      className={styles.resetButton}
                      onClick={handleClearSearch}
                      aria-label="Clear search"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd"></path>
                      </svg>
                    </button>
                  )}
                </div>
                <button type="submit" className={styles.searchButton}>
                  Знайти
                </button>
              </form>
              <details className={styles.filtersDropdown}>
                <summary className={styles.filtersToggle}>Фільтри</summary>
                <aside className={styles.filtersPanel} aria-label="Filters">
                  <div className={styles.filterGroup}>
                    <h3 className={styles.filterTitle}>Тип матеріалу</h3>
                    <div className={styles.filterList}>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeType === "all" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleTypeToggle("all")}
                      >
                        Усі матеріали
                      </button>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeType === "DOCUMENT" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleTypeToggle("DOCUMENT")}
                      >
                        Документ
                      </button>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeType === "VIDEO_LINK" || "VIDEO" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleTypeToggle("VIDEO_LINK")}
                      >
                        Відео
                      </button>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeType === "EXTERNAL_LINK" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleTypeToggle("EXTERNAL_LINK")}
                      >
                        Зовнішній лінк
                      </button>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeType === "IMAGE" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleTypeToggle("IMAGE")}
                      >
                        Світлина
                      </button>
                    </div>
                  </div>

                  <div className={styles.filterGroup}>
                    <h3 className={styles.filterTitle}>Сортування</h3>
                    <div className={styles.filterList}>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeOrder === "newest" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleOrderSelect("newest")}
                      >
                        Найновіші
                      </button>
                      <button
                        type="button"
                        className={`${styles.filterOption} ${activeOrder === "oldest" ? styles.filterOptionActive : ""}`}
                        onClick={() => handleOrderSelect("oldest")}
                      >
                        Найстаріші
                      </button>
                    </div>
                  </div>
                </aside>
              </details>
            </div>

            {showEmptyState && (
              <div className={styles.emptyState}>
                <h3>Нічого не знайдено</h3>
                <p>
                  Схоже, матеріали ще не додані або запит не дав результатів. Перейдіть у
                  <Link to="/main#faq" className={styles.faqLink}> ЧаПи</Link>, щоб отримати відповіді для вступників.
                </p>
              </div>
            )}

            <div className={styles.grid}>
              {resources.map((resource) => (
                <div key={resource.id} className={styles.cardWrap}>
                  <MediaCard resource={resource} onOpen={handleOpenResource} />
                </div>
              ))}
            </div>

            {!showEmptyState && !pageInfo.last && (
              <div className={styles.loadMoreRow}>
                <button
                  type="button"
                  className={styles.loadMore}
                  onClick={handleLoadMore}
                  disabled={pageInfo.loading}
                >
                  {pageInfo.loading ? "Завантаження..." : "Завантажити ще"}
                </button>
              </div>
            )}
          </main>
        </div>
      </section>

      {isUploadModalOpen && (
        <div className={styles.modalOverlay} onClick={handleCloseUploadModal}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <div className={styles.modalHeader}>
              <h2>Додати матеріал</h2>
              <button className={styles.closeButton} onClick={handleCloseUploadModal} type="button">
                &times;
              </button>
            </div>

            <form onSubmit={handleUploadSubmit} className={styles.uploadForm}>
              <div className={styles.formGroup}>
                <label>Назва *</label>
                <input
                  type="text"
                  name="title"
                  value={uploadData.title}
                  onChange={handleUploadInputChange}
                  required
                />
              </div>

              <div className={styles.formGroup}>
                <label>Опис</label>
                <textarea
                  name="description"
                  value={uploadData.description}
                  onChange={handleUploadInputChange}
                  rows="3"
                ></textarea>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label>Тип *</label>
                  <select
                    name="type"
                    value={uploadData.type}
                    onChange={handleUploadInputChange}
                    required
                  >
                    <option value="DOCUMENT">Документ</option>
                    <option value="VIDEO_LINK">Відео</option>
                    <option value="IMAGE">Світлина</option>
                    <option value="EXTERNAL_LINK">Зовнішній лінк</option>
                  </select>
                </div>

                <div className={styles.formGroup}>
                  <label>Категорія *</label>
                  <select
                    name="categoryId"
                    value={uploadData.categoryId}
                    onChange={handleUploadInputChange}
                    required
                  >
                    <option value="" disabled>Оберіть категорію</option>
                    {categories
                      .filter((c) => c.id)
                      .map((cat) => (
                        <option key={cat.id} value={cat.id}>
                          {cat.name}
                        </option>
                      ))}
                  </select>
                </div>
              </div>

              <div className={styles.formGroup}>
                <label>Файли *</label>
                <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>
                  Максимальний розмір: {uploadData.type === "VIDEO_LINK" ? "50MB" : uploadData.type === "IMAGE" ? "50MB" : "50MB"}
                </div>
                <div className={styles.fileUploadWrapper}>
                  <input
                    ref={fileInputRef}
                    type="file"
                    id="file-upload"
                    multiple
                    onChange={handleFileChange}
                    required
                    accept={getAcceptAttribute()}
                    className={styles.hiddenFileInput}
                  />
                  <label htmlFor="file-upload" className={styles.fileInputCustom}>
                    <span className={styles.customButton}>Вибрати файли</span>
                    <span className={styles.fileName}>{fileName}</span>
                  </label>
                </div>
                {fileError && (
                  <div style={{ color: "#9b4d57", fontSize: "14px", marginTop: "8px", fontWeight: "500" }}>
                    {fileError}
                  </div>
                )}
              </div>

              <button
                type="submit"
                className={styles.submitUpload}
                disabled={isUploading}
              >
                {isUploading ? "Завантаження..." : "Зберегти"}
              </button>
            </form>
          </div>
        </div>
      )}

      <Footer />
    </>
  );
}

export default MediaHub;
