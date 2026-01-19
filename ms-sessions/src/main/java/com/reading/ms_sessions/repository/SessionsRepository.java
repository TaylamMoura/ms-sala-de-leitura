package com.reading.ms_sessions.repository;


import com.reading.ms_sessions.entity.ReadingSession;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionsRepository extends JpaRepository<ReadingSession, Long> {

    //Busca a última sessão de leitura por user e book
    Optional<ReadingSession> findTopByUserIdAndBookIdOrderByEndTimeDesc(Long userId, Long bookId);

    //Busca a última sessão de leitura registrada para um determinado livro
    Optional<ReadingSession> findTopByBookIdOrderByEndTimeDesc(Long bookId);

    //Busca a sessão mais recente iniciada por um usuário em um determinado livro
    Optional<ReadingSession> findTopByUserIdAndBookIdOrderByStartTimeDesc(Long userId, Long bookId);

}