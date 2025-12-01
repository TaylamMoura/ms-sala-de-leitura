package com.reading.ms_catalog.dto;

import com.reading.ms_catalog.entity.Book;

public record BookDTO(
        Long id,
        String title,
        String author,
        int pages,
        String coverUrl,
        int publicationYear,
        int currentPage,
        boolean finished) {

    public BookDTO(Book book, int currentPage) {
        this(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPages(),
                book.getCoverUrl(),
                book.getPublicationYear(),
                currentPage,
                book.getFinished());
    }
    public BookDTO(Book book){
        this(book, 0);
    }
}