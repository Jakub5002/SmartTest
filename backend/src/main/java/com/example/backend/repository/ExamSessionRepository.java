package com.example.backend.repository;

import com.example.backend.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {
    Optional<ExamSession> findByExamIdAndUserId(UUID examId, UUID userId);

    List<ExamSession> findAllBySubmittedFalse();
    List<ExamSession> findAllByExamId(UUID examId);
}
