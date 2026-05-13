package com.example.backend.service;

import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.User;
import com.example.backend.repository.ExamSessionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExamSessionService {

    private final ExamSessionRepository examSessionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExamSession createSession(Exam exam, User user) {
        ExamSession session = new ExamSession();
        session.setExam(exam);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now());
        session.setSubmitted(false);
        return examSessionRepository.saveAndFlush(session);
    }
}