package com.example.backend.service;

import com.example.backend.dto.QuestionResponse;
import com.example.backend.model.Question;
import com.example.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    /** :)
     * Zwraca przetasowaną listę pytań dla studenta, wykorzystując algorytm Fisher-Yates Shuffle.
     * @param examId ID egzaminu
     * @param k Liczba pytań do wylosowania
     * @return Lista DTO bez poprawnych odpowiedzi
     */
    public List<QuestionResponse> getShuffledQuestionsForExam(UUID examId, int k) {
        List<Question> allQuestions = questionRepository.findByExamId(examId);

        Random random = new Random();
        for (int i = allQuestions.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            Question temp = allQuestions.get(i);
            allQuestions.set(i, allQuestions.get(j));
            allQuestions.set(j, temp);
        }

        int limit = Math.min(k, allQuestions.size());
        List<Question> selectedQuestions = allQuestions.subList(0, limit);

        //"correctOption" przed frontendem
        return selectedQuestions.stream()
                .map(q -> new QuestionResponse(q.getId(), q.getContent(), q.getOptions()))
                .toList();
    }
}