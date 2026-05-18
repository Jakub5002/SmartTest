package com.example.backend.controller;

import com.example.backend.dto.UserAnswer;
import com.example.backend.model.Classroom;
import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.User;
import com.example.backend.repository.ClassroomRepository;
import com.example.backend.repository.ExamRepository;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.ExamSessionService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/exam-sessions")
@RequiredArgsConstructor
public class ExamSessionController {

    private final ExamSessionRepository examSessionRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final UserService userService;

    private final ExamSessionService examSessionService;

    @Transactional
    @PostMapping("/start/{examId}")
    public ResponseEntity<?> startExam(@PathVariable UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Egzamin nie istnieje"));

        UUID userId = userService.getUserIdByEmail(principal.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie istnieje"));

        List<Classroom> classroomsWithExam = classroomRepository.findAllByExamsContaining(exam);
        boolean isStudentInClass = classroomsWithExam.stream()
                .anyMatch(classroom -> classroom.getStudents().contains(user));

        if (!isStudentInClass && !"ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Ten egzamin nie został przypisany do Twojej klasy.");
        }

        if (!exam.getActive()) {
            return ResponseEntity.badRequest().body("Egzamin jest obecnie nieaktywny.");
        }

        Optional<ExamSession> existingSession = examSessionRepository.findFirstByExamIdAndUserId(examId, userId);
        if (existingSession.isPresent()) {
            if (existingSession.get().isSubmitted()) {
                return ResponseEntity.status(409).body("ALREADY_SUBMITTED");
            }
            return ResponseEntity.ok(existingSession.get());
        }

        try {
            ExamSession session = examSessionService.createSession(exam, user);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return examSessionRepository.findFirstByExamIdAndUserId(examId, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(500).build());
        }
    }
    @PostMapping("/{sessionID}/auto-save")
    public ResponseEntity<?> autoSave(@PathVariable UUID sessionID,@RequestBody List<UserAnswer> partialAnswers) {
        ExamSession session = examSessionRepository.findById(sessionID).orElseThrow(()-> new EntityNotFoundException("Sesja nie istenije"));
        if(session.isSubmitted()){
            return ResponseEntity.badRequest().body("Nie można nadpisać, egzamin juz jest oddany");
        }
        for(UserAnswer answer : partialAnswers){
            session.getDraftAnswers().put(answer.questionId(), answer.selectedOption());
        }
        examSessionRepository.save(session);
        return ResponseEntity.ok("Zapisano odpowiedzi");
    }

    @GetMapping("/status/{examId}")
    public ResponseEntity<?> getSessionStatus(@PathVariable UUID examId, @RequestParam UUID userId) {
        Optional<ExamSession> session = examSessionRepository.findFirstByExamIdAndUserId(examId, userId);
        if (session.isEmpty()) return ResponseEntity.ok(Map.of("status", "NOT_STARTED"));
        if (session.get().isSubmitted()) return ResponseEntity.ok(Map.of("status", "SUBMITTED"));
        return ResponseEntity.ok(Map.of("status", "IN_PROGRESS"));
    }
}