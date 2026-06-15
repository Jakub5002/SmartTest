package com.example.backend.service;

import com.example.backend.dto.ExamResult;
import com.example.backend.dto.ExamSubmission;
import com.example.backend.dto.UserAnswer;
import com.example.backend.model.*;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.QuestionRepository;
import com.example.backend.repository.ResultRepository;
import com.example.backend.repository.StudentAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock private ExamSessionRepository examSessionRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private ResultRepository resultRepository;
    @Mock private StudentAnswerRepository studentAnswerRepository;

    @InjectMocks private ExamService examService;

    private UUID examId;
    private UUID userId;
    private ExamSession session;
    private Exam exam;
    private User user;

    @BeforeEach
    void setUp() {
        examId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        exam = new Exam();
        exam.setId(examId);
        exam.setDurationMinutes(30);

        session = new ExamSession();
        session.setExam(exam);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now().minusMinutes(10));
        session.setSubmitted(false);
    }

    @Test
    void shouldSuccessfullySubmitExamAndCalculateScore() {
        // given
        UUID q1Id = UUID.randomUUID();
        UUID q2Id = UUID.randomUUID();

        Question q1 = new Question();
        q1.setId(q1Id);
        q1.setPoints(2);
        q1.setCorrectOption("A");

        Question q2 = new Question();
        q2.setId(q2Id);
        q2.setPoints(3);
        q2.setCorrectOption("B");

        when(examSessionRepository.findFirstByExamIdAndUserId(examId, userId)).thenReturn(Optional.of(session));
        when(questionRepository.findByExamId(examId)).thenReturn(List.of(q1, q2));
        when(resultRepository.save(any(Result.class))).thenAnswer(i -> i.getArgument(0));

        ExamSubmission submission = new ExamSubmission(List.of(
                new UserAnswer(q1Id, "A"),
                new UserAnswer(q2Id, "C")
        ));

        // when
        ExamResult result = examService.submitExam(examId, userId, submission);

        // then
        assertEquals(2, result.score());
        assertEquals(5, result.totalScore());
        assertEquals(40.0, result.percentage());
        assertTrue(session.isSubmitted());
        verify(resultRepository, times(2)).save(any(Result.class));
        verify(studentAnswerRepository, times(2)).save(any(StudentAnswer.class));
    }

    @Test
    void shouldThrowExceptionWhenExamAlreadySubmitted() {
        session.setSubmitted(true);
        when(examSessionRepository.findFirstByExamIdAndUserId(examId, userId)).thenReturn(Optional.of(session));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                examService.submitExam(examId, userId, new ExamSubmission(List.of()))
        );
        assertEquals("Egzamin już został oddany", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimeExpired() {
        session.setStartedAt(LocalDateTime.now().minusMinutes(40));
        when(examSessionRepository.findFirstByExamIdAndUserId(examId, userId)).thenReturn(Optional.of(session));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                examService.submitExam(examId, userId, new ExamSubmission(List.of()))
        );
        assertEquals("Czas egzaminu minął", ex.getMessage());
        assertTrue(session.isSubmitted());
    }
}