import React, { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const RegisterPage = () => {
    const[email, setEmail] = useState('');
    const[password, setPassword] = useState('');
    const [isAdmin, setIsAdmin] = useState(false);
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await api.post('/auth/register', {
                email,
                password,
                admin: isAdmin
            });
            alert("Konto założone!");
            navigate('/login');
        } catch(err) { /* ... */ }
    };

    return(
        <div style={{ padding: '20px' }}>
            <h2>Rejestracja w SmartTest</h2>
            <form onSubmit={handleRegister}>
                <input type="email" placeholder="Email" onChange={e => setEmail(e.target.value)} required/><br/>
                <input type="password" placeholder="Hasło" onChange={e => setPassword(e.target.value)} required/><br/>
                <select onChange={e => setIsAdmin(e.target.value === "true")} value={isAdmin}>
                    <option value="false">Student</option>
                    <option value="true">Admin</option>
                </select><br/>
                <button type="submit">Zarejestuj się!</button>
            </form>
        </div>
    );
};
export default RegisterPage