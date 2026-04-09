package com.reading.ms_catalog.dto;

import com.reading.ms_catalog.entity.Book;

public record BookDTO(
        Long bookId,
        String title,
        String author,
        int pages,
        String coverUrl,
        int publicationYear,
        boolean finished,
        String country,
        Integer lastPage) {

    public BookDTO(Book book, Integer lastPage) {
        this(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPages(),
                book.getCoverUrl(),
                book.getPublicationYear(),
                book.getFinished(),
                book.getCountry(),
                lastPage
        );
    }

}