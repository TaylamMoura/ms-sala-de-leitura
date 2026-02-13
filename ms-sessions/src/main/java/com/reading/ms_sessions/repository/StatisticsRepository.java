
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
    @Query(value = "SELECT COALESCE(TIMESTAMPDIFF(DAY, MIN(s.start_time), MAX(s.end_time)), 0) " +
            "FROM reading_sessions s WHERE s.book_id = :bookId", nativeQuery = true)
    Optional<Integer> calculateDaysToFinishBook(@Param("bookId") Long bookId);

    // Calcula a média de páginas lidas por dia, evitando divisão por zero
    @Query(value = "SELECT COALESCE(SUM(s.end_page - s.start_page) / NULLIF(TIMESTAMPDIFF(DAY, MIN(s.start_time), MAX(s.end_time)), 0), 0) " +
            "FROM reading_sessions s WHERE s.book_id = :bookId", nativeQuery = true)
    Optional<Double> calculateAveragePagesPerDay(@Param("bookId") Long bookId);

    // Calcula a média de tempo das sessões de leitura
    @Query(value = "SELECT COALESCE(AVG(s.reading_time) / 3600, 0) FROM reading_sessions s WHERE s.book_id = :bookId", nativeQuery = true)
    Optional<Double> calculateAverageSessionTime(@Param("bookId") Long bookId);


    //Retorna os segundos para ser convertido em Total de horas lidas no service -- evita erros de conversão no SQL
    @Query(value = "SELECT SUM(reading_time) FROM reading_sessions", nativeQuery = true)
    Long totalSecondsRead();


    // Total de PÁGINAS lidas em todas as sessões
    @Query("SELECT SUM(s.lastPage - s.startPage) FROM ReadingSession s")
    int totalPagesRead();

}
