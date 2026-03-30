
package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.*;
import com.reading.ms_sessions.repository.StatisticsRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        Claims claims = jwtService.validateToken(token);
        //Long userId = Long.valueOf(claims.getSubject());

        String subject = claims.getSubject();
        System.out.println(">>> DEBUG: Subject extraído do token: " + subject); // VEJA ISSO NO LOG

        if (subject == null || subject.equals("null")) {
            throw new RuntimeException("Usuário não identificado no token!");
        }

        Long userId = Long.valueOf(subject);


        // Busca livros e garante que não venha nulo
        List<BookDTO> allBooks = catalogClient.listSavedBooks(userId);
        if (allBooks == null) allBooks = List.of();

        List<BookDTO> finishedBooks = allBooks.stream()
                .filter(book -> book != null && book.finished())
                .toList();

        // Ranking de livros
        List<BookRankingDTO> rankingBooks = finishedBooks.stream()
                .map(book -> new BookRankingDTO(
                        book.bookId(),
                        book.title(),
                        book.author(),
                        book.coverUrl()
                ))
                .toList();

        // Ranking de países - TRATANDO NULOS NO COUNTRY
        Map<String, Long> countryCount = finishedBooks.stream()
                .map(b -> b.country() == null || b.country().isBlank() ? "N/A" : b.country())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<CountryRankingDTO> topCountries = countryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> new CountryRankingDTO(entry.getKey(), entry.getValue()))
                .toList();

        // BUSCA NO BANCO COM TRATAMENTO DE NUMBER
        Long totalSecondsRead = statisticsRepository.totalSecondsReadByUser(userId);
        if (totalSecondsRead == null) totalSecondsRead = 0L;

        // O ERRO DE 500 COSTUMA SER AQUI (Cast de Number para Integer)
        Long pagesRead = statisticsRepository.totalPagesReadByUser(userId);
        int totalPagesRead = (pagesRead != null) ? pagesRead.intValue() : 0;

        int totalBooksRead = finishedBooks.size();
        int totalCountriesRead = countryCount.keySet().size();

        return new OverallStatisticsDTO(
                rankingBooks,
                totalSecondsRead,
                totalPagesRead,
                totalBooksRead,
                topCountries,
                totalCountriesRead
        );
    }


    //GERA ESTATÍSTICAS DE UM LIVRO ESPECÍFICO
    public BookStatisticsDTO bookStatistics(String token, Long bookId) {
        Long userId = jwtService.extractUserId(token);

        Number days = (Number) statisticsRepository.calculateDaysToFinishBook(bookId, userId).orElse(0);
        int daysRead = days.intValue();

        double avgPagesPerDay = statisticsRepository.calculateAveragePagesPerDay(bookId, userId).orElse(0.0);

        //Arredondar os decimais
        double rawReadingSpeed = statisticsRepository.calculateReadingSpeed(bookId, userId).orElse(0.0);
        double readingSpeed = Math.round(rawReadingSpeed * 10.0) / 10.0;

        return new BookStatisticsDTO(daysRead, avgPagesPerDay, readingSpeed);

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
