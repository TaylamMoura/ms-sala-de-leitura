package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.EndSessionDTO;
import com.reading.ms_sessions.dto.StartSessionDTO;
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
    public ReadingSession startSession(StartSessionDTO dto) {
        ReadingSession session = new ReadingSession();
        session.setUserId(dto.userId());
        session.setBookId(dto.bookId());
        session.setStartPage(getLastReadPage(dto.bookId()));
        session.setStartTime(LocalDateTime.now());

        return sessionsRepository.save(session);
    }

    // Finalizar Sessão
    @Transactional
    public ReadingSession endSession(EndSessionDTO dto ) {
        int startPage = getLastReadPage(dto.bookId());

        ReadingSession session = new ReadingSession();
        session.setUserId(dto.userId());
        session.setBookId(dto.bookId());
        session.setStartPage(startPage);
        session.setEndPage(dto.lastPage());
        session.setReadingTime(dto.readingTime());
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(session.getStartTime().plusSeconds(dto.readingTime()));

        return sessionsRepository.save(session);
    }

    public int getLastReadPage(Long bookId) {
        return sessionsRepository.findTopByBookIdOrderByEndTimeDesc(bookId)
                .map(ReadingSession::getEndPage)
                .orElse(0);
    }
}
