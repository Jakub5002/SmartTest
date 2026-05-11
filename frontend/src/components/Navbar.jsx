import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={{
            display: 'flex',
            justifyContent: 'space-between',
            padding: '10px 20px',
            background: '#2c3e50',
            color: 'white',
            alignItems: 'center'
        }}>
            <div>
                <Link to="/" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>SmartTest</Link>
            </div>
            <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
                <span>{user?.email} ({user?.role})</span>
                <Link to="/profile" style={{ color: 'white', textDecoration: 'none' }}>Edytuj profil</Link>
                {/*  warunkowe linki */}
                {user?.role === 'ROLE_ADMIN' && <Link to="/admin" style={{ color: 'white' }}>Panel Admina</Link>}
                {user?.role === 'ROLE_STUDENT' && <Link to="/student" style={{ color: 'white' }}>Moje Egzaminy</Link>}

                <button onClick={handleLogout} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '4px' }}>
                    Wyloguj
                </button>
            </div>
        </nav>
    );
};

export default Navbar;