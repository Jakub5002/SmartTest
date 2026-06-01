import React, { useState, useEffect } from 'react';
import api from '../../api/axios';

const ClassroomManagement = () => {
    const [classrooms, setClassrooms] = useState([]);
    const [newClassName, setNewClassName] = useState('');
    const [selectedClassId, setSelectedClassId] = useState('');
    const [studentEmail, setStudentEmail] = useState(''); // Powrót do adresu e-mail
    const [message, setMessage] = useState('');

    // Pobierz wszystkie klasy
    const fetchClassrooms = async () => {
        try {
            // Teraz to zapytanie zadziała, ponieważ w kontrolerze zdefiniowaliśmy @GetMapping
            const response = await api.get('/admin/classrooms');
            setClassrooms(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error("Błąd pobierania klas:", err);
        }
    };

    useEffect(() => {
        fetchClassrooms();
    }, []);

    // Tworzenie nowej klasy
    const handleCreateClassroom = async (e) => {
        e.preventDefault();
        try {
            await api.post('/admin/classrooms', newClassName, {
                headers: { 'Content-Type': 'text/plain' }
            });
            setMessage(`🏆 Pomyślnie utworzono klasę: ${newClassName}`);
            setNewClassName('');
            fetchClassrooms(); // Odświeżenie listy rozwijanej po sukcesie
        } catch (err) {
            const errorData = err.response?.data;
            const errorMessage = typeof errorData === 'object' ? (errorData.message || JSON.stringify(errorData)) : (errorData || "Wystąpił błąd");
            setMessage("❌ Błąd tworzenia klasy: " + errorMessage);
        }
    };

    // Dodawanie ucznia do klasy po adresie e-mail
    const handleAddStudent = async (e) => {
        e.preventDefault();
        if (!selectedClassId || !studentEmail) {
            setMessage("❌ Wybierz klasę i podaj adres email!");
            return;
        }
        try {
            // Adres URL: /api/admin/classrooms/{classId}/students?email={studentEmail}
            await api.post(`/admin/classrooms/${selectedClassId}/students`, null, {
                params: { email: studentEmail }
            });
            setMessage(`👤 Uczeń o e-mailu ${studentEmail} został pomyślnie dodany do klasy!`);
            setStudentEmail('');
        } catch (err) {
            const errorData = err.response?.data;
            const errorMessage = typeof errorData === 'object' ? (errorData.message || JSON.stringify(errorData)) : (errorData || "Wystąpił błąd");
            setMessage("❌ Błąd dodawania ucznia: " + errorMessage);
        }
    };

    return (
        <div style={{ color: 'white', maxWidth: '600px' }}>
            <h2>🏫 Zarządzanie Klasami (Classrooms)</h2>
            {message && <p style={{ background: '#34495e', padding: '10px', borderRadius: '5px', fontWeight: 'bold' }}>{message}</p>}

            {/* FORMULARZ 1: TWORZENIE KLASY */}
            <form onSubmit={handleCreateClassroom} style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
                <h3>Utwórz nową klasę</h3>
                <input
                    type="text"
                    placeholder="Np. Klasa 1A"
                    value={newClassName}
                    onChange={e => setNewClassName(e.target.value)}
                    required
                    style={{ width: '100%', padding: '10px', marginBottom: '10px', borderRadius: '5px', border: 'none' }}
                />
                <button type="submit" style={{ padding: '10px 20px', background: '#2ecc71', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                    Dodaj Klasę
                </button>
            </form>

            {/* FORMULARZ 2: DODAWANIE UCZNIA PO EMAILU */}
            <form onSubmit={handleAddStudent} style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px' }}>
                <h3>Dodaj ucznia do klasy</h3>

                <label>Wybierz klasę:</label>
                <select
                    value={selectedClassId}
                    onChange={e => setSelectedClassId(e.target.value)}
                    required
                    style={{ width: '100%', padding: '10px', marginBottom: '15px', borderRadius: '5px', color: 'black' }}
                >
                    <option value="">-- Wybierz klasę --</option>
                    {classrooms.map(cls => (
                        <option key={cls.id} value={cls.id}>{cls.name}</option>
                    ))}
                </select>

                <label>Email Ucznia:</label>
                <input
                    type="email"
                    placeholder="student@szkola.pl"
                    value={studentEmail}
                    onChange={e => setStudentEmail(e.target.value)}
                    required
                    style={{ width: '100%', padding: '10px', marginBottom: '15px', borderRadius: '5px', border: 'none' }}
                />
                <button type="submit" style={{ padding: '10px 20px', background: '#3498db', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                    Przypisz Ucznia do Klasy
                </button>
            </form>
        </div>
    );
};

export default ClassroomManagement;