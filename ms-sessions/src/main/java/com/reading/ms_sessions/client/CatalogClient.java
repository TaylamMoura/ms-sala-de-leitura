package com.reading.ms_sessions.client;

import com.reading.ms_sessions.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Cliente HTTP para comunicação com o microserviço de catálogo via OpenFeign.
@FeignClient(name = "ms-catalog")
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
