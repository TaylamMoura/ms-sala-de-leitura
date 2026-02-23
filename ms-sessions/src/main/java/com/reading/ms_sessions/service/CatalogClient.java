package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-catalog") // Consul resolve esse nome
public interface CatalogClient {

    // Buscar detalhes de um livro específico para um usuário
    @GetMapping("/books/{bookId}")
    BookDTO getBookDetails(@PathVariable("bookId") Long bookId,
                           @RequestParam("userId") Long userId);

    // Listar todos os livros salvos de um usuário
    @GetMapping("/books/user/{userId}")
    List<BookDTO> listSavedBooks(@PathVariable("userId") Long userId);

    // Contar quantos livros finalizados o usuário tem
    @GetMapping("/books/user/{userId}/count-finished")
    int countFinishedBooks(@PathVariable("userId") Long userId);
}
