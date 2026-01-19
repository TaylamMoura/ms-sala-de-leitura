package com.reading.ms_catalog.dto;

public record BookUpdate(
        Long id,
        String title,
        String author,
        String country,
        int pages,
        int publicationYear) {
}