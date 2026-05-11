package com.example.backend.controller;

import com.example.backend.model.Question;
import com.example.backend.model.Result;
import com.example.backend.repository.QuestionRepository;
import com.example.backend.repository.ResultRepository;
import com.example.backend.service.ResultService;
import com.example.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/results")
public class ResultController {
    private final ResultService resultService;
    private final UserService userService;
    private final QuestionRepository questionRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Result>> getAllResults(){
        return ResponseEntity.ok(resultService.getAllResults());
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Result>> getResultsByExam(@PathVariable UUID examId){
        return ResponseEntity.ok(resultService.getResultsByExam(examId));
    }

    @GetMapping("/{resultId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result> getResultDetails(@PathVariable UUID resultId) {
        return ResponseEntity.ok(resultService.getResultDetails(resultId));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')") // Admin też może chcieć zobaczyć swoje testowe wyniki
    public ResponseEntity<List<Result>> getMyResults(Principal principal) {
        String userEmail = principal.getName();

        UUID userId = userService.getUserIdByEmail(userEmail);

        List<Result> userResults = resultService.getResultsByUser(userId);

        return ResponseEntity.ok(userResults);
    }

    @GetMapping("/user/{userId}/exam/{examId}")
    @Transactional
    public ResponseEntity<?> getResultByUserAndExam(@PathVariable UUID userId, @PathVariable UUID examId) {
        return resultService.getResultByUserAndExam(userId, examId)
                .map(result -> {
                    List<Question> questions = questionRepository.findByExamId(examId);
                    int totalScore = questions.stream()
                            .mapToInt(Question::getPoints)
                            .sum();
                    return ResponseEntity.ok(Map.of(
                            "score", result.getScore(),
                            "totalScore", totalScore
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
