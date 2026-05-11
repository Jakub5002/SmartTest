package com.example.backend.service;

import com.example.backend.dto.ExamResult;
import com.example.backend.dto.ExamSubmission;
import com.example.backend.dto.UserAnswer;
import com.example.backend.model.ExamSession;
import com.example.backend.model.Question;
import com.example.backend.model.Result;
import com.example.backend.model.StudentAnswer;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.QuestionRepository;
import com.example.backend.repository.ResultRepository;
import com.example.backend.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamSessionRepository examSessionRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Transactional
    public ExamResult submitExam(UUID examId, ExamSubmission submission) {
        ExamSession session = examSessionRepository.findFirstByExamIdAndUserId(examId, submission.userId())
                .orElseThrow(() -> new RuntimeException("Nie rozpocząłeś egzaminu"));

        if (session.isSubmitted()) {
            throw new RuntimeException("Egzamin już został oddany");
        }

        int durationMinutes = session.getExam().getDurationMinutes();
        LocalDateTime endTime = session.getStartedAt().plusMinutes(durationMinutes);
        if (LocalDateTime.now().isAfter(endTime.plusMinutes(1))) {
            session.setSubmitted(true);
            examSessionRepository.save(session);
            throw new RuntimeException("Czas egzaminu minął");
        }

        List<Question> questions = questionRepository.findByExamId(examId);
        if (questions.isEmpty()) {
            throw new RuntimeException("Brak pytań w egzaminie");
        }

        Map<UUID, Question> correctAnswersMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int score = 0;
        int totalScore = questions.stream().mapToInt(Question::getPoints).sum();

        Result result = new Result();
        result.setUser(session.getUser());
        result.setExam(session.getExam());
        result.setStartedAt(session.getStartedAt());
        result.setFinishedAt(LocalDateTime.now());
        result = resultRepository.save(result);

        for (UserAnswer userAnswer : submission.answers()) {
            Question dbQuestion = correctAnswersMap.get(userAnswer.questionId());

            if (dbQuestion != null) {
                if (dbQuestion.getCorrectOption().equals(userAnswer.selectedOption())) {
                    score += dbQuestion.getPoints();
                }

                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setResult(result);
                studentAnswer.setQuestion(dbQuestion);
                studentAnswer.setSelectedOption(userAnswer.selectedOption());
                studentAnswerRepository.save(studentAnswer);
            }
        }

        result.setScore(score);
        resultRepository.save(result);

        session.setSubmitted(true);
        examSessionRepository.save(session);

        double percentage = totalScore > 0 ? ((double) score / totalScore) * 100 : 0;
        return new ExamResult(score, totalScore, percentage);
    }


    @Transactional
    public void forceSubmitExpiredSession(ExamSession session) {
        if (session.isSubmitted()) return;

        List<Question> questions = questionRepository.findByExamId(session.getExam().getId());
        Map<UUID, Question> correctAnswersMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int score = 0;

        Result result = new Result();
        result.setUser(session.getUser());
        result.setExam(session.getExam());
        result.setStartedAt(session.getStartedAt());
        result.setFinishedAt(LocalDateTime.now());
        result = resultRepository.save(result);

        Map<UUID, String> drafts = session.getDraftAnswers();
        if (drafts != null && !drafts.isEmpty()) {
            for (Map.Entry<UUID, String> entry : drafts.entrySet()) {
                UUID questionId = entry.getKey();
                String selectedOption = entry.getValue();

                Question dbQuestion = correctAnswersMap.get(questionId);

                if (dbQuestion != null) {
                    if (dbQuestion.getCorrectOption().equals(selectedOption)) {
                        score += dbQuestion.getPoints();
                    }

                    StudentAnswer studentAnswer = new StudentAnswer();
                    studentAnswer.setResult(result);
                    studentAnswer.setQuestion(dbQuestion);
                    studentAnswer.setSelectedOption(selectedOption);
                    studentAnswerRepository.save(studentAnswer);
                }
            }
        }

        result.setScore(score);
        resultRepository.save(result);

        session.setSubmitted(true);
        examSessionRepository.save(session);
    }
}