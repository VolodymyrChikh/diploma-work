import styles from './RedButton.module.css';
import PropTypes from 'prop-types'; 

function RedButton({text}){

    return(
        <button className={styles.button}>{text}</button>
    );
}

RedButton.propTypes = {
    text: PropTypes.string.isRequired
};

export default RedButton