package com.reading.ms_catalog.controller;

import com.reading.ms_catalog.dto.BookDTO;
import com.reading.ms_catalog.entity.Book;
import com.reading.ms_catalog.repository.BookRepository;
import com.reading.ms_catalog.dto.BookUpdate;
import com.reading.ms_catalog.service.BookService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;


    @Autowired
    public BookController(BookService service, BookRepository bookRepository) {
        this.bookService = service;
        this.bookRepository = bookRepository;
    }


    @GetMapping("/pesquisar")
    public List<BookDTO> searchBook(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }


    @Transactional
    @PostMapping("/salvar")
    public ResponseEntity<BookDTO> saveBook(@RequestBody BookDTO bookDTO,
                                            @RequestHeader("X-User-Id") Long userId) {

        if (bookDTO.title() == null || bookDTO.title().trim().isEmpty()) {
            throw new ValidationException("Título do livro não pode estar vazio.");
        }

        bookService.saveBook(bookDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookDTO);
    }


    //Exibir na Página Inicial
    @GetMapping("/meus-livros")
    public List<BookDTO> listBooks(@RequestHeader("X-User-Id") Long userId) {
        return bookService.listSavedBooks(userId);
    }


    @GetMapping("/detalhes/{bookId}")
    public ResponseEntity<BookDTO> getDetailsBooks(@PathVariable Long bookId,
                                                   @RequestHeader("X-User-Id") Long userId){

        Book book = bookRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new ValidationException("Livro não encontrado"));

        BookDTO bookDTO = new BookDTO(book, null);

        return ResponseEntity.ok(bookDTO);
    }

    @Transactional
    @DeleteMapping("/excluir/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId,
                                           @RequestHeader("X-User-Id") Long userId) {

        bookService.deleteBook(bookId, userId);

        return ResponseEntity.ok().build();
    }


    @Transactional
    @PutMapping("/editar/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId,
                                              @RequestBody BookUpdate updateBook,
                                              @RequestHeader("X-User-Id") Long userId) {

        Book bookToUpdate = bookService.updateBook(bookId, updateBook, userId);

        BookDTO bookDTO = new BookDTO(
                bookToUpdate.getBookId(),
                bookToUpdate.getTitle(),
                bookToUpdate.getAuthor(),
                bookToUpdate.getPages(),
                bookToUpdate.getCoverUrl(),
                bookToUpdate.getPublicationYear(),
                bookToUpdate.getFinished(),
                bookToUpdate.getCountry(),
                bookToUpdate.getUserId(),
                null
        );
        return ResponseEntity.ok(bookDTO);
    }

    //endpoint usado em ms-session
    @PutMapping("/{bookId}/finalizar/{userId}")
    public ResponseEntity<Void> markBookAsFinished(@PathVariable Long bookId, @PathVariable Long userId){
        bookService.markAsFinished(bookId, userId);
        return ResponseEntity.ok().build();
    }
}