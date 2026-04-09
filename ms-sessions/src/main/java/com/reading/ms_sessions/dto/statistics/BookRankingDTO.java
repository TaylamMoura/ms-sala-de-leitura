package com.reading.ms_sessions.dto.statistics;


public record BookRankingDTO(
        Long id,
        String title,
        String author,
        String coverUrl) {
}