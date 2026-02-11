package com.reading.ms_catalog.repository;

import com.reading.ms_catalog.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUserId(Long userId);

    Optional<Book> findByBookIdAndUserId(Long bookId, Long userId);

    List<Book> findByUserIdAndFinishedTrue(Long userId);

}