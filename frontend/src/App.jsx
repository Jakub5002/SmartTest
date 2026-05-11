import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { useAuth } from './context/AuthContext';
import RegisterPage from "./pages/RegisterPage.jsx";

function App() {
    const { user, loading } = useAuth();

    if (loading) return <div>Ładowanie systemu...</div>;

    return (
        <Routes>
            <Route path="/login" element={<LoginPage/>} />
            <Route path="/register" element={<RegisterPage/>} />
            <Route path="/" element={user ? <Dashboard /> : <Navigate to="/login" />} />
        </Routes>
    );
}

export default App;