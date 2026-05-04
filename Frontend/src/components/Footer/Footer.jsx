import styles from './Footer.module.css';

function Footer(){
    
    const handleCopy = (e) => {
        e.preventDefault();
    };

    return(
        <footer className={styles.footer} onCopy={handleCopy}>
            <div className={styles.footerBlockFirst}>
                <h1 className={styles.headerTitle}>
                    <span className={styles.ami}>AMI</span>
                    <span className={styles.portal}>Portal</span>
                </h1>  
                <div className={styles.techQuestion}>
                    <p>Питання з технічного боку?</p>
                    <span>
                        Напишіть на пошту:&nbsp;  
                        <a href="mailto:volodymyr.university@gmail.com
                                    ?subject=Привіт%20від%20ФПМІ">
                                        volodymyr.university@gmail.com
                    </a>
                    </span>
                </div>
                <p className={styles.reservedRight}>&copy; {new Date().getFullYear()} AMI Portal | ФПМІ ЛНУ ім. Івана Франка. Усі права захищено.</p>              
            </div>
            <div className={styles.footerBlockSecond}>
                <ul className={styles.footerHeaderNav}>
                    <li><a href="/main">Головна</a></li>
                    <li><a href="/about-specialties">Про спеціальності</a></li>
                    <li><a href="/course-map">Карта курсів</a></li>
                    <li><a href="/forum">Форум</a></li>
                    <li><a href="/profile">Профіль</a></li>
                </ul>
            </div>
            <div className={styles.footerBlockThird}>
                <h2>Студентська рада</h2>
                <div className={styles.footerBlockThirdInfo}>
                    <span><a href="mailto:student.counsil@lnu.edu.ua
                                        ?subject=Привіт%20від%20ФПМІ">
                                            student.counsil@lnu.edu.ua
                            </a>
                    </span>
                    <span><a href="tel:+380938572398">+380938572398</a>
                    </span>
                    <div className={styles.footerBlockThirdSocials}>
                        <a href="https://t.me/ami_lnu" target="_blank" rel="noopener noreferrer">
                            <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 25 25" fill="none">
                                <path fill-rule="evenodd" clip-rule="evenodd" d="M20.6006 4.61461C20.858 4.50627 21.1398 4.4689 21.4165 4.5064C21.6933 4.54389 21.9549 4.65489 22.1742 4.82782C22.3935 5.00076 22.5625 5.22931 22.6635 5.48969C22.7645 5.75008 22.7938 6.03278 22.7485 6.30836L20.386 20.6386C20.1568 22.0209 18.6402 22.8136 17.3725 22.125C16.3121 21.549 14.7371 20.6615 13.3204 19.7354C12.6121 19.2719 10.4423 17.7875 10.7089 16.7313C10.9381 15.8282 14.5839 12.4344 16.6673 10.4167C17.485 9.62398 17.1121 9.16669 16.1464 9.89586C13.7475 11.7063 9.89851 14.4594 8.6256 15.2344C7.50268 15.9177 6.91726 16.0344 6.21726 15.9177C4.94018 15.7052 3.75581 15.3761 2.78914 14.975C1.48289 14.4334 1.54643 12.6375 2.7881 12.1146L20.6006 4.61461Z" fill="#1E1E1E" fill-opacity="0.76"/>
                            </svg>
                        </a>
                        <a href="https://www.tiktok.com/@ami_lnu" target="_blank" rel="noopener noreferrer">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M16.6002 5.82C15.9166 5.03962 15.5399 4.03743 15.5402 3H12.4502V15.4C12.4263 16.071 12.143 16.7066 11.6599 17.1729C11.1768 17.6393 10.5316 17.8999 9.86016 17.9C8.44016 17.9 7.26016 16.74 7.26016 15.3C7.26016 13.58 8.92016 12.29 10.6302 12.82V9.66C7.18016 9.2 4.16016 11.88 4.16016 15.3C4.16016 18.63 6.92016 21 9.85016 21C12.9902 21 15.5402 18.45 15.5402 15.3V9.01C16.7932 9.90985 18.2975 10.3926 19.8402 10.39V7.3C19.8402 7.3 17.9602 7.39 16.6002 5.82Z" fill="#1E1E1E" fill-opacity="0.76"/>
                            </svg>
                        </a>
                        <a href="https://www.instagram.com/ami_lnu/" target="_blank" rel="noopener noreferrer">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M17.2 5.62857H18.7429M7.17143 1H16.4286C18.0653 1 19.6351 1.6502 20.7924 2.80757C21.9498 3.96494 22.6 5.53466 22.6 7.17143V16.4286C22.6 18.0653 21.9498 19.6351 20.7924 20.7924C19.6351 21.9498 18.0653 22.6 16.4286 22.6H7.17143C5.53466 22.6 3.96494 21.9498 2.80757 20.7924C1.6502 19.6351 1 18.0653 1 16.4286V7.17143C1 5.53466 1.6502 3.96494 2.80757 2.80757C3.96494 1.6502 5.53466 1 7.17143 1ZM11.8 16.4286C10.5724 16.4286 9.39513 15.9409 8.52711 15.0729C7.65908 14.2049 7.17143 13.0276 7.17143 11.8C7.17143 10.5724 7.65908 9.39513 8.52711 8.52711C9.39513 7.65908 10.5724 7.17143 11.8 7.17143C13.0276 7.17143 14.2049 7.65908 15.0729 8.52711C15.9409 9.39513 16.4286 10.5724 16.4286 11.8C16.4286 13.0276 15.9409 14.2049 15.0729 15.0729C14.2049 15.9409 13.0276 16.4286 11.8 16.4286Z" stroke="#1E1E1E" stroke-opacity="0.76"/>
                            </svg>
                        </a>
                        <a href="https://www.youtube.com/@ami_lnu" target="_blank" rel="noopener noreferrer">
                        <svg fill='#1E1E1E' enable-background="new 0 0 24 24" height="25px" width="25px" id="Layer_1" version="1.1" viewBox="0 0 24 24"  xml:space="preserve" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><g><path d="M23.3,7.3c0-0.2-0.3-1.8-1-2.5c-0.9-1-1.9-1.1-2.4-1.1l-0.1,0c-3.1-0.2-7.7-0.2-7.8-0.2c0,0-4.7,0-7.8,0.2l-0.1,0   c-0.5,0-1.5,0.1-2.4,1.1c-0.7,0.8-1,2.4-1,2.6c0,0.1-0.2,1.9-0.2,3.8v1.7c0,1.9,0.2,3.7,0.2,3.8c0,0.2,0.3,1.8,1,2.5   c0.8,0.9,1.8,1,2.4,1.1c0.1,0,0.2,0,0.3,0c1.8,0.2,7.3,0.2,7.5,0.2c0,0,0,0,0,0c0,0,4.7,0,7.8-0.2l0.1,0c0.5-0.1,1.5-0.2,2.4-1.1   c0.7-0.8,1-2.4,1-2.6c0-0.1,0.2-1.9,0.2-3.8v-1.7C23.5,9.3,23.3,7.4,23.3,7.3z M15.9,12.2l-6,3.2c-0.1,0-0.1,0.1-0.2,0.1   c-0.1,0-0.2,0-0.2-0.1c-0.1-0.1-0.2-0.2-0.2-0.4l0-6.5c0-0.2,0.1-0.3,0.2-0.4S9.8,8,10,8.1l6,3.2c0.2,0.1,0.3,0.2,0.3,0.4   S16.1,12.1,15.9,12.2z"/></g></svg>
                        </a>
                        </div>
                </div>
            </div>
            <div className={styles.footerBlockFourth}>
                <h2>Деканат</h2>
                <div className={styles.footerBlockThirdInfo}>
                    <span><a href="mailto:ami.faculty@lnu.edu.ua
                                        ?subject=Привіт%20від%20ФПМІ">
                                            ami.faculty@lnu.edu.ua
                            </a>
                    </span>
                    <span><a href="tel:+380322394727">+38 032 239-47-27</a>
                    </span>
                    <span>вул. Університетська, 1, ФПМІ, Львівський національний університет імені Івана Франка</span>
                </div>
            </div>

        </footer>
    );
}

export default Footer