package com.example.backend.controller;

import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.User;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
        // 1. Pobranie danych z elegancką obsługą błędów
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Egzamin nie istnieje"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie istnieje"));

        // 2. Walidacja biznesowa
        if (!exam.getActive()) {
            return ResponseEntity.badRequest().body("Egzamin jest obecnie nieaktywny.");
        }

        // 3. Sprawdzenie czy sesja już trwa
        boolean alreadyStarted = examSessionRepository.findByExamIdAndUserId(examId, userId).isPresent();
        if (alreadyStarted) {
            return ResponseEntity.badRequest().body("Już rozpocząłeś ten egzamin.");
        }

        // 4. Tworzenie nowej sesji
        ExamSession session = new ExamSession();
        session.setExam(exam);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now());
        session.setSubmitted(false);

        ExamSession savedSession = examSessionRepository.save(session);
        return ResponseEntity.ok(savedSession);
    }
}