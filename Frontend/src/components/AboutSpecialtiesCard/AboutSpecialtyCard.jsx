import styles from './AboutSpecialtyCard.module.css';
import PropTypes from 'prop-types';

function AboutSpecialtyCard(props) {
    return (
        <div className={styles.cardContainer}>
        <div className={styles.imageWrapper}>
            <img src={props.specialtyImage} alt={props.imageTitle} className={styles.image} />
            <div className={styles.textContainer}>
                <div className={styles.specialtyWrapper}>
                    <h3 className={styles.specialtyName}>{props.imageTitle}</h3>
                </div>
                <div className={styles.textWrapper}>
                    <span className={styles.text}>{props.imageText}</span>
                </div>
            </div>
        </div>
        </div>
    );
}

AboutSpecialtyCard.propTypes = {
    specialtyImage: PropTypes.string.isRequired,
    imageTitle: PropTypes.string.isRequired,
    imageText: PropTypes.string.isRequired,
};

AboutSpecialtyCard.defaultProps = {
    imageTitle: "113 - Прикладна математика",
};

export default AboutSpecialtyCard