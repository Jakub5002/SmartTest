package com.example.backend.service;

import com.example.backend.model.Exam;
import com.example.backend.model.ExamSession;
import com.example.backend.model.User;
import com.example.backend.repository.ExamSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamSessionServiceTest {

    @Mock
    private ExamSessionRepository examSessionRepository;

    @InjectMocks
    private ExamSessionService examSessionService;

    @Test
    void shouldCreateNewExamSession() {
        // given
        Exam exam = new Exam();
        User user = new User();

        when(examSessionRepository.saveAndFlush(any(ExamSession.class))).thenAnswer(i -> i.getArgument(0));

        // when
        ExamSession session = examSessionService.createSession(exam, user);

        // then
        assertNotNull(session);
        assertEquals(exam, session.getExam());
        assertEquals(user, session.getUser());
        assertFalse(session.isSubmitted());
        assertNotNull(session.getStartedAt());
    }
}