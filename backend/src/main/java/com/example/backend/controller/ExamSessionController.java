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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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


    @PostMapping("/start/{examId}")
    public ResponseEntity<?> startExam(@PathVariable UUID examId, @RequestParam UUID userId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Egzamin nie istnieje"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie istnieje"));

        List<Classroom> classroomsWithExam = classroomRepository.findAllByExamsContaining(exam);
        boolean isStuedntInClass = classroomsWithExam.stream().anyMatch(classroom -> classroom.getStudents().contains(user));

        if (!isStuedntInClass && !"ROLE_ADMIN".equals((user.getRole()))){
            return ResponseEntity.status(403).body("Ten egzamin nie został przypisany do Twojej klasy.");
        }

        if (!exam.getActive()) {
            return ResponseEntity.badRequest().body("Egzamin jest obecnie nieaktywny.");
        }

        boolean alreadyStarted = examSessionRepository.findByExamIdAndUserId(examId, userId).isPresent();
        if (alreadyStarted) {
            return ResponseEntity.badRequest().body("Już rozpocząłeś ten egzamin.");
        }

        ExamSession session = new ExamSession();
        session.setExam(exam);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now());
        session.setSubmitted(false);

        ExamSession savedSession = examSessionRepository.save(session);
        return ResponseEntity.ok(savedSession);
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
}