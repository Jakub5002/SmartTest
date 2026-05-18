import { useState, useEffect } from 'react';
import ExamCard from '../components/ExamCard';

function AllExams() {
    const [egzaminy, setEgzaminy] = useState([]); // Inicjalizacja pustą tablicą

    useEffect(() => {
        fetch("http://localhost:8080/api/exams")
            .then(res => {
                if (!res.ok) throw new Error("Błąd serwera: " + res.status);
                return res.json();
            })
            .then(data => {
                // Sprawdzamy czy to co przyszło jest na pewno tablicą
                if (Array.isArray(data)) {
                    setEgzaminy(data);
                } else {
                    console.error("Dane nie są tablicą:", data);
                    setEgzaminy([]);
                }
            })
            .catch(err => {
                console.error("Błąd pobierania:", err);
                setEgzaminy([]); // W razie błędu czyścimy listę
            });
    }, []);

    return (
        <div style={{ color: 'white', padding: '20px' }}>
            <h1>Lista Egzaminów</h1>
            {/* Bezpieczne mapowanie */}
            {egzaminy && egzaminy.length > 0 ? (
                egzaminy.map(e => <ExamCard key={e.id} exam={e} />)
            ) : (
                <p>Brak dostępnych egzaminów lub błąd połączenia.</p>
            )}
        </div>
    );
}

export default AllExams;