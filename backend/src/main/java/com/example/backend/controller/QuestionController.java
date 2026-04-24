package com.example.backend.controller;

import com.example.backend.model.AnswerRequest;
import com.example.backend.model.Question;
import com.example.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/test")
    public String test() {
        return "Question Logic działa";
    }

    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }
    @PostMapping
    public Question addQuestion(@RequestBody Question question) { // <-- MUSI BYĆ @RequestBody
        return questionRepository.save(question);
    }

    @PostMapping("/{id}/check")//Sprawdzanie czy pytanie jest dobre z wymagana odpowiedzią
    public ResponseEntity<String> checkAnswer(@PathVariable UUID id, @RequestBody AnswerRequest answer) {
        return questionRepository.findById(id)
                .map(question -> {
                    if (question.getCorrectOption().equals(answer.selectedOption())) {
                        return ResponseEntity.ok("Poprawan odpowiedz");
                    } else {
                        return ResponseEntity.ok("Błędna odpowiedź. Poprawan odpowiedz to: " + question.getCorrectOption());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exam/{examId}")//Powiązanie pytań z konkretnym egzaminem
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable UUID examId) {
        List<Question> questions = questionRepository.findByExamId(examId);
        return ResponseEntity.ok(questions);
    }

}
