import React, { useState } from 'react';
import api from '../api/axios';

const ProfilePage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            // Zakładając, że masz taki endpoint w Springu
            await api.put('/users/profile', { email, password });
            setMessage("Dane zostały pomyślnie zaktualizowane!");
        } catch (err) {
            setMessage("Błąd podczas aktualizacji danych.");
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto' }}>
            <h2>Edycja profilu</h2>
            {message && <p>{message}</p>}
            <form onSubmit={handleUpdate}>
                <div style={{ marginBottom: '10px' }}>
                    <label>Nowy email:</label><br/>
                    <input type="email" value={email} onChange={e => setEmail(e.target.value)} style={{ width: '100%' }} />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>Nowe hasło:</label><br/>
                    <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={{ width: '100%' }} />
                </div>
                <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#3498db', color: 'white', border: 'none' }}>
                    Zapisz zmiany
                </button>
            </form>
        </div>
    );
};

export default ProfilePage;