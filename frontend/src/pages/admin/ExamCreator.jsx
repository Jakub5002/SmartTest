import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { getUserId } from '../../auth/HelperGetIdUser.jsx'; //

const ExamCreator = () => {
    const [classrooms, setClassrooms] = useState([]);
    const [classroomId, setClassroomId] = useState('');
    const [title, setTitle] = useState('');
    const [durationMinutes, setDurationMinutes] = useState(30);
    const [isActive, setIsActive] = useState(true);
    const [message, setMessage] = useState('');

    // Dynamiczna lista pytań - każde domyślnie posiada treść, opcje, poprawną odpowiedź i domyślne 1 pkt
    const [questions, setQuestions] = useState([
        { content: '', options: ['', '', '', ''], correctOption: '', points: 1 }
    ]);

    // Pobranie klas dla listy wyboru
    useEffect(() => {
        api.get('/admin/classrooms') //
            .then(res => setClassrooms(Array.isArray(res.data) ? res.data : [])) //
            .catch(err => console.error("Błąd pobierania klas:", err));
    }, []);

    const addQuestionField = () => {
        setQuestions([...questions, { content: '', options: ['', '', '', ''], correctOption: '', points: 1 }]);
    };

    const removeQuestionField = (index) => {
        setQuestions(questions.filter((_, i) => i !== index));
    };

    const handleQuestionContentChange = (index, value) => {
        const updated = [...questions];
        updated[index].content = value;
        setQuestions(updated);
    };

    // Zmiana wartości punktów dla danego pytania
    const handlePointsChange = (index, value) => {
        const updated = [...questions];
        updated[index].points = parseInt(value) || 1;
        setQuestions(updated);
    };

    const handleOptionChange = (qIndex, oIndex, value) => {
        const updated = [...questions];
        updated[qIndex].options[oIndex] = value;
        if (updated[qIndex].correctOption === updated[qIndex].options[oIndex]) {
            updated[qIndex].correctOption = value;
        }
        setQuestions(updated);
    };

    const handleCorrectOptionChange = (qIndex, value) => {
        const updated = [...questions];
        updated[qIndex].correctOption = value;
        setQuestions(updated);
    };

    // PROCES SEKWENCYJNEGO ZAPISU CAŁEGO EGZAMINU
    const handleSubmitExam = async (e) => {
        e.preventDefault();
        setMessage('');

        if (!classroomId) {
            setMessage("❌ Wybierz klasę dla tego egzaminu!");
            return;
        }

        const hasMissingCorrect = questions.some(q => !q.correctOption);
        if (hasMissingCorrect) {
            setMessage("❌ Każde pytanie musi mieć wybraną poprawną odpowiedź (użyj kropki obok opcji)!");
            return;
        }

        const adminId = getUserId(); // Pobieramy UUID zalogowanego admina
        if (!adminId) {
            setMessage("❌ Błąd autoryzacji: nie znaleziono ID użytkownika. Zaloguj się ponownie.");
            return;
        }

        const examPayload = {
            title: title,
            durationMinutes: parseInt(durationMinutes),
            createdBy: adminId,

            isActive: isActive,
            active: isActive,
            is_active: isActive
        };

        try {
            const examResponse = await api.post('/exams', examPayload); //
            const createdExam = examResponse.data;
            const examId = createdExam.id; // Uzyskujemy UUID nowo dodanego egzaminu

            await api.post(`/admin/classrooms/${classroomId}/exams/${examId}`); //

            const questionPromises = questions.map(q => {
                const questionPayload = {
                    examId: examId,             // Łączymy pytanie z nowym egzaminem
                    content: q.content,         // Treść pytania
                    options: q.options,         // Tablica String[] odpowiedzi
                    correctOption: q.correctOption, // Poprawny tekst
                    points: q.points            // Liczba punktów za zadanie
                };
                return api.post('/questions', questionPayload); // POST pod /api/questions
            });

            await Promise.all(questionPromises);

            setMessage(`🏆 Egzamin "${title}" wraz z zestawem pytań został opublikowany pomyślnie!`);

            setTitle('');
            setDurationMinutes(30);
            setQuestions([{ content: '', options: ['', '', '', ''], correctOption: '', points: 1 }]);
            setClassroomId('');
        } catch (err) {
            const errorData = err.response?.data;
            const errorMessage = typeof errorData === 'object' ? (errorData.message || JSON.stringify(errorData)) : (errorData || err.message);
            setMessage("❌ Błąd zapisu struktury danych: " + errorMessage);
        }
    };

    return (
        <div style={{ color: 'white', maxWidth: '700px', margin: '0 auto' }}>
            <h2>📝 Kreator Nowego Egzaminu</h2>
            {message && <p style={{ background: '#34495e', padding: '12px', borderRadius: '5px', fontWeight: 'bold' }}>{message}</p>}

            <form onSubmit={handleSubmitExam}>
                {/* METADANE EGZAMINU */}
                <div style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
                    <h3>1. Informacje ogólne</h3>

                    <label>Tytuł Egzaminu:</label>
                    <input
                        type="text"
                        placeholder="Np. Kolokwium z Podstaw Javy"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        required
                        style={{ width: '100%', padding: '10px', marginBottom: '15px', borderRadius: '5px', border: 'none' }}
                    />

                    <div style={{ display: 'flex', gap: '20px' }}>
                        <div style={{ flex: 1 }}>
                            <label>Czas trwania (minuty):</label>
                            <input
                                type="number"
                                value={durationMinutes}
                                onChange={e => setDurationMinutes(e.target.value)}
                                required
                                min="1"
                                style={{ width: '100%', padding: '10px', borderRadius: '5px', border: 'none' }}
                            />
                        </div>
                        <div style={{ flex: 1 }}>
                            <label>Przypisz do klasy:</label>
                            <select
                                value={classroomId}
                                onChange={e => setClassroomId(e.target.value)}
                                required
                                style={{ width: '100%', padding: '10px', borderRadius: '5px', height: '38px', color: 'black' }}
                            >
                                <option value="">-- Wybierz klasę --</option>
                                {classrooms.map(cls => (
                                    <option key={cls.id} value={cls.id}>{cls.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>

                {/* MODUŁ DYNAMICZNEGO DODAWANIA PYTAŃ */}
                <div style={{ background: '#2c3e50', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
                    <h3>2. Pytania egzaminacyjne</h3>

                    {questions.map((q, qIndex) => (
                        <div key={qIndex} style={{ background: '#34495e', padding: '15px', borderRadius: '8px', marginBottom: '15px', border: '1px solid #555' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                                <h4>Pytanie #{qIndex + 1}</h4>
                                {questions.length > 1 && (
                                    <button type="button" onClick={() => removeQuestionField(qIndex)} style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer' }}>
                                        Usuń pytanie
                                    </button>
                                )}
                            </div>

                            <input
                                type="text"
                                placeholder="Treść pytania..."
                                value={q.content}
                                onChange={e => handleQuestionContentChange(qIndex, e.target.value)}
                                required
                                style={{ width: '100%', padding: '10px', marginBottom: '10px', borderRadius: '5px', border: 'none' }}
                            />

                            <div style={{ marginBottom: '15px' }}>
                                <label style={{ marginRight: '10px' }}>Punkty za pytanie:</label>
                                <input
                                    type="number"
                                    min="1"
                                    value={q.points}
                                    onChange={e => handlePointsChange(qIndex, e.target.value)}
                                    style={{ width: '70px', padding: '5px', borderRadius: '4px', border: 'none' }}
                                />
                            </div>

                            <h5>Opcje odpowiedzi (zaznacz kropką poprawną):</h5>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                {q.options.map((opt, oIndex) => {
                                    const letter = String.fromCharCode(65 + oIndex); // A, B, C, D
                                    return (
                                        <div key={oIndex} style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                            <input
                                                type="radio"
                                                name={`correct-for-q-${qIndex}`}
                                                checked={q.correctOption === opt && opt !== ''}
                                                onChange={() => handleCorrectOptionChange(qIndex, opt)}
                                                disabled={!opt}
                                                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                                            />
                                            <span style={{ fontWeight: 'bold' }}>{letter}:</span>
                                            <input
                                                type="text"
                                                placeholder={`Odpowiedź ${letter}`}
                                                value={opt}
                                                onChange={e => handleOptionChange(qIndex, oIndex, e.target.value)}
                                                required
                                                style={{ flex: 1, padding: '8px', borderRadius: '5px', border: 'none' }}
                                            />
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    ))}

                    <button
                        type="button"
                        onClick={addQuestionField}
                        style={{ width: '100%', padding: '10px', background: '#3498db', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}
                    >
                        ➕ Dodaj kolejne pytanie
                    </button>
                </div>

                <button
                    type="submit"
                    style={{ width: '100%', padding: '15px', background: '#2ecc71', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '1.1rem', fontWeight: 'bold' }}
                >
                    💾 Stwórz i Opublikuj Egzamin
                </button>
            </form>
        </div>
    );
};

export default ExamCreator;