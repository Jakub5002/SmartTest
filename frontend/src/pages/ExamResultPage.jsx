import { useLocation, useNavigate } from 'react-router-dom';

function ExamResultPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const result = location.state?.result;
    const alreadyDone = location.state?.alreadyDone;

    if (!result) {
        return (
            <div style={{ color: 'white', padding: '20px', textAlign: 'center' }}>
                <p>Brak wyników.</p>
                <button onClick={() => navigate('/')}
                        style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>
                    Wróć
                </button>
            </div>
        );
    }

    const { score, totalScore, percentage } = result;

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
                    {percentage.toFixed(1)}%
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
                    onClick={() => navigate('/')}
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