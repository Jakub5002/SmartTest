package com.example.backend.service;

import com.example.backend.model.Result;
import com.example.backend.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;

    public List<Result> getAllResults(){
        return resultRepository.findAll();
    }

    public List<Result> getResultsByExam(UUID examId){
        return resultRepository.findByExamId(examId);
    }

    public List<Result> getResultsByUser(UUID userId) {
        return resultRepository.findByUserId(userId);
    }

    public Result getResultDetails(UUID resultId){
        return resultRepository.findById(resultId).orElseThrow(() -> new RuntimeException("Nie znaleznionow wuyniku o ID " + resultId));
    }

    public Optional<Result> getResultByUserAndExam(UUID userId, UUID examId) {
        return resultRepository.findFirstByUserIdAndExamId(userId, examId);
    }
}
