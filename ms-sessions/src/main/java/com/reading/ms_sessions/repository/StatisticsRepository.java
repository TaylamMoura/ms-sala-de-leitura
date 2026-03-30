
package com.reading.ms_sessions.repository;

import com.reading.ms_sessions.entity.ReadingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface StatisticsRepository extends JpaRepository<ReadingSession, Long> {

    // Calcula quantos dias levou para finalizar o livro
    @Query(value = "SELECT COALESCE(EXTRACT(DAY FROM (MAX(s.end_time) - MIN(s.start_time))), 0) " +
            "FROM sessions s WHERE s.book_id = :bookId AND s.user_id = :userId", nativeQuery = true)
    Optional<Integer> calculateDaysToFinishBook(@Param("bookId") Long bookId, @Param("userId") Long userId);


    // Calcula a média de páginas lidas por dia, evitando divisão por zero
    @Query(value = "SELECT COALESCE(SUM(s.end_page - s.start_page) / NULLIF(EXTRACT(DAY FROM (MAX(s.end_time) - MIN(s.start_time))), 0), 0) " +
            "FROM sessions s WHERE s.book_id = :bookId AND s.user_id = :userId", nativeQuery = true)
    Optional<Double> calculateAveragePagesPerDay(@Param("bookId") Long bookId, @Param("userId") Long userId);


    // Calcula a velocidade de leitura (Páginas por Hora)
    @Query(value = "SELECT COALESCE(SUM(s.end_page - s.start_page) / (NULLIF(SUM(s.reading_time), 0) / 3600.0), 0) " +
            "FROM sessions s WHERE s.book_id = :bookId AND s.user_id = :userId", nativeQuery = true)
    Optional<Double> calculateReadingSpeed(@Param("bookId") Long bookId, @Param("userId") Long userId);

    //Retorna os segundos para ser convertido em Total de horas lidas no service -- evita erros de conversão no SQL
    @Query(value = "SELECT COALESCE(SUM(s.reading_time), 0) " +
            "FROM sessions s WHERE s.user_id = :userId", nativeQuery = true)
    Long totalSecondsReadByUser(@Param("userId") Long userId);


    // Total de PÁGINAS lidas em todas as sessões
    @Query(value = "SELECT COALESCE(SUM(s.end_page - s.start_page), 0) " +
            "FROM sessions s WHERE s.user_id = :userId", nativeQuery = true)
    Long totalPagesReadByUser(@Param("userId") Long userId);
}
