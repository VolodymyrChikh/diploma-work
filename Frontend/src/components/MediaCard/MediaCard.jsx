import { useState } from "react";
import PropTypes from "prop-types";
import styles from "./MediaCard.module.css";

function MediaCard({ resource, onOpen }) {
  const { title, description, type } = resource;
  
  const getDisplayTypeLabel = (resourceType) => {
    const typeMap = {
      "VIDEO_LINK": "VIDEO",
      "EXTERNAL_LINK": "LINK",
      "DOCUMENT": "DOCUMENT",
      "IMAGE": "IMAGE"
    };
    return typeMap[resourceType] || resourceType || "RESOURCE";
  };
  
  const typeLabel = getDisplayTypeLabel(type);
  const isImageType = type === "IMAGE";
  const isVideoType = type === "VIDEO_LINK" || type === "VIDEO";
  const isDocumentType = type === "DOCUMENT";
  const isLinkType = type === "EXTERNAL_LINK" || type === "LINK";
  const isImageUrl = (url) => !!(url && /\.(png|jpe?g|gif|webp|avif|svg)(\?.*)?$/i.test(url));
  const getFileExtension = (url) => {
    if (!url) {
      return "DOC";
    }

    const cleanPath = url.split("?")[0].split("#")[0];
    const fileName = cleanPath.split("/").pop() || "";
    const extension = fileName.includes(".") ? fileName.split(".").pop() : "";

    return extension ? extension.toUpperCase() : "DOC";
  };
  const fileUrls = Array.isArray(resource.fileUrls) && resource.fileUrls.length > 0
    ? resource.fileUrls
    : resource.url
      ? [resource.url]
      : [];
  const [filesExpanded, setFilesExpanded] = useState(false);
  const primaryUrl = fileUrls[0];
  const isCompact = fileUrls.length > 2 && !filesExpanded;
  const visibleFileUrls = isCompact ? fileUrls.slice(0, 2) : fileUrls;
  const overflowCount = Math.max(fileUrls.length - 2, 0);

  const openResource = (url = primaryUrl) => {
    if (!url) {
      return;
    }

    if (onOpen) {
      onOpen({ ...resource, url, fileUrls });
    }
  };

  const handleCardClick = () => {
    if (fileUrls.length > 2) {
      setFilesExpanded((current) => !current);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      handleCardClick();
    }
  };

  const renderPreview = () => {
    if (fileUrls.length === 0) {
      return null;
    }

    return (
      <div className={`${styles.filesStrip} ${filesExpanded ? styles.filesStripExpanded : styles.filesStripCollapsed}`}>
        <div className={styles.filesIcons}>
          {visibleFileUrls.map((url, index) => {
            const showImage = isImageUrl(url);
            return (
              <button
                key={url}
                type="button"
                className={styles.fileIconButton}
                onClick={(event) => {
                  event.stopPropagation();
                  openResource(url);
                }}
                aria-label={`Відкрити файл ${index + 1}`}
              >
                {showImage ? (
                  <img
                    src={url}
                    alt={`Файл ${index + 1}`}
                    className={styles.fileThumb}
                    loading="lazy"
                    onError={(e) => {
                      e.currentTarget.style.display = "none";
                    }}
                  />
                ) : (
                  <span className={styles.fileIcon} aria-hidden="true">
                    {isVideoType ? "▶" : isDocumentType ? getFileExtension(url) : isLinkType ? "↗" : "🖼"}
                  </span>
                )}
              </button>
            );
          })}
        </div>

        {fileUrls.length > 2 && !filesExpanded && (
          <button
            type="button"
            className={styles.moreFilesBubble}
            onClick={(event) => {
              event.stopPropagation();
              setFilesExpanded(true);
            }}
            aria-label={`Показати ще ${overflowCount} файли`}
          >
            +{overflowCount}
          </button>
        )}

        {filesExpanded && fileUrls.length > 2 && (
          <button
            type="button"
            className={styles.moreFilesBubble}
            onClick={(event) => {
              event.stopPropagation();
              setFilesExpanded(false);
            }}
            aria-label="Згорнути список файлів"
          >
            Менше
          </button>
        )}
      </div>
    );
  };

  const renderIcon = () => {
    switch (type) {
      case "DOCUMENT":
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <path d="M7 3h7l5 5v13a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm7 1.5V9h4.5" fill="none" stroke="currentColor" strokeWidth="1.5" />
            <path d="M8 12h8M8 15h8M8 18h6" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
      case "VIDEO":
      case "VIDEO_LINK":
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <rect x="3" y="6" width="18" height="12" rx="2" fill="none" stroke="currentColor" strokeWidth="1.5" />
            <path d="M10 9l5 3-5 3z" fill="currentColor" />
          </svg>
        );
      case "LINK":
      case "EXTERNAL_LINK":
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <path d="M10 7H7a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2v-3" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            <path d="M14 4h6v6" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            <path d="M10 14L20 4" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
      case "IMAGE":
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <rect x="4" y="5" width="16" height="14" rx="2" fill="none" stroke="currentColor" strokeWidth="1.5" />
            <circle cx="9" cy="10" r="1.5" fill="currentColor" />
            <path d="M4 16l4-4 4 4 3-3 5 5" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        );
      default:
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" strokeWidth="1.5" />
            <path d="M8 12h8" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
    }
  };

  return (
    <article
      className={styles.card}
      onKeyDown={handleKeyDown}
      role="button"
      tabIndex={0}
    >
      <div className={styles.iconRow}>
        <div className={styles.iconWrap}>
          {renderIcon()}
        </div>
        <span className={styles.typePill}>{typeLabel}</span>
      </div>
      {renderPreview()}
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.description}>{description || "Опис відсутній"}</p>
      <button
        className={styles.cta}
        type="button"
        onClick={(event) => {
          event.stopPropagation();
          openResource();
        }}
      >
        Переглянути
      </button>
    </article>
  );
}

MediaCard.propTypes = {
  resource: PropTypes.shape({
    id: PropTypes.string,
    title: PropTypes.string,
    description: PropTypes.string,
    url: PropTypes.string,
    files: PropTypes.array,
    fileUrls: PropTypes.arrayOf(PropTypes.string),
    type: PropTypes.string,
  }).isRequired,
  onOpen: PropTypes.func,
};

MediaCard.defaultProps = {
  onOpen: null,
};

export default MediaCard;
