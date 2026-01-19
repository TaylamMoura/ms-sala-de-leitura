package com.reading.ms_sessions.dto;


public record BookRankingDTO(
        Long id,
        String title,
        String author,
        String coverUrl) {
}