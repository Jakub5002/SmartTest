import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AllExams from './pages/AllExams';
import TakeExam from './pages/TakeExam';

// TUTAJ NIE MOŻE BYĆ ŻADNYCH: const navigate = useNavigate() ANI useState!

function App() {
    // Hooks mogą być tylko TUTAJ (wewnątrz funkcji)
    return (
        <Router>
            <Routes>
                <Route path="/" element={<AllExams />} />
                <Route path="/exam/:id" element={<TakeExam />} />
            </Routes>
        </Router>
    );
}

export default App;