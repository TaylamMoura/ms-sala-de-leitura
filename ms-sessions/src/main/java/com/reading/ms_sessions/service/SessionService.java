package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.BookDTO;
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


    //Instância da entidade SessaoService
    @Autowired
    public SessionService(SessionsRepository sessionsRepository, CatalogClient catalogClient) {
        this.sessionsRepository = sessionsRepository;
        this.catalogClient = catalogClient;
    }

    // Funções do Cronômetro
    public ReadingSession startSession(Long userId, Long bookId, String token) {
        //Validar livro no ms-catalog
        BookDTO bookDTO = catalogClient.getBook(bookId, token);

        // Salva sessão
        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(bookId);
        session.setStartPage(getLastReadPage(bookId));
        session.setCoverUrl(bookDTO.coverUrl());
        session.setStartTime(LocalDateTime.now());

        return sessionsRepository.save(session);
    }


    // Finalizar Sessão
    @Transactional
    public ReadingSession endSession(Long userId, Long bookId, int endPage, int readingTimeInSeconds, String token) {
        int lastPage = getLastReadPage(bookId);

        ReadingSession session = new ReadingSession();
        session.setUserId(userId);
        session.setBookId(bookId);
        session.setStartPage(lastPage); //página inicial da última sessão salva
        session.setEndPage(endPage);    // página em que usuário parou nesta sessão
        session.setReadingTime(readingTimeInSeconds);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(session.getStartTime().plusSeconds(readingTimeInSeconds));

        // Valida livro no catálogo
        BookDTO bookDTO = catalogClient.getBook(bookId, token);

        //Marcar livro como 'finalizado' no ms-catalog se chegar a última página
        if (endPage >= bookDTO.pages()){
            catalogClient.markBookAsFinished(bookId, token);
        }

        return sessionsRepository.save(session);
    }

    public int getLastReadPage(Long bookId) {
        return sessionsRepository.findTopByBookIdOrderByEndTimeDesc(bookId)
                .map(ReadingSession::getEndPage)
                .orElse(0);
    }
}
