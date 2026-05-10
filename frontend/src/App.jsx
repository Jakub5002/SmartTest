import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { useAuth } from './context/AuthContext';

function App() {
    const { user, loading } = useAuth();

    if (loading) return <div>Ładowanie systemu...</div>;

    return (
        <Routes>
            {/* Jeśli nie jesteś zalogowany, zawsze lądujesz na /login */}
            <Route path="/login" element={!user ? <LoginPage /> : <Navigate to="/" />} />

            {/* Strona główna - na razie tylko napis, dopóki nie zrobisz Dashboardu */}
            <Route path="/" element={user ? <h1>Witaj, {user.email}! Jesteś zalogowany.</h1> : <Navigate to="/login" />} />
        </Routes>
    );
}

export default App;