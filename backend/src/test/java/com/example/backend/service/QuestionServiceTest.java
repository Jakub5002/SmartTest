package com.example.backend.service;

import com.example.backend.dto.QuestionResponse;
import com.example.backend.model.Question;
import com.example.backend.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void shouldReturnLimitedAndShuffledQuestions() {
        // given
        UUID examId = UUID.randomUUID();
        List<Question> dbQuestions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Question q = new Question();
            q.setId(UUID.randomUUID());
            q.setContent("Pytanie " + i);
            dbQuestions.add(q);
        }

        when(questionRepository.findByExamId(examId)).thenReturn(dbQuestions);

        List<QuestionResponse> result = questionService.getShuffledQuestionsForExam(examId, 5);

        assertEquals(5, result.size());
    }

    @Test
    void shouldReturnAllQuestionsIfLimitIsGreaterThanAvailable() {
        // given
        UUID examId = UUID.randomUUID();
        List<Question> dbQuestions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Question q = new Question();
            q.setId(UUID.randomUUID());
            dbQuestions.add(q);
        }

        when(questionRepository.findByExamId(examId)).thenReturn(dbQuestions);

        List<QuestionResponse> result = questionService.getShuffledQuestionsForExam(examId, 100);

        assertEquals(3, result.size());
    }
}