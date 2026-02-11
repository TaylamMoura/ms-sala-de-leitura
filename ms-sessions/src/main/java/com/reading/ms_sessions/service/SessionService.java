package com.reading.ms_sessions.service;

import com.reading.ms_sessions.entity.ReadingSession;
import com.reading.ms_sessions.repository.SessionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessionService {

    private final SessionsRepository sessionsRepository;

    @Autowired
    public SessionService(SessionsRepository sessionsRepository) {
        this.sessionsRepository = sessionsRepository;
    }

    // Iniciar Sessão
    public ReadingSession startSession(Long userId, Long bookId) {
        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(bookId);
        session.setStartPage(getLastReadPage(bookId));
        session.setStartTime(LocalDateTime.now());

        return sessionsRepository.save(session);
    }

    // Finalizar Sessão
    @Transactional
    public ReadingSession endSession(Long userId, Long bookId, int endPage, int readingTimeInSeconds) {
        int lastPage = getLastReadPage(bookId);

        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(bookId);
        session.setStartPage(lastPage);
        session.setEndPage(endPage);
        session.setReadingTime(readingTimeInSeconds);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(session.getStartTime().plusSeconds(readingTimeInSeconds));

        return sessionsRepository.save(session);
    }

    public int getLastReadPage(Long bookId) {
        return sessionsRepository.findTopByBookIdOrderByEndTimeDesc(bookId)
                .map(ReadingSession::getEndPage)
                .orElse(0);
    }
}
