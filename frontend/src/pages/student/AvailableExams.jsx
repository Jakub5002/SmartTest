import { useState, useEffect } from 'react';
import ExamCard from '../../components/ExamCard';
import { getUserId } from "../../auth/HelperGetIdUser.jsx";


function AvaiableExams() {
    const [egzaminy, setEgzaminy] = useState([]); // Inicjalizacja pustą tablicą

    useEffect(() => {
        const token = localStorage.getItem("token");

        console.log("TOKEN:", token);

        fetch("http://localhost:8080/api/exams/my", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(async res => {
                console.log("STATUS:", res.status);

                const text = await res.text();
                console.log("RESPONSE:", text);

                if (!res.ok) {
                    throw new Error("Błąd serwera: " + res.status);
                }

                return JSON.parse(text);
            })
            .then(data => {
                setEgzaminy(data);
            })
            .catch(err => {
                console.error(err);
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

export default AvaiableExams;