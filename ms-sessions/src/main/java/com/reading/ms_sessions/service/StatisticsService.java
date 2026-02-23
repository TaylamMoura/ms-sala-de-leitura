
package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.*;
import com.reading.ms_sessions.repository.StatisticsRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.reading.ms_sessions.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final CatalogClient catalogClient;
    private final JwtService jwtService; // injeta o serviço que valida/decodifica o token

    public StatisticsService(StatisticsRepository statisticsRepository,
                             CatalogClient catalogClient,
                             JwtService jwtService) {
        this.statisticsRepository = statisticsRepository;
        this.catalogClient = catalogClient;
        this.jwtService = jwtService;
    }

    //GERA ESTATÍSTICAS GERAIS DE TODOS LIVROS LIDOS PELO USUÁRIO
    public OverallStatisticsDTO overallStatistics(String token) {
        // Extrai userId do token
        Claims claims = jwtService.validateToken(token);
        Long userId = Long.valueOf(claims.getSubject());

        // Busca livros finalizados no ms-catalog
        List<BookDTO> finishedBooks = catalogClient.listSavedBooks(userId)
                .stream()
                .filter(BookDTO::finished)
                .toList();

        // Ranking de livros
        List<BookRankingDTO> rankingBooks = finishedBooks.stream()
                .map(book -> new BookRankingDTO(
                        book.id(),
                        book.title(),
                        book.author(),
                        book.coverUrl()
                ))
                .toList();

        //Ranking de países mais lidos (Top 3)
        Map<String, Long> countryCount = finishedBooks.stream()
                .collect(Collectors.groupingBy(BookDTO::country, Collectors.counting()));

        List<CountryRankingDTO> topCountries = countryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> new CountryRankingDTO(entry.getKey(), entry.getValue())) // converte para DTO
                .toList();


        // Gera dados dos livros: segundos, paginas lidas e total de livro
        Long totalSecondsRead = Optional.ofNullable(statisticsRepository.totalSecondsReadByUser(userId)).orElse(0L);
        int totalPagesRead = statisticsRepository.totalPagesReadByUser(userId);
        int totalBooksRead = finishedBooks.size();
        int totalCountriesRead = countryCount.keySet().size();

        //Retorna o DTO
        return new OverallStatisticsDTO(
                rankingBooks,
                totalSecondsRead,
                totalPagesRead,
                totalBooksRead,
                topCountries,
                totalCountriesRead
        );

        /* Retorna: Ranking de livros finalizados, Total de segundos lidos,
        Total de páginas lidas, Total de livros finalizados
         */
    }


    //GERA ESTATÍSTICAS DE UM LIVRO ESPECÍFICO
    public BookStatisticsDTO bookStatistics(String token, Long bookId) {
        Long userId = jwtService.extractUserId(token);

        int daysRead = statisticsRepository.calculateDaysToFinishBook(bookId,userId).orElse(0);
        double avgPagesPerDay = statisticsRepository.calculateAveragePagesPerDay(bookId, userId).orElse(0.0);
        double avgSessionTime = statisticsRepository.calculateAverageSessionTime(bookId, userId).orElse(0.0);

        return new BookStatisticsDTO(daysRead, avgPagesPerDay, avgSessionTime);

        /*Retorna: Dias que levou para terminar o livro (daysRead), Média de páginas lidas por dia (averagePagesPerDay),
        Média de tempo por sessão (averageSessionTime)
         */
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
