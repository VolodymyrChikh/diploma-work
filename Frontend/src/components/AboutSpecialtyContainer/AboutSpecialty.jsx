import styles from './AboutSpecialty.module.css';
import PropTypes from 'prop-types';

function AboutSpecialty(props) {
  return (
    <div className={styles.container}>
      <div className={styles.innerContainer}>
        <div className={styles.titleContainer}>
          <h1 className={styles.specialtyNumber}>{props.specialtyNumber}</h1>
          <h3 className={styles.specialtyName}>{props.specialtyName}</h3>
        </div>
        <p className={styles.text}>{props.text}</p>
        <div className={styles.specialtyDetails}>
          <div className={styles.whatYouLearn}>
            <h3>📘 Що ти тут вивчатимеш?</h3>
            <ul>
              {props.whatYouLearn.map((item, idx) => (
                <li key={idx}>{item}</li>
              ))}
            </ul>
          </div>
          <div className={styles.careerOpportunities}>
            <h3>🎯 Кар’єрні можливості</h3>
            <ul>
              {props.careerOpportunities.map((item, idx) => (
                <li key={idx}>{item}</li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

AboutSpecialty.propTypes = {
    specialtyNumber: PropTypes.string,
    specialtyName: PropTypes.string,
    text: PropTypes.string,
    whatYouLearn: PropTypes.arrayOf(PropTypes.string),
    careerOpportunities: PropTypes.arrayOf(PropTypes.string)
};

export default AboutSpecialty;