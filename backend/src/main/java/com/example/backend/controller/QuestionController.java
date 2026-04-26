package com.example.backend.controller;

import com.example.backend.dto.AnswerRequest;
import com.example.backend.dto.QuestionRequest;
import com.example.backend.dto.QuestionResponse;
import com.example.backend.model.Question;
import com.example.backend.repository.QuestionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.backend.model.Exam;
import com.example.backend.repository.ExamRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamRepository examRepository;

    @GetMapping("/test")
    public String test() {
        return "Question Logic działa";
    }

    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }
    @PostMapping
    public ResponseEntity<Question> addQuestion(@Valid @RequestBody QuestionRequest request){
        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Question question = new Question();
        question.setContent(request.content());
        question.setOptions(request.options());
        question.setCorrectOption(request.correctOption());
        question.setExam(exam);

        return ResponseEntity.ok(questionRepository.save(question));
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

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByExam(@PathVariable UUID examId) {
        List<Question> questions = questionRepository.findByExamId(examId);

        //DTO dla studenta
        List<QuestionResponse> response = questions.stream()
                .map(q -> new QuestionResponse(q.getId(), q.getContent(), q.getOptions()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")//Update Questions
    public ResponseEntity<Question> updateQuestion(@PathVariable UUID id, @RequestBody Question questionDetails) {
        return questionRepository.findById(id)
                .map(question -> {
                    // Tutaj zmieniamy dane w obiekcie wyjętym z bazy
                    question.setContent(questionDetails.getContent());
                    question.setOptions(questionDetails.getOptions());
                    question.setCorrectOption(questionDetails.getCorrectOption());

                    // musimy zachować powiązanie z egzaminem
                    if (questionDetails.getExam() != null) {
                        question.setExam(questionDetails.getExam());
                    }

                    // Zapisujemy zmiany
                    Question updated = questionRepository.save(question);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
