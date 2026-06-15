package com.example.backend.service;

import com.example.backend.model.Result;
import com.example.backend.repository.ResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    void shouldReturnLatestResultForUserAndExam() {
        UUID userId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();
        Result latestResult = new Result();
        latestResult.setScore(10);

        when(resultRepository.findByUserIdAndExamIdOrderByFinishedAtDesc(userId, examId))
                .thenReturn(List.of(latestResult, new Result()));

        Optional<Result> result = resultService.getResultByUserAndExam(userId, examId);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getScore());
    }

    @Test
    void shouldReturnEmptyWhenNoResults() {
        UUID userId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        when(resultRepository.findByUserIdAndExamIdOrderByFinishedAtDesc(userId, examId))
                .thenReturn(List.of());

        Optional<Result> result = resultService.getResultByUserAndExam(userId, examId);

        assertTrue(result.isEmpty());
    }
}