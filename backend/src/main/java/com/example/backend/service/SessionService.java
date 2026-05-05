package com.example.backend.service;

import com.example.backend.model.ExamSession;
import com.example.backend.model.Result;
import com.example.backend.repository.ExamSessionRepository;
import com.example.backend.repository.ResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SessionService {

    private ExamSessionRepository sessionRepository;
    private ResultRepository resultRepository;
    private ExamService examService;

    // Uruchamiaj co minutę (60000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCloseExpiredSessions() {
        List<ExamSession> openSessions = sessionRepository.findAllBySubmittedFalse();
        LocalDateTime now = LocalDateTime.now();

        for (ExamSession session : openSessions) {
            int limit = session.getExam().getDurationMinutes();
            if (session.getStartedAt().plusMinutes(limit).plusMinutes(1).isBefore(now)) {


                examService.forceSubmitExpiredSession(session);

                System.out.println("Czas minął. Automatycznie oceniono i zamknięto sesję: " + session.getId());
            }
        }
    }
}