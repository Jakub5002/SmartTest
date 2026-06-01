import React, { useState, useEffect } from 'react';
import api from '../../api/axios';

const AdminStats = () => {
    const [stats, setStats] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await api.get('/exams/admin/stats');
                setStats(Array.isArray(response.data) ? response.data : []);
                setLoading(false);
            } catch (err) {
                console.error("Błąd pobierania statystyk:", err);
                setError("Nie udało się załadować statystyk z serwera.");
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    if (loading) return <div style={{ color: 'white', padding: '20px' }}>Ładowanie statystyk i wyników...</div>;

    return (
        <div style={{ color: 'white', padding: '10px' }}>
            <h2>📊 Panel Statystyk i Wyników Uczniów</h2>
            <p>Poniższa tabela przedstawia oficjalne, przeliczone przez system oceny przesłane przez studentów.</p>

            {error && <p style={{ color: '#e74c3c', fontWeight: 'bold' }}>{error}</p>}

            {stats.length === 0 ? (
                <div style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px', marginTop: '20px' }}>
                    <p>📭 Brak zatwierdzonych wyników egzaminów w bazie danych.</p>
                </div>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px', background: '#2c3e50', borderRadius: '10px', overflow: 'hidden' }}>
                    <thead>
                    <tr style={{ background: '#34495e', textAlign: 'left' }}>
                        <th style={{ padding: '12px' }}>Student (Email)</th>
                        <th style={{ padding: '12px' }}>Nazwa Egzaminu</th>
                        <th style={{ padding: '12px' }}>Uzyskany Wynik</th>
                        <th style={{ padding: '12px' }}>Data Zakończenia</th>
                    </tr>
                    </thead>
                    <tbody>
                    {stats.map((result) => (
                        <tr key={result.id} style={{ borderBottom: '1px solid #444' }}>
                            <td style={{ padding: '12px' }}>{result.studentEmail}</td>
                            <td style={{ padding: '12px' }}>{result.examTitle}</td>
                            <td style={{ padding: '12px', fontWeight: 'bold', color: '#2ecc71', fontSize: '1.1rem' }}>
                                {result.score}/{result.maxPoints} pkt
                            </td>
                            <td style={{ padding: '12px', color: '#bdc3c7' }}>
                                {result.finishedAt}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default AdminStats;