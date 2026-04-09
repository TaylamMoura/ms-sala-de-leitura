package com.reading.ms_sessions.service;

import com.reading.ms_sessions.client.CatalogClient;
import com.reading.ms_sessions.dto.BookDTO;
import com.reading.ms_sessions.dto.session.EndSessionDTO;
import com.reading.ms_sessions.dto.session.StartSessionDTO;
import com.reading.ms_sessions.entity.ReadingSession;
import com.reading.ms_sessions.repository.SessionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessionService {

    private final SessionsRepository sessionsRepository;
    private final CatalogClient catalogClient;

    public SessionService(SessionsRepository sessionsRepository, CatalogClient catalogClient) {
        this.sessionsRepository = sessionsRepository;
        this.catalogClient = catalogClient;
    }


    @Transactional
    public ReadingSession startSession(StartSessionDTO dto, Long userId) {
        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(dto.bookId());

        int lastPage = sessionsRepository.findTopByUserIdAndBookIdOrderByEndTimeDesc(userId, dto.bookId())
                .map(ReadingSession::getEndPage)
                .orElse(0);

        session.setStartPage(lastPage);
        session.setStartTime(LocalDateTime.now());

        return sessionsRepository.save(session);
    }

    @Transactional
    public ReadingSession endSession(EndSessionDTO dto, Long userId) {
        int startPage = sessionsRepository.findTopByUserIdAndBookIdOrderByEndTimeDesc(userId, dto.bookId())
                .map(ReadingSession::getEndPage)
                .orElse(0);

        BookDTO book = catalogClient.getBookDetails(dto.bookId());

        if (dto.lastPage() >= book.pages()) {
            catalogClient.markAsFinished(dto.bookId());
        }

        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(dto.bookId());
        session.setStartPage(startPage);
        session.setEndPage(dto.lastPage());
        session.setReadingTime(dto.readingTime());
        LocalDateTime timeNow = LocalDateTime.now();
        session.setEndTime(timeNow);
        session.setStartTime(timeNow.minusSeconds(dto.readingTime()));

        return sessionsRepository.save(session);
    }
}
