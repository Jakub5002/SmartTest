package com.example.backend.repository;

import com.example.backend.model.Result;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findByUserId(UUID userId);
    List<Result> findByExamId(UUID examId);
    List<Result> findByUser(User user);
    @Query("SELECT r FROM Result r WHERE r.user.id = :userId AND r.exam.id = :examId ORDER BY r.finishedAt DESC")
    List<Result> findByUserIdAndExamIdOrderByFinishedAtDesc(@Param("userId") UUID userId, @Param("examId") UUID examId);

}