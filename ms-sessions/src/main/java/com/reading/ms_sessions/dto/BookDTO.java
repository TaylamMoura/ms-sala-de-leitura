package com.reading.ms_sessions.dto;

import java.awt.print.Book;

public record BookDTO(
        Long id,
        String title,
        String author,
        int pages,
        String coverUrl,
        int publicationYear,
        boolean finished,
        String country,
        Long userId) {}