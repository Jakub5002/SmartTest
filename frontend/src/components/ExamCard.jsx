import { useNavigate } from 'react-router-dom'; // Tego brakowało!

function ExamCard({ exam }) {
    const navigate = useNavigate();

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
                onClick={() => navigate(`/exam/${exam.id}`)}
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