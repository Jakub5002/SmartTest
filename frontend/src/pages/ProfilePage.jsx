import React, { useState } from 'react';
import api from '../api/axios';

const ProfilePage = () => {
    const [email, setEmail] = useState('');
    const [oldPassword, setOldPassword] = useState(''); // DODANE
    const [password, setPassword] = useState('');    // To będzie nasze newPassword
    const [message, setMessage] = useState('');

    const handleUpdateEmail = async (e) => {
        e.preventDefault();
        try {
            await api.put('/user/update-email', { newEmail: email });
            setMessage("Email został pomyślnie zaktualizowany!");
        } catch (err) {
            const errorData = err.response?.data;
            const errorMessage = typeof errorData === 'object'
                ? (errorData.message || JSON.stringify(errorData))
                : (errorData || "Wystąpił błąd");

            setMessage(errorMessage);
        }
    };

    const handleUpdatePassword = async (e) => {
        e.preventDefault();
        try {
            await api.put('/user/update-password', {
                oldPassword: oldPassword,
                newPassword: password
            });
            setMessage("Hasło zostało pomyślnie zmienione!");
            setOldPassword(''); // Czyścimy pola po sukcesie
            setPassword('');
        } catch (err) {
            setMessage(err.response?.data || "Błąd podczas zmiany hasła.");
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto' }}>
            <h2>Edycja profilu</h2>
            {message && <p style={{
                padding: '10px',
                backgroundColor: '#d1ecf1',
                color: '#0c5460',
                borderRadius: '5px'
            }}>{message}</p>}

            {/* Sekcja zmiany emaila */}
            <form onSubmit={handleUpdateEmail} style={{ marginBottom: '30px', borderBottom: '1px solid #ccc', paddingBottom: '20px' }}>
                <h3>Zmień Email</h3>
                <input
                    type="email"
                    placeholder="Nowy email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', marginBottom: '10px' }}
                />
                <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Aktualizuj Email
                </button>
            </form>

            {/* Sekcja zmiany hasła */}
            <form onSubmit={handleUpdatePassword}>
                <h3>Zmień Hasło</h3>
                <input
                    type="password"
                    placeholder="Stare hasło"
                    value={oldPassword}
                    onChange={e => setOldPassword(e.target.value)} // TERAZ ZADZIAŁA
                    required
                    style={{ width: '100%', padding: '8px', marginBottom: '10px' }}
                />
                <input
                    type="password"
                    placeholder="Nowe hasło"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', marginBottom: '10px' }}
                />
                <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#3498db', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Zmień Hasło
                </button>
            </form>
        </div>
    );
};

export default ProfilePage;