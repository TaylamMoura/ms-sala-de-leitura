package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-catalog") // Consul resolve esse nome
public interface CatalogClient {

    // Buscar detalhes de um livro específico para um usuário
    @GetMapping("/livros/detalhes/{bookId}")
    BookDTO getBookDetails(@PathVariable("bookId") Long bookId);

    // Listar todos os livros salvos de um usuário
    @GetMapping("/livros/meus-livros")
    List<BookDTO> listSavedBooks();

    // Contar quantos livros finalizados o usuário tem
    @GetMapping("/livros/contar-finalizados")
    int countFinishedBooks();

    //Marcar livro como finalizado
    @PutMapping("/livros/{bookId}/finalizar")
    void markAsFinished(@PathVariable("bookId") Long bookId);
}
