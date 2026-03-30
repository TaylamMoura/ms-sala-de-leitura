package com.reading.ms_sessions.dto;


import java.util.List;
//Estatisticas gerais - cria estatisticas de todas as leituras concluidas

public record OverallStatisticsDTO(
        List<BookRankingDTO> rankingBooks,
        Long totalSecondsRead,
        int totalPagesRead,
        int totalBooksRead,
        List<CountryRankingDTO> topCountries,
        int totalCountriesRead
) { }