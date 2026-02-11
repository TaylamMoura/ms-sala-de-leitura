package com.reading.ms_catalog.dto;

import com.reading.ms_catalog.entity.Book;

public record BookDTO(
        Long id,
        String title,
        String author,
        int pages,
        String coverUrl,
        int publicationYear,
        boolean finished,
        String country,
        Long userId) {

    public BookDTO(Book book) {
        this(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPages(),
                book.getCoverUrl(),
                book.getPublicationYear(),
                book.getFinished(),
                book.getCountry(),
                book.getUserId()
        );
    }

}