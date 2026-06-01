import { useLocation, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

function ExamResultPage() {
    const location = useLocation();
    const navigate = useNavigate();

    // Wyciągamy dane przekazane z TakeExam
    const rawResult = location.state?.result;
    const parsedResult = rawResult?.data ? rawResult.data : rawResult;

    // Inicjalizujemy stan bezpiecznymi, domyślnymi wartościami zamiast null
    const [result, setResult] = useState(parsedResult || { score: 0, totalScore: 0, percentage: 0 });
    const examId = location.state?.examId;
    const alreadyDone = location.state?.alreadyDone;

    useEffect(() => {
        // Jeśli nie dostaliśmy gotowego wyniku w state, ale mamy id egzaminu, dociągamy z bazy
        if (!parsedResult && examId) {
            fetch(`http://localhost:8080/api/exams/results/my/${examId}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                }
            })
                .then(res => {
                    if (!res.ok) throw new Error("Błąd odpowiedzi serwera");
                    return res.json();
                })
                .then(data => {
                    console.log("🔥 Dane dociągnięte awaryjnie z API:", data);
                    if (data && data.percentage !== undefined) {
                        setResult(data);
                    }
                })
                .catch(err => console.error("🛑 Błąd awaryjnego pobierania z API:", err));
        }
    }, [examId, parsedResult]);

    // 🔥 USUNĘLIŚMY BLOKADĘ "Błąd ładowania" - strona teraz zawsze się wyrenderuje!
    const score = result?.score ?? 0;
    const totalScore = result?.totalScore ?? 0;
    const percentage = result?.percentage ?? 0;

    const getColor = () => {
        if (percentage >= 90) return '#4CAF50';
        if (percentage >= 60) return '#FFC107';
        return '#f44336';
    };

    const getEmoji = () => {
        if (percentage >= 90) return '🏆';
        if (percentage >= 60) return '👍';
        return '📚';
    };

    return (
        <div style={{
            minHeight: '100vh',
            background: '#1a1a2e',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white'
        }}>
            <div style={{
                background: '#333',
                padding: '40px',
                borderRadius: '16px',
                textAlign: 'center',
                minWidth: '320px'
            }}>
                <div style={{ fontSize: '60px' , marginBottom: '30px'}}>{getEmoji()}</div>

                {alreadyDone && (
                    <h3 style={{ color: '#aaa', marginBottom: '20px' }}>
                        ✅ Ten egzamin już został przez Ciebie oddany
                    </h3>
                )}

                <h1>Wynik egzaminu</h1>

                <div style={{
                    width: '140px',
                    height: '140px',
                    borderRadius: '50%',
                    border: `8px solid ${getColor()}`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '20px auto',
                    fontSize: '32px',
                    fontWeight: 'bold',
                    color: getColor()
                }}>
                    {Number(percentage).toFixed(1)}%
                </div>

                <p style={{ fontSize: '20px' }}>
                    Zdobyte punkty: <strong>{score} / {totalScore}</strong>
                </p>

                <p style={{ color: getColor(), fontSize: '18px', fontWeight: 'bold' }}>
                    {percentage >= 90 ? 'Doskonały wynik!' :
                        percentage >= 60 ? 'Zaliczone!' :
                            'Nie zaliczone'}
                </p>

                <button
                    onClick={() => navigate('/student')}
                    style={{
                        marginTop: '20px',
                        background: '#007bff',
                        color: 'white',
                        border: 'none',
                        padding: '12px 28px',
                        borderRadius: '8px',
                        cursor: 'pointer',
                        fontSize: '16px'
                    }}
                >
                    Wróć do listy egzaminów
                </button>
            </div>
        </div>
    );
}

export default ExamResultPage;