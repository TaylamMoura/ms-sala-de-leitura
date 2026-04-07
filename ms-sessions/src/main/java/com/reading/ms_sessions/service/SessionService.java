package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.BookDTO;
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
    private final CatalogClient catalogClient;

    @Autowired
    public SessionService(SessionsRepository sessionsRepository, CatalogClient catalogClient) {
        this.sessionsRepository = sessionsRepository;
        this.catalogClient = catalogClient;
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
        int startPage = sessionsRepository.findTopByUserIdAndBookIdOrderByEndTimeDesc(dto.userId(), dto.bookId())
                .map(ReadingSession::getEndPage)
                .orElse(0);

        //Busca Total de páginas
        BookDTO book = catalogClient.getBookDetails(dto.bookId(), dto.userId());

        //Verifica se livro foi finalizado
        if (dto.lastPage() >= book.pages()) {
            // Se ele terminou, avisamos o outro microserviço!
            catalogClient.markAsFinished(dto.bookId(), dto.userId());
        }

        ReadingSession session = new ReadingSession();
        session.setUserId(dto.userId());
        session.setBookId(dto.bookId());
        session.setStartPage(startPage);
        session.setEndPage(dto.lastPage());
        session.setReadingTime(dto.readingTime());
        LocalDateTime timeNow = LocalDateTime.now();
        session.setEndTime(timeNow);
        session.setStartTime(timeNow.minusSeconds(dto.readingTime()));

        return sessionsRepository.save(session);
    }

    public int getLastReadPage(Long bookId) {
        return sessionsRepository.findTopByBookIdOrderByEndTimeDesc(bookId)
                .map(ReadingSession::getEndPage)
                .orElse(0);
    }
}
