package com.example.backend.repository;

import com.example.backend.model.ExamSession;
import com.example.backend.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {

    List<ExamSession> findAllBySubmittedFalse();
    Optional<ExamSession> findFirstByExamIdAndUserId(UUID examId, UUID userId);
}
