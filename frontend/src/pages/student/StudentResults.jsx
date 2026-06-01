import React, { useState, useEffect } from 'react';

const StudentResults = () => {
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchMyResults = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/exams/my/results', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                });
                if (!response.ok) throw new Error("Błąd pobierania danych");
                const data = await response.json();
                setResults(Array.isArray(data) ? data : []);
                setLoading(false);
            } catch (err) {
                console.error("Błąd:", err);
                setError("Nie udało się załadować Twoich wyników.");
                setLoading(false);
            }
        };

        fetchMyResults();
    }, []);

    if (loading) return <div style={{ color: 'white', padding: '20px' }}>Ładowanie Twoich wyników...</div>;

    return (
        <div style={{ color: 'white', padding: '20px', maxWidth: '1000px', margin: '0 auto' }}>
            <h2 style={{ marginBottom: '10px' }}>🏆 Twoje Wyniki Egzaminów</h2>
            <p style={{ color: '#aaa', marginBottom: '20px' }}>Poniżej znajduje się historia Twoich zaliczonych podejść do testów.</p>

            {error && <p style={{ color: '#e74c3c', fontWeight: 'bold' }}>{error}</p>}

            {results.length === 0 ? (
                <div style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px' }}>
                    <p>📭 Nie masz jeszcze żadnych zapisanych wyników.</p>
                </div>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse', background: '#2c3e50', borderRadius: '10px', overflow: 'hidden' }}>
                    <thead>
                    <tr style={{ background: '#34495e', textAlign: 'left' }}>
                        <th style={{ padding: '15px' }}>Nazwa Egzaminu</th>
                        <th style={{ padding: '15px' }}>Uzyskany Wynik</th>
                        <th style={{ padding: '15px' }}>Procentowo</th>
                        <th style={{ padding: '15px' }}>Data Zakończenia</th>
                    </tr>
                    </thead>
                    <tbody>
                    {results.map((res) => {
                        const percent = res.maxPoints > 0 ? Math.round((res.score / res.maxPoints) * 100) : 0;
                        return (
                            <tr key={res.id} style={{ borderBottom: '1px solid #444' }}>
                                <td style={{ padding: '15px', fontWeight: 'bold' }}>{res.examTitle}</td>
                                <td style={{ padding: '15px', color: '#2ecc71', fontSize: '1.1rem', fontWeight: 'bold' }}>
                                    {res.score} / {res.maxPoints} pkt
                                </td>
                                <td style={{ padding: '15px' }}>
                                        <span style={{
                                            background: percent >= 50 ? '#2ecc71' : '#e74c3c',
                                            padding: '4px 10px',
                                            borderRadius: '6px',
                                            fontWeight: 'bold',
                                            color: '#fff'
                                        }}>
                                            {percent}%
                                        </span>
                                </td>
                                <td style={{ padding: '15px', color: '#bdc3c7' }}>{res.finishedAt}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default StudentResults;