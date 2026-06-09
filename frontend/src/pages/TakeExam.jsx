import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function TakeExam() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [exam, setExam] = useState(null);
    const [loading, setLoading] = useState(true);
    const [sessionError, setSessionError] = useState(null);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [timeLeft, setTimeLeft] = useState(null);

    const handleSubmit = useCallback(() => {
        const submission = {
            answers: Object.entries(answers).map(([questionId, selectedOption]) => ({
                questionId,
                selectedOption
            }))
        };

        fetch(`http://localhost:8080/api/exams/${id}/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem("token")}`
            },
            body: JSON.stringify(submission)
        })
            .then(res => {
                if (!res.ok) return res.text().then(text => { throw new Error(text) });
                return res.json();
            })
            .then(result => {
                navigate('/result', { state: { result, examId: id } });
            })
            .catch(() => {
                navigate('/result', { state: { examId: id } });
            });
    }, [answers, id, navigate]);

    useEffect(() => {
        let cancelled = false;

        fetch(`http://localhost:8080/api/exam-sessions/start/${id}`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            }
        })
            .then(res => {
                if (cancelled) return null;
                if (res.status === 409) {
                    // 🔥 Jeśli student już pisał ten test, kierujemy na poprawne /student/results
                    navigate('/student/results', { state: { examId: id, alreadyDone: true } });
                    return null;
                }
                if (!res.ok) return res.text().then(text => { throw new Error(text) });
                return res.json();
            })
            .then(session => {
                if (!session || cancelled) return;
                const startedAt = new Date(session.startedAt);

                return fetch(`http://localhost:8080/api/exams/${id}`, {
                    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
                })
                    .then(res => res.json())
                    .then(examData => {
                        if (!examData || cancelled) return;

                        setExam(examData);
                        const endTime = new Date(startedAt.getTime() + examData.durationMinutes * 60 * 1000);
                        const remaining = Math.floor((endTime - new Date()) / 1000);

                        if (remaining <= 0) {
                            handleSubmit();
                            return;
                        }

                        setTimeLeft(remaining);
                        setLoading(false);
                    });
            })
            .catch(err => {
                if (!cancelled) {
                    setSessionError(err.message);
                    setLoading(false);
                }
            });

        return () => {
            cancelled = true;
        };
    }, [id, navigate, handleSubmit]);

    useEffect(() => {
        if (timeLeft === null) return;
        if (timeLeft <= 0) {
            handleSubmit();
            return;
        }
        const timer = setInterval(() => {
            setTimeLeft(prev => prev - 1);
        }, 1000);
        return () => clearInterval(timer);
    }, [timeLeft, handleSubmit]);

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
    };

    const handleAnswerSelect = (questionId, selectedOption) => {
        setAnswers(prev => ({ ...prev, [questionId]: selectedOption }));
    };

    const nextQuestion = () => {
        if (currentQuestionIndex < exam.questions.length - 1)
            setCurrentQuestionIndex(currentQuestionIndex + 1);
    };

    const prevQuestion = () => {
        if (currentQuestionIndex > 0)
            setCurrentQuestionIndex(currentQuestionIndex - 1);
    };

    if (sessionError) return (
        <div style={{ color: 'white', padding: '40px', textAlign: 'center' }}>
            <h2>⚠️ {sessionError}</h2>
            <button
                onClick={() => navigate('/student')}
                style={{ marginTop: '20px', padding: '10px 24px', borderRadius: '8px',
                    background: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}
            >
                Wróć do listy egzaminów
            </button>
        </div>
    );

    if (loading) return <div style={{ color: 'white', padding: '20px' }}>Ładowanie...</div>;
    if (!exam || !exam.questions || exam.questions.length === 0)
        return <div style={{ color: 'white' }}>Brak pytań.
            <button
                onClick={() => navigate('/student')}
                style={{ marginTop: '20px', padding: '10px 24px', borderRadius: '8px',
                    background: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}
            >
                Wróć do listy egzaminów
            </button>
    </div>;

    const currentQuestion = exam.questions[currentQuestionIndex];

    return (
        <div style={{ padding: '20px', color: 'white', maxWidth: '600px', margin: '0 auto', fontFamily: 'Arial, sans-serif' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '30px' }}>{exam.title}</h1>

            {/* LICZNIK */}
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '30px' }}>
                <div style={{
                    background: timeLeft < 60 ? '#cc0000' : '#222',
                    padding: '8px 25px', borderRadius: '20px',
                    border: `2px solid ${timeLeft < 60 ? '#ff4d4d' : '#444'}`,
                    fontSize: '1.4rem', fontWeight: 'bold', fontFamily: 'monospace'
                }}>
                    <span style={{ fontSize: '0.9rem', marginRight: '10px', color: '#aaa' }}>POZOSTAŁO:</span>
                    {formatTime(timeLeft)}
                </div>
            </div>

            {/* PYTANIE */}
            <div style={{ background: '#333', padding: '25px', borderRadius: '15px' }}>
                <div style={{ color: '#aaa', marginBottom: '15px' }}>
                    Pytanie {currentQuestionIndex + 1} z {exam.questions.length}
                </div>
                <h3 style={{ fontSize: '1.4rem', marginBottom: '25px' }}>{currentQuestion.content}</h3>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {currentQuestion.options.map((optionText, index) => {
                        const letter = String.fromCharCode(65 + index);
                        return (
                            <label key={index} style={{
                                padding: '15px',
                                background: answers[currentQuestion.id] === optionText ? '#007bff' : '#444',
                                borderRadius: '10px', cursor: 'pointer',
                                display: 'flex', alignItems: 'center'
                            }}>
                                <input
                                    type="radio"
                                    name={`question-${currentQuestion.id}`}
                                    checked={answers[currentQuestion.id] === optionText}
                                    onChange={() => handleAnswerSelect(currentQuestion.id, optionText)}
                                    style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                                />
                                <span style={{ marginLeft: '15px', fontSize: '1.1rem' }}>
                                    {letter}. {optionText}
                                </span>
                            </label>
                        );
                    })}
                </div>
            </div>

            {/* NAWIGACJA */}
            <div style={{ marginTop: '30px', display: 'flex', justifyContent: 'space-between' }}>
                <button onClick={prevQuestion} disabled={currentQuestionIndex === 0}
                        style={{ padding: '12px 24px', borderRadius: '8px',
                            cursor: currentQuestionIndex === 0 ? 'not-allowed' : 'pointer',
                            background: '#555', color: 'white', border: 'none' }}>
                    Poprzednie
                </button>

                {currentQuestionIndex < exam.questions.length - 1 ? (
                    <button onClick={nextQuestion}
                            style={{ padding: '12px 24px', borderRadius: '8px',
                                background: '#007bff', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>
                        Następne
                    </button>
                ) : (
                    <button onClick={handleSubmit}
                            style={{ padding: '12px 30px', borderRadius: '8px',
                                background: '#28a745', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>
                        Zakończ egzamin
                    </button>
                )}
            </div>
        </div>
    );
}

export default TakeExam;