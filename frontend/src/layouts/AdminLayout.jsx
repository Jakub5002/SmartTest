import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from "../context/AuthContext";

const AdminLayout = () => {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div style={{ display: 'flex', minHeight: '100vh' }}>
            {/* Lewy pasek boczny - Ciemny motyw dla Admina */}
            <aside style={{
                width: '250px',
                background: '#2c3e50',
                color: 'white',
                padding: '20px',
                display: 'flex',
                flexDirection: 'column'
            }}>
                <h2 style={{ marginBottom: '30px' }}>Panel Admina</h2>

                <nav style={{ display: 'flex', flexDirection: 'column', gap: '15px', flex: 1 }}>
                    <Link to="/admin" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>
                        📈 Statystyki i Wyniki
                    </Link>
                    <Link to="/admin/classrooms" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>
                        🏫 Zarządzaj Klasami
                    </Link>
                    <Link to="/admin/exams" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>
                        📝 Kreator Egzaminów
                    </Link>

                    <hr style={{ width: '100%', border: '0.5px solid rgba(255,255,255,0.2)', margin: '10px 0' }} />

                    <Link to="/profile" style={{ color: '#f1c40f', textDecoration: 'none', fontWeight: 'bold' }}>
                        👤 Mój Profil
                    </Link>
                </nav>

                {/* Przycisk wylogowania na dole, tak samo jak u studenta */}
                <button
                    onClick={handleLogout}
                    style={{
                        padding: '10px',
                        background: '#e74c3c',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        marginTop: 'auto'
                    }}
                >
                    Wyloguj się
                </button>
            </aside>

            {/* Główna treść panelu admina */}
            <main style={{ flex: 1, padding: '30px', background: '#0e4365' }}>
                <Outlet />
            </main>
        </div>
    );
};

export default AdminLayout;