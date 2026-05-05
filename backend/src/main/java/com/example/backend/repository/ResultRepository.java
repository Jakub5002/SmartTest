package com.example.backend.repository;

import com.example.backend.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findByUserId(UUID userId);
    List<Result> findByExamId(UUID examId);
}