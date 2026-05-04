import { BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom';
import Register from './pages/Auth Page/SignUp/Register';
import MainPage from './pages/MainPage/MainPage';
import Authenticate from './pages/Auth Page/SignIn/Authenticate';
import Forum from './pages/Forum Page/Forum'; 
import CreatePost from './pages/Create Post Page/CreatePost';
import Profile from './pages/Profile Page/Profile';
import PrivateRoute from './routes/PrivateRoute';
import AboutSpecialtyPage from './pages/About Specialty Page/AboutSpecialtyPage';
import CourseMapPage from './pages/Course Map Page/CourseMapPage';
import PostDetail from './pages/Post Detail Page/PostDetail';
import AiChatPopup from './components/AiChatPopup/AiChatPopup';
import NotFound from './components/Errors/NotFound/NotFound';
import MediaHub from './pages/MediaHub/MediaHub';

function App() {
  return (
    <Router>
      <AiChatPopup />
      <Routes>
        <Route path="/signup" element={<Register />} />
        <Route path="/signin" element={<Authenticate />} />
        <Route path="/" element={<Navigate to="/main" replace />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/about-specialties" element={<AboutSpecialtyPage />} />
        <Route path="/course-map" element={<CourseMapPage />} />
        <Route path="/forum" element={<Forum />} />
        <Route path="/forum/post/:slug" element={<PostDetail />} />
        <Route path="/post/:slug" element={<PostDetail />} />
        <Route path="/media" element={<MediaHub />} />
        <Route path="/create-post" element={<PrivateRoute><CreatePost /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile/></PrivateRoute>} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Router>
  );
}

export default App;
