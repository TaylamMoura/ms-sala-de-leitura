package com.reading.ms_sessions.dto;

public record BookDTO(
        Long bookId,
        String title,
        String author,
        int pages,
        String coverUrl,
        int publicationYear,
        boolean finished,
        String country) {}
