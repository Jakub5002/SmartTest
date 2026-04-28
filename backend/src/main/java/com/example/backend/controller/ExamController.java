package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.Question;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.QuestionRepository;
import com.example.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    public String test() {
        return "Logika egzaminów na nowym branchu działa!";
    }

    @GetMapping
    public List<Exam> getAll() {
        return examRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Exam> addExam(@Valid @RequestBody ExamRequest request) {
        Exam exam = new Exam();
        exam.setTitle(request.title());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setActive(request.isActive());

        return ResponseEntity.ok(examRepository.save(exam));
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<?> submitExam(@PathVariable UUID examId, @RequestBody ExamSubmission submission) {
        // Sprawdź sesję
        ExamSession session = examSessionRepository.findByExamIdAndUserId(examId, submission.userId())
                .orElse(null);
        if (session == null) return ResponseEntity.badRequest().body("Nie rozpocząłeś egzaminu");
        if (session.isSubmitted()) return ResponseEntity.badRequest().body("Egzamin już został oddany");

        // Sprawdź czy czas nie minął
        int durationMinutes = session.getExam().getDurationMinutes();
        LocalDateTime endTime = session.getStartedAt().plusMinutes(durationMinutes);
        if (LocalDateTime.now().isAfter(endTime)) {
            session.setSubmitted(true);
            examSessionRepository.save(session);
            return ResponseEntity.badRequest().body("Czas egzaminu minął");
        }
        List<Question> questions = questionRepository.findByExamId(examId);
        if (questions.isEmpty()) return ResponseEntity.notFound().build();

        int score = 0;
        int totalScore = 0;
        for (Question dbQuestion : questions) {
            for (UserAnswer userAnswer : submission.answers()) {
                if (dbQuestion.getId().equals(userAnswer.questionId())) {
                    if (dbQuestion.getCorrectOption().equals(userAnswer.selectedOption())) {
                        score += dbQuestion.getPoints();
                    }
                }
            }
            totalScore += dbQuestion.getPoints();
        }

        // Oznacz jako submitted
        session.setSubmitted(true);
        examSessionRepository.save(session);

        double percentage = ((double) score / totalScore) * 100;
        return ResponseEntity.ok(new ExamResult(score, totalScore, percentage));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Exam> patchExam(@PathVariable UUID id, @RequestBody ExamRequest examDetails) {
        return examRepository.findById(id)
                .map(exam -> {
                    if (examDetails.title() != null) exam.setTitle(examDetails.title());
                    if (examDetails.durationMinutes() != null) exam.setDurationMinutes(examDetails.durationMinutes());
                    if (examDetails.isActive() != null) exam.setActive(examDetails.isActive());

                    Exam updated = examRepository.save(exam);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID id) {
        return examRepository.findById(id)
                .map(exam -> {
                    examRepository.delete(exam);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}