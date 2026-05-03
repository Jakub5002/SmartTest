package com.example.backend.service;

import com.example.backend.model.ExamSession;
import com.example.backend.repository.ExamSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    @Autowired
    private ExamSessionRepository sessionRepository;

    // Uruchamiaj co minutę (60000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCloseExpiredSessions() {
        List<ExamSession> openSessions = sessionRepository.findAllBySubmittedFalse();
        LocalDateTime now = LocalDateTime.now();

        for (ExamSession session : openSessions) {
            int limit = session.getExam().getDurationMinutes();
            if (session.getStartedAt().plusMinutes(limit).isBefore(now)) {
                session.setSubmitted(true);
                // Tutaj możesz też dopisać logikę "0 punktów" lub zapisać to, co uczeń zdążył kliknąć
                sessionRepository.save(session);
                System.out.println("Automatycznie zamknięto sesję: " + session.getId());
            }
        }
    }
}