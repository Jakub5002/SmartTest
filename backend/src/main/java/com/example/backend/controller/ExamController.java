package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    @Autowired
    private  ClassroomRepository classroomRepository;

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

        User user = userRepository.findById(request.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.createdBy()));

        exam.setCreatedBy(user);
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

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteExam(@PathVariable UUID id) {
        // 1. Znajdź egzamin lub rzuć błąd 404
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + id));

        // 2. Rozwiązanie problemu "classroom_exams"
        // Szukamy wszystkich klas, które mają ten egzamin na swojej liście
        List<Classroom> classrooms = classroomRepository.findAllByExamsContaining(exam);

        // Usuwamy egzamin z każdej znalezionej klasy (to czyści tabelę łączącą w bazie)
        for (Classroom classroom : classrooms) {
            classroom.getExams().remove(exam);
        }

        // Zapisujemy zmiany w klasach
        classroomRepository.saveAll(classrooms);

        // 3. Finalne usunięcie egzaminu
        // Upewnij się, że w encji Exam masz CascadeType.ALL dla pytań i sesji,
        // aby one również zostały usunięte automatycznie.
        examRepository.delete(exam);

        return ResponseEntity.noContent().build();
    }

}