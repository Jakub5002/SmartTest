package com.example.backend.controller;

import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.User;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/exam-sessions")
public class ExamSessionController {

    private final ExamSessionRepository examSessionRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    public ExamSessionController(ExamSessionRepository examSessionRepository, ExamRepository examRepository, UserRepository userRepository) {
        this.examSessionRepository = examSessionRepository;
        this.examRepository = examRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/start/{examId}")
    public ResponseEntity<?> startExam(@PathVariable UUID examId, @RequestParam UUID userId) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return ResponseEntity.notFound().build();

        if (!exam.getActive()) {
            return ResponseEntity.badRequest().body("Egzamin nie jest aktywny");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        Optional<ExamSession> existing = examSessionRepository.findByExamIdAndUserId(examId, userId);
        if (existing.isPresent()) return ResponseEntity.badRequest().body("Sesja już istnieje");

        ExamSession session = new ExamSession();
        session.setExam(exam);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now());
        session.setSubmitted(false);

        return ResponseEntity.ok(examSessionRepository.save(session));
    }
}