package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.Exam;
import com.example.backend.model.Question;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.QuestionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Exam> addExam(@Valid @RequestBody ExamRequest request) {
        Exam exam = new Exam();
        exam.setTitle(request.title());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setActive(request.isActive());

        return ResponseEntity.ok(examRepository.save(exam));
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<ExamResult> submitExam(@PathVariable java.util.UUID examId, @RequestBody ExamSubmission submission) {
        // Pobierz wszystkie poprawne pytania dla tego egzaminu z bazy
        List<Question> questions = questionRepository.findByExamId(examId);

        if (questions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        int score = 0;

        //Porównanie odpowiedzi z zaznaczonymi
        for (Question dbQuestion : questions) {
            for (UserAnswer userAnswer : submission.answers()) {
                if (dbQuestion.getId().equals(userAnswer.questionId())) {
                    if (dbQuestion.getCorrectOption().equals(userAnswer.selectedOption())) {
                        score++;
                    }
                }
            }
        }

        // Oblicz statystyki
        int totalQuestions = questions.size();
        double percentage = ((double) score / totalQuestions) * 100;

        return ResponseEntity.ok(new ExamResult(score, totalQuestions, percentage));
    }
}