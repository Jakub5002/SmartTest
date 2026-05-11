import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function TakeExam() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [exam, setExam] = useState(null);
    const [loading, setLoading] = useState(true);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [answers, setAnswers] = useState({});

    // NOWE: Stan dla pozostałego czasu (w sekundach)
    const [timeLeft, setTimeLeft] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/api/exams/${id}`)
            .then(res => res.json())
            .then(data => {
                setExam(data);
                // Ustawiamy czas na starcie (minuty z bazy * 60 sekund)
                setTimeLeft(data.durationMinutes * 60);
                setLoading(false);
            })
            .catch(err => {
                console.error("Błąd pobierania:", err);
                setLoading(false);
            });
    }, [id]);

    // NOWE: Licznik czasu
    useEffect(() => {
        if (timeLeft === null || timeLeft <= 0) {
            if (timeLeft === 0) handleSubmit(); // Automatyczny wysyłka po czasie
            return;
        }

        const timer = setInterval(() => {
            setTimeLeft(prev => prev - 1);
        }, 1000);

        return () => clearInterval(timer); // Czyszczenie timera przy wyjściu z komponentu
    }, [timeLeft]);

    // Funkcja pomocnicza do formatowania czasu (sekundy -> MM:SS)
    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
    };

    const handleAnswerSelect = (questionId, answerId) => {
        setAnswers({ ...answers, [questionId]: answerId });
    };

    const nextQuestion = () => {
        if (currentQuestionIndex < exam.questions.length - 1) {
            setCurrentQuestionIndex(currentQuestionIndex + 1);
        }
    };

    const prevQuestion = () => {
        if (currentQuestionIndex > 0) {
            setCurrentQuestionIndex(currentQuestionIndex - 1);
        }
    };

    const handleSubmit = () => {
        console.log("Wysyłam odpowiedzi:", answers);
        fetch(`http://localhost:8080/api/exams/${id}/submit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ answers: answers })
        }).then(res => {
            if(res.ok) {
                alert("Czas minął lub test zakończony! Egzamin wysłany.");
                navigate("/");
            }
        });
    };

    if (loading) return <div style={{color: 'white'}}>Ładowanie...</div>;
    if (!exam || !exam.questions || exam.questions.length === 0) return <div style={{color: 'white'}}>Brak pytań.</div>;

    const currentQuestion = exam.questions[currentQuestionIndex];

    return (
        <div style={{ padding: '20px', color: 'white', maxWidth: '600px', margin: '0 auto', fontFamily: 'Arial, sans-serif' }}>

            {/* TYTUŁ EGZAMINU - Duży i na środku */}
            <h1 style={{ textAlign: 'center', marginBottom: '30px', color: '#fff' }}>
                {exam.title}
            </h1>

            {/* OSOBNY LICZNIK CZASU - Wyśrodkowany pasek pod tytułem */}
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                marginBottom: '30px'
            }}>
                <div style={{
                    background: timeLeft < 60 ? '#cc0000' : '#222',
                    padding: '8px 25px',
                    borderRadius: '20px',
                    border: '2px solid',
                    borderColor: timeLeft < 60 ? '#ff4d4d' : '#444',
                    fontSize: '1.4rem',
                    fontWeight: 'bold',
                    fontFamily: 'monospace',
                    boxShadow: '0 4px 15px rgba(0,0,0,0.5)',
                    transition: 'all 0.3s ease'
                }}>
                <span style={{ fontSize: '0.9rem', marginRight: '10px', verticalAlign: 'middle', color: '#aaa' }}>
                    POZOSTAŁO:
                </span>
                    {formatTime(timeLeft)}
                </div>
            </div>

            {/* KONTENER PYTANIA */}
            <div style={{ background: '#333', padding: '25px', borderRadius: '15px', boxShadow: '0 4px 10px rgba(0,0,0,0.3)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', color: '#aaa', marginBottom: '15px' }}>
                    <span>Pytanie {currentQuestionIndex + 1} z {exam.questions.length}</span>
                </div>

                <h3 style={{ fontSize: '1.4rem', lineHeight: '1.4', marginBottom: '25px' }}>
                    {currentQuestion.content}
                </h3>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {currentQuestion.options.map((optionText, index) => (
                        <label key={index} style={{
                            padding: '15px',
                            background: answers[currentQuestion.id] === optionText ? '#007bff' : '#444',
                            borderRadius: '10px',
                            cursor: 'pointer',
                            transition: 'background 0.2s',
                            display: 'flex',
                            alignItems: 'center'
                        }}>
                            <input
                                type="radio"
                                name={`question-${currentQuestion.id}`}
                                checked={answers[currentQuestion.id] === optionText}
                                onChange={() => handleAnswerSelect(currentQuestion.id, optionText)}
                                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                            />
                            <span style={{ marginLeft: '15px', fontSize: '1.1rem' }}>{optionText}</span>
                        </label>
                    ))}
                </div>
            </div>

            {/* PRZYCISKI NAWIGACJI */}
            <div style={{ marginTop: '30px', display: 'flex', justifyContent: 'space-between' }}>
                <button
                    onClick={prevQuestion}
                    disabled={currentQuestionIndex === 0}
                    style={{
                        padding: '12px 24px',
                        borderRadius: '8px',
                        cursor: currentQuestionIndex === 0 ? 'not-allowed' : 'pointer',
                        background: '#555',
                        color: 'white',
                        border: 'none'
                    }}
                >
                    Poprzednie
                </button>

                {currentQuestionIndex < exam.questions.length - 1 ? (
                    <button
                        onClick={nextQuestion}
                        style={{
                            padding: '12px 24px',
                            borderRadius: '8px',
                            cursor: 'pointer',
                            background: '#007bff',
                            color: 'white',
                            border: 'none',
                            fontWeight: 'bold'
                        }}
                    >
                        Następne
                    </button>
                ) : (
                    <button
                        onClick={handleSubmit}
                        style={{
                            padding: '12px 30px',
                            borderRadius: '8px',
                            cursor: 'pointer',
                            background: '#28a745',
                            color: 'white',
                            border: 'none',
                            fontWeight: 'bold',
                            boxShadow: '0 0 10px rgba(40, 167, 69, 0.4)'
                        }}
                    >
                        Zakończ egzamin
                    </button>
                )}
            </div>
        </div>
    );
}

export default TakeExam;