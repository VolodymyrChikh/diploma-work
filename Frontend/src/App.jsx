import { BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom';
import Register from './pages/Auth Page/SignUp/Register';
import MainPage from './pages/MainPage/MainPage';
import Authenticate from './pages/Auth Page/SignIn/Authenticate';
import Forum from './pages/Forum Page/Forum'; 
import CreatePost from './pages/Create Post Page/CreatePost';
import Profile from './pages/Profile Page/Profile';
import PrivateRoute from './routes/PrivateRoute';
import AboutSpecialtyPage from './pages/About Specialty Page/AboutSpecialtyPage';
import PostDetail from './pages/Post Detail Page/PostDetail';
import AiChatPopup from './components/AiChatPopup/AiChatPopup';

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
        <Route path="/forum" element={<Forum />} />
        <Route path="/post/:id" element={<PostDetail />} />
        <Route path="/create-post" element={<PrivateRoute><CreatePost /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile/></PrivateRoute>} />
        <Route path="*" element={<div style={{ display: 'flex', justifyContent: 'center', color: 'red', fontSize: '48px', }}><p>404 Not Found</p></div>} />
      </Routes>
    </Router>
  );
}

export default App;
