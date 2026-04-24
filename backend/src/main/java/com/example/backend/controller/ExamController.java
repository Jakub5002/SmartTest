package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.Exam;
import com.example.backend.model.Question;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/test")
    public String test() {
        return "Logika egzaminów na nowym branchu działa!";
    }

    @GetMapping
    public List<Exam> getAll() {
        return examRepository.findAll();
    }

    @PostMapping
    public Exam addExam(@RequestBody Exam exam){
        return examRepository.save(exam);
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<ExamResult> submitExam(@PathVariable java.util.UUID examId, @RequestBody ExamSubmission submission) {
        // Pobierz wszystkie poprawne pytania dla tego egzaminu z bazy
        List<Question> questions = questionRepository.findByExamId(examId);

        if (questions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        int score = 0;

        // 2. Porównaj odpowiedzi studenta z tymi z bazy
        for (Question q : questions) {
            // Znajdź odpowiedź studenta dla konkretnego ID pytania
            submission.answers().stream()
                    .filter(answer -> answer.questionId().equals(q.getId()))
                    .findFirst()
                    .ifPresent(answer -> {
                        // Jeśli student udzielił odpowiedzi i jest ona poprawna - punkt!
                        if (q.getCorrectOption().equals(answer.selectedOption())) {
                            // Uwaga: wewnątrz lambdy musimy użyć triku,
                            // ale dla czytelności użyjemy zmiennej pomocniczej poza streamem
                            // lub zmienimy to na zwykłą pętlę.
                        }
                    });
        }

        // Wersja z klasycznymi pętlami (bezpieczniejsza i czytelniejsza dla początkujących):
        for (Question dbQuestion : questions) {
            for (UserAnswer userAnswer : submission.answers()) {
                if (dbQuestion.getId().equals(userAnswer.questionId())) {
                    if (dbQuestion.getCorrectOption().equals(userAnswer.selectedOption())) {
                        score++;
                    }
                }
            }
        }

        // 3. Oblicz statystyki
        int totalQuestions = questions.size();
        double percentage = ((double) score / totalQuestions) * 100;

        return ResponseEntity.ok(new ExamResult(score, totalQuestions, percentage));
    }
}