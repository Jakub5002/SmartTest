import React, { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const RegisterPage = () => {
    const[email, setEmail] = useState('');
    const[password, setPassword] = useState('');
    const[role, setRole] = useState('STUDENT')
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try{
            await api.post('/auth/register', { email, password, role});
            alert("Konto zalozone, mozesz sie zalogowac");
            navigate('/login');
        }catch(err){
            alert("Bład rejestracji: " + (err.response?.data?.message || "Sproboj ponownie"));
        }
    };

    return(
        <div style={{ padding: '20px' }}>
            <h2>Rejestracja w SmartTest</h2>
            <form onSubmit={handleRegister}>
                <input type="email" placeholder="Email" onChange={e => setEmail(e.target.value)} required/><br/>
                <input type="password" placeholder="Hasło" onChange={e => setPassword(e.target.value)} required/><br/>
                <select onChange={e=>setRole(e.target.value)} value={role}>
                    <option value="STUDENT">Student</option>
                    <option value="ADMIN">Admin</option>
                </select><br/>
                <button type="submit">Zarejestuj się!</button>
            </form>
        </div>
    );
};
export default RegisterPage