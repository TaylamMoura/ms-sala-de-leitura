package com.reading.ms_catalog.service;

public record BookUpdate(
        Long id,
        String title,
        String author,
        String country,
        int pages,
        int publicationYear) {
}