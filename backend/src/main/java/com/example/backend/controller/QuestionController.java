package com.example.backend.controller;

import com.example.backend.model.Question;
import com.example.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

}
