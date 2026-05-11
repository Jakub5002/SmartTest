import { useNavigate } from 'react-router-dom';

const HARDCODED_USER_ID = "5828c1b5-2ffd-41f6-bf32-0c77a2589458";

function ExamCard({ exam }) {
    const navigate = useNavigate();


    const handleStart = () => {
        fetch(`http://localhost:8080/api/exam-sessions/status/${exam.id}?userId=${HARDCODED_USER_ID}`)
            .then(res => res.json())
            .then(data => {
                if (data.status === "SUBMITTED") {
                    fetch(`http://localhost:8080/api/results/user/${HARDCODED_USER_ID}/exam/${exam.id}`)
                        .then(res => res.json())
                        .then(result => {
                            console.log("Wynik z backendu:", result);
                            const resultWithPercentage = {
                                ...result,
                                percentage: result.totalScore > 0
                                    ? (result.score / result.totalScore) * 100
                                    : 0
                            };
                            navigate('/result', { state: { result: resultWithPercentage, alreadyDone: true } });
                        });
                } else {
                    navigate(`/exam/${exam.id}`);
                }
            });
    };

    return (
        <div style={{
            border: '1px solid #444',
            padding: '15px',
            margin: '10px',
            borderRadius: '8px',
            background: '#333',
            color: 'white'
        }}>
            <h3>{exam.title}</h3>
            <p>Czas: {exam.durationMinutes} min</p>
            <button
                onClick={handleStart}
                style={{
                    background: '#007bff',
                    color: 'white',
                    border: 'none',
                    padding: '8px 16px',
                    borderRadius: '4px',
                    cursor: 'pointer'
                }}
            >
                Rozwiąż test
            </button>
        </div>
    );
}

export default ExamCard;