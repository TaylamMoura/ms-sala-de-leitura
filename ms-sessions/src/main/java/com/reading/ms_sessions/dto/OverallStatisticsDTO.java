package com.reading.ms_sessions.dto;


import java.util.List;
//Estatisticas gerais - cria estatisticas de todas as leituras concluidas

public record OverallStatisticsDTO(
        List<BookRankingDTO> bookRanking,
        Long totalSecondsRead,
        int totalPagesRead,
        int totalBooksReads,
        List<CountryRankingDTO> topCountries,
        int totalCountriesRead
) { }