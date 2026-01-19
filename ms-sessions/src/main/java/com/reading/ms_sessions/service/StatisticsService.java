package com.reading.ms_sessions.service;


import com.reading.ms_sessions.dto.BookRankingDTO;
import com.reading.ms_sessions.dto.BookStatisticsDTO;
import com.reading.ms_sessions.dto.OverallStatisticsDTO;
import com.reading.ms_sessions.repository.StatisticsRepository;
import com.reading.sala_de_leitura.entity.Usuario;


import java.awt.print.Book;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private StatisticsRepository statisticsRepository;


    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }


    //Service de Estatistíca do LIVRO
    public BookStatisticsDTO bookStatistics(Long bookId, Usuario usuarioLogado) {
        return new Book(
                statisticsRepository.calculateDaysToFinishBook(bookId).orElse(0),
                statisticsRepository.calculateAveragePagesPerDay(bookId).orElse(0.0),
                statisticsRepository.calculateAverageSessionTime(bookId).orElse(0.0)

                /*
                statisticsRepository.calcularDiasParaTerminarLivro(bookId).orElse(0),
                statisticsRepository.calcularMediaPaginasPorDia(bookId).orElse(0.0),
                statisticsRepository.calcularMediaTempoSessao(bookId).orElse(0.0)*/
        );
    }


    //Service de Estatistíca GERAL
    public OverallStatisticsDTO overallStatistics(Usuario usuarioLogado) {
        Long usuarioId = usuarioLogado.getId();
        List<BookRankingDTO> rankingBooks = statisticsRepository.rankingBooks(usuarioLogado.getId());

        // Converte a string "HH:MM:SS" para segundos usando nosso método auxiliar (converterHorasParaSegundos)
        Long totalSecondsRead = Optional.ofNullable(statisticsRepository.totalSecondsRead())
                .orElse(0L);


        return new OverallStatisticsDTO(
                rankingBooks,
                totalSecondsRead,
                statisticsRepository.totalPagesRead(),
                statisticsRepository.totalBooksRead()
        );
    }


    // Método auxiliar para converter "HH:MM:SS" em segundos
    private long convertHoursToSeconds(String formattedHours) {
        if (formattedHours == null || formattedHours.isEmpty()) {
            return 0L;
        }
        LocalTime time = LocalTime.parse(formattedHours);
        return time.toSecondOfDay();
    }



    private String formattedHoursMinutes(Long seconds) {
        if (seconds == null) return "0 horas e 0 minutos";

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + " horas e " + minutes + " minutos";
    }

}
