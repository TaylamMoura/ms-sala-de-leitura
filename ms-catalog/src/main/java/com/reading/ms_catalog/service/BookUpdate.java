package com.reading.ms_catalog.service;

public record BookUpdate(
        Long id,
        int pages,
        int publicationYear) {
}