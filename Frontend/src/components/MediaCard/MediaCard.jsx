import PropTypes from "prop-types";
import styles from "./MediaCard.module.css";

function MediaCard({ resource, onOpen }) {
  const { title, description, type } = resource;
  const typeLabel = type || "RESOURCE";

  const handleOpen = () => {
    if (onOpen) {
      onOpen(resource);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      handleOpen();
    }
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
      case "VIDEO_LINK":
        return (
          <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
            <rect x="3" y="6" width="18" height="12" rx="2" fill="none" stroke="currentColor" strokeWidth="1.5" />
            <path d="M10 9l5 3-5 3z" fill="currentColor" />
          </svg>
        );
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
      onClick={handleOpen}
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
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.description}>{description || "Опис відсутній"}</p>
      <button
        className={styles.cta}
        type="button"
        onClick={(event) => {
          event.stopPropagation();
          handleOpen();
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
    type: PropTypes.string,
  }).isRequired,
  onOpen: PropTypes.func,
};

MediaCard.defaultProps = {
  onOpen: null,
};

export default MediaCard;
