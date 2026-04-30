import styles from "./Forum.module.css";
import Header from "../../components/Header/Header";
import forumPageImage from "../../assets/images/forumPageImage.png";
import { useState, useEffect, useCallback } from "react";
import Post from "../../components/Post/Post";
import Footer from "../../components/Footer/Footer";

const POSTS_PER_PAGE = 10;

function Forum() {
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [selectedNavItem, setSelectedNavItem] = useState('Популярні');
    const [posts, setPosts] = useState([]);
    const [allPosts, setAllPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [prevPosts, setPrevPosts] = useState([]);

    const clearSearch = () => {
        setSearchQuery('');
        console.log("Search cleared, showing all posts:", allPosts.length);
    };
    const handleSearchChange = (event) => {
        setSearchQuery(event.target.value);
    };

    const handleCategoryClick = (category) => {
        if (selectedCategories.includes(category)) {
            setSelectedCategories(selectedCategories.filter(cat => cat !== category));
        } else {
            setSelectedCategories([...selectedCategories, category]);
        }
    };

    const getCategoryStyle = (category) => {
        if (selectedCategories.includes(category)) {
            return {
                color: '#9B4D57',
                fontWeight: '600'
            };
        }
        return {};
    };

    const handleNavItemClick = (item) => {
        setSelectedNavItem(item);
    };

    const getNavItemStyle = (item) => {
        if (selectedNavItem === item) {
            return {
                color: '#9B4D57',
                fontWeight: '600',
                position: 'relative',
                display: 'inline-block',
                paddingBottom: '2px',
            };
        }
        return {};
    };

    const getActiveClass = (item) => {
        return selectedNavItem === item ? styles.activeNavItem : '';
    };

    const fetchPosts = useCallback(async (page, reset = false) => {
        setLoading(true);
        setError(null);

        let url = 'http://localhost:9000/posts';
        let queryParams = `?page=${page}&size=${POSTS_PER_PAGE}`;
        let applyClientSideCategoryFilter = false;

        let sortParamForCategoryEndpoint = '';
        if (selectedNavItem === 'Популярні') {
            sortParamForCategoryEndpoint = 'sort=likes,desc';
        } else if (selectedNavItem === 'Нові') {
            sortParamForCategoryEndpoint = 'sort=createdAt,desc';
        } else if (selectedNavItem === 'Старі') {
            sortParamForCategoryEndpoint = 'sort=createdAt,asc';
        }

        if (selectedCategories.length === 1) {
            url += `/by-category-name/${selectedCategories[0]}`;
            if (sortParamForCategoryEndpoint) {
                queryParams += `&${sortParamForCategoryEndpoint}`;
            }
        } else {
            if (selectedNavItem === 'Популярні') {
                url += '/by-popularity';
            } else if (selectedNavItem === 'Нові') {
                url += '/by-new';
            } else if (selectedNavItem === 'Старі') {
                url += '/by-old';
            }

            if (selectedCategories.length > 1) {
                applyClientSideCategoryFilter = true;
            }
        }

        try {
            const response = await fetch(url + queryParams);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.json();
            let fetchedPosts = Array.isArray(data.content) ? data.content : (Array.isArray(data) ? data : []);

            if (applyClientSideCategoryFilter) {
                fetchedPosts = fetchedPosts.filter(post =>
                    post.categoryResponse && selectedCategories.includes(post.categoryResponse.name)
                );
            }

            setAllPosts(prevAll => (reset ? fetchedPosts : [...prevAll, ...fetchedPosts]));
            setPosts(prevPosts => (reset ? fetchedPosts : [...prevPosts, ...fetchedPosts]));
            setHasMore(fetchedPosts.length === POSTS_PER_PAGE);
            setPrevPosts([]);
        } catch (e) {
            console.error("Failed to fetch posts:", e);
            setError(e.message);
            setHasMore(false);
        } finally {
            setLoading(false);
        }
    }, [selectedNavItem, selectedCategories]);

    useEffect(() => {
        setPrevPosts(posts);
        setCurrentPage(0);
        setHasMore(true);
        fetchPosts(0, true);
    }, [selectedNavItem, selectedCategories]);

    useEffect(() => {
        if (searchQuery) return;
        fetchPosts(currentPage, currentPage === 0);
    }, [currentPage, fetchPosts, searchQuery]);


    useEffect(() => {
        if (!searchQuery) {
            setPosts(allPosts);
            setHasMore(allPosts.length >= POSTS_PER_PAGE);
            return;
        }

        console.log("Searching for:", searchQuery, "in", allPosts.length, "posts");

        const filtered = allPosts.filter(post =>
            post.title && post.title.toLowerCase().includes(searchQuery.toLowerCase())
        );

        console.log("Found", filtered.length, "matching posts");

        setPosts(filtered);
        setHasMore(false);
    }, [searchQuery, allPosts]);

    const loadMorePosts = () => {
        if (!loading && hasMore) {
            setCurrentPage(prev => prev + 1);
        }
    };

    const [lastComments, setLastComments] = useState({});

    useEffect(() => {
        fetch('http://localhost:9000/comments/last-comments')
            .then(res => res.json())
            .then(data => setLastComments(data));
    }, []);

    const mapCommentsByPostId = (comments) => {
        if (!comments) return {};

        const mapped = {};
        const list = Array.isArray(comments) ? comments : Object.values(comments);

        list.forEach(comment => {
            const postId = comment?.postResponse?.id;
            if (postId) {
                mapped[postId] = comment;
            }
        });

        return mapped;
    };

    const lastCommentsByPostId = mapCommentsByPostId(lastComments);

    return (
        <div className={styles.forumPage}>
            <Header />
            <div className={styles.forumImageContainer}>
                <img className={styles.forumImage} src={forumPageImage} alt="Фото форуму"></img>
                <span className={styles.centeredTextFirst}>ФОРУМ СТУДЕНТІВ ФАКУЛЬТЕТУ ПРИКЛАДНОЇ МАТЕМАТИКИ ТА ІНФОРМАТИКИ</span>
                <span className={styles.centeredTextSecond}>Обговорюй, питай та ділися думками!</span>
            </div>
            <div className={styles.forumContainer}>
                <div className={styles.forumHeader}>
                    <div className={styles.searchContainer}>
                        <div className={styles.searchIcon}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" viewBox="0 0 19 19" fill="none">
                                <path d="M17.7314 15.2038L12.6628 10.0991L10.04 12.7386L15.1124 17.8433C15.5286 18.2622 16.2138 18.2622 16.63 17.8433L17.7314 16.7349C18.1477 16.3123 18.1477 15.619 17.7314 15.2038Z" fill="#828282"/>
                                <path d="M10.1543 11.6005L11.5283 10.2178L9.96277 8.64225C11.4436 6.49582 11.2373 3.5227 9.33656 1.60982C7.20375 -0.536607 3.73748 -0.536607 1.60099 1.60982C-0.535504 3.75625 -0.53182 7.24466 1.60099 9.3948C3.50173 11.3077 6.45598 11.5153 8.58879 10.025L10.1543 11.6005ZM2.66186 8.31232C1.11475 6.75532 1.11475 4.23076 2.66186 2.67748C4.20898 1.12048 6.71751 1.12048 8.26094 2.67748C9.80806 4.23447 9.80806 6.75903 8.26094 8.31232C6.71751 9.86931 4.20898 9.86931 2.66186 8.31232Z" fill="#828282"/>
                            </svg>
                        </div>
                        <input
                            className={styles.searchField}
                            type="text"
                            required
                            placeholder="Пошук..."
                            id="search"
                            value={searchQuery}
                            onChange={handleSearchChange}
                        />
                        <button className={styles.resetButton} type="reset" onClick={clearSearch}>
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd"></path>
                            </svg>
                        </button>
                    </div>
                    <div className={styles.forumHeaderNav}>
                        <ul className={styles.forumHeaderList}>
                            <li
                                className={styles.forumHeaderListItem}
                                onClick={() => handleCategoryClick('Навчання')}
                                style={getCategoryStyle('Навчання')}
                            >
                                Навчання
                            </li>
                            <li
                                className={styles.forumHeaderListItem}
                                onClick={() => handleCategoryClick('Події')}
                                style={getCategoryStyle('Події')}
                            >
                                Події
                            </li>
                            <li
                                className={styles.forumHeaderListItem}
                                onClick={() => handleCategoryClick('Поради')}
                                style={getCategoryStyle('Поради')}
                            >
                                Поради
                            </li>
                            <li
                                className={styles.forumHeaderListItem}
                                onClick={() => handleCategoryClick('FAQ')}
                                style={getCategoryStyle('FAQ')}
                            >
                                FAQ
                            </li>
                            <li
                                className={styles.forumHeaderListItem}
                                onClick={() => handleCategoryClick('Гумор')}
                                style={getCategoryStyle('Гумор')}
                            >
                                Гумор
                            </li>
                        </ul>
                    </div>
                    <div className={styles.buttonContainer}>
                        <a href="/create-post" className={styles.createPostLink}>
                            <button className={styles.createPostButton}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" fill="white" stroke="white">
                                    <path d="M10.0001 2.5C10.0001 2.22386 10.2239 2 10.5001 2C10.7763 2 11.0001 2.22386 11.0001 2.5V17.5C11.0001 17.7761 10.7763 18 10.5001 18C10.2239 18 10.0001 17.7761 10.0001 17.5V2.5Z" fill="#828282"/>
                                    <path d="M17.5 9C17.7761 9 18 9.22386 18 9.5C18 9.77614 17.7761 10 17.5 10H2.5C2.22386 10 2 9.77614 2 9.5C2 9.22386 2.22386 9 2.5 9H17.5Z" fill="#828282"/>
                                </svg>
                            </button>
                        </a>
                    </div>
                </div>
                <div className={styles.forumInnerContainer}>
                    <div className={styles.forumHeaderNavInner}>
                        <ul className={styles.forumHeaderListInner}>
                            <li
                                className={`${styles.forumHeaderListItemInner} ${getActiveClass('Популярні')}`}
                                onClick={() => handleNavItemClick('Популярні')}
                                style={getNavItemStyle('Популярні')}
                            >
                                Популярні
                            </li>
                            <li
                                className={`${styles.forumHeaderListItemInner} ${getActiveClass('Нові')}`}
                                onClick={() => handleNavItemClick('Нові')}
                                style={getNavItemStyle('Нові')}
                            >
                                Нові
                            </li>
                            <li
                                className={`${styles.forumHeaderListItemInner} ${getActiveClass('Старі')}`}
                                onClick={() => handleNavItemClick('Старі')}
                                style={getNavItemStyle('Старі')}
                            >
                                Старі
                            </li>
                        </ul>
                    </div>                    <div className={styles.forumPostsContainer}>
                        {error && <p>Помилка завантаження постів: {error}</p>}
                        {!error && (loading ? prevPosts : posts).length === 0 && <p>Немає постів для відображення.</p>}
                        {!error && (loading ? prevPosts : posts).map(post => (
                            <Post
                                key={`post-${post.id}-${post.likes || 0}`}
                                {...post}
                                lastComment={lastCommentsByPostId[post.id]} 
                            />
                        ))}
                        {!loading && !error && hasMore && (
                            <button className={styles.loadMoreButton} onClick={loadMorePosts} disabled={loading}>
                                {loading ? 'Завантаження...' : 'Завантажити більше'}
                            </button>
                        )}
                    </div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}

export default Forum;