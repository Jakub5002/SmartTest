package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.example.backend.service.ExamService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamSessionRepository examSessionRepository;
    private final UserRepository userRepository;
    private  final ClassroomRepository classroomRepository;
    private final ExamService examService;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getById(@PathVariable UUID id) {
        return examRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    public ResponseEntity<?> submitExam(@PathVariable UUID examId, @RequestBody ExamSubmission submission, Principal principal) {
        try {
            UUID userId = userService.getUserIdByEmail(principal.getName());
            ExamResult result = examService.submitExam(examId, userId, submission);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + id));

        List<Classroom> classrooms = classroomRepository.findAllByExamsContaining(exam);

        for (Classroom classroom : classrooms) {
            classroom.getExams().remove(exam);
        }

        classroomRepository.saveAll(classrooms);

        examRepository.delete(exam);

        return ResponseEntity.noContent().build();
    }
    @Transactional
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Set<ExamDTO>> getMyAvaliableExams(Principal principal) {
        UUID userId = userService.getUserIdByEmail(principal.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Uzytkownik nie istnieje"));

        List<Classroom> userClassrooms = classroomRepository.findAllByStudentsContainingWithExams(user);

        Set<ExamDTO> availableExams = userClassrooms.stream()
                .flatMap(classroom -> classroom.getExams().stream()
                        .filter(Exam::getActive)
                        .map(exam -> new ExamDTO(
                                exam.getId(),
                                exam.getTitle(),
                                exam.getDurationMinutes(),
                                exam.getActive(),
                                classroom.getName()
                        )))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(availableExams);
    }

    @Transactional
    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<Exam> exams = examRepository.findAll();
        List<Classroom> classrooms = classroomRepository.findAll();

        return exams.stream().map(exam -> {
            Map<String, Object> examMap = new HashMap<>();
            examMap.put("id", exam.getId());
            examMap.put("title", exam.getTitle());
            examMap.put("durationMinutes", exam.getDurationMinutes());
            examMap.put("active", exam.getActive());
            System.out.println("Liczba classroom: " + classrooms.size());

            String classroomName = classrooms.stream()
                    .filter(c -> c.getExams().stream()
                            .anyMatch(e -> e.getId().equals(exam.getId()))) // <- porównanie po UUID
                    .map(Classroom::getName)
                    .findFirst()
                    .orElse("Brak klasy");
            examMap.put("classroomName", classroomName);

            return examMap;
        }).collect(Collectors.toList());
    }

}