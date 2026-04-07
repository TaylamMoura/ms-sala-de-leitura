package com.reading.ms_catalog.dto;

public record BookUpdate(
        String title,
        String author,
        String country,
        int pages,
        int publicationYear) {
}