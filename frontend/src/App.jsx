import { Routes, Route, Navigate } from 'react-router-dom';

// LAYOUTY
import AdminLayout from './layouts/AdminLayout';
import StudentLayout from './layouts/StudentLayout';

// CONTEXT
import { useAuth } from './context/AuthContext';

// STRONY WSPÓLNE
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';

// STRONY ADMINA
import AdminStats from './pages/admin/AdminStats';
import ManageExams from './pages/admin/ManageExams';

// STRONY STUDENTA
import AvailableExams from './pages/student/AvailableExams';
import StudentResults from './pages/student/StudentResults';

import AllExams from './pages/AllExams';
import TakeExam from './pages/TakeExam';
function App() {
    const { user, loading } = useAuth();

    if (loading) return <div>Ładowanie systemu...</div>;

    return (
        <Routes>
            {/* Publiczne */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* --- SEKCJA WSPÓLNA DLA ZALOGOWANYCH (Profil) --- */}
            <Route element={user ? (user.role === 'ROLE_ADMIN' ? <AdminLayout /> : <StudentLayout />) : <Navigate to="/login" />}>
                <Route path="/profile" element={<ProfilePage />} />
            </Route>

            {/* ADMIN  */}
            <Route element={user?.role === 'ROLE_ADMIN' ? <AdminLayout /> : <Navigate to="/login" />}>
                <Route path="/admin" element={<AdminStats />} />
                <Route path="/admin/exams" element={<ManageExams />} />
            </Route>

            {/* STUDENT */}
            <Route element={user?.role === 'ROLE_STUDENT' ? <StudentLayout /> : <Navigate to="/login" />}>
                <Route path="/student" element={<AvailableExams />} />
                <Route paht="/exams" element={<AllExams/>} />
                <Route path="/student/results" element={<StudentResults />} />
                <Route path="/exam/:id" element={<TakeExam />} />
            </Route>

            {/* przekierowanie */}
            <Route path="/" element={
                user ? (user.role === 'ROLE_ADMIN' ? <Navigate to="/admin" /> : <Navigate to="/student" />) : <Navigate to="/login" />
            } />
        </Routes>
    );
}

export default App;