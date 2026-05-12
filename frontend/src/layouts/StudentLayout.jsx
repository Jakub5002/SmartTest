import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from "../context/AuthContext";

const StudentLayout = () => {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div style={{ display: 'flex', minHeight: '100vh' }}>
            <aside style={{
                width: '250px',
                background: '#27ae60',
                color: 'white',
                padding: '20px',
                display: 'flex',
                flexDirection: 'column'
            }}>
                <h2 style={{ marginBottom: '30px' }}>Portal Studenta</h2>

                <nav style={{ display: 'flex', flexDirection: 'column', gap: '15px', flex: 1 }}>
                    <Link to="/student" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>
                        📚 Dostępne Testy
                    </Link>
                    <Link to="/student/results" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>
                        📊 Moje Wyniki
                    </Link>

                    <hr style={{ width: '100%', border: '0.5px solid rgba(255,255,255,0.3)' }} />

                    <Link to="/profile" style={{ color: '#f1c40f', textDecoration: 'none', fontWeight: 'bold' }}>
                        ⚙️ Ustawienia Konta
                    </Link>
                </nav>

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

            <main style={{ flex: 1, padding: '30px', background: '#0e4365' }}>
                <Outlet />
            </main>
        </div>
    );
};

export default StudentLayout;