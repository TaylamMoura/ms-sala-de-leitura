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


    @GetMapping("/pesquisarLivro")
    public BookDTO searchBook(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }


    @Transactional
    @PostMapping("/salvarLivro")
    public ResponseEntity<BookDTO> saveBook(@RequestBody BookDTO bookDTO) {

        if (bookDTO.title() == null || bookDTO.title().trim().isEmpty()) {
            throw new ValidationException("Título do livro não pode estar vazio.");
        }

        bookService.saveBook(bookDTO, bookDTO.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookDTO);
    }


    //Exibir na Página Inicial
    @GetMapping("/livrosSalvos/{userId}")
    public List<BookDTO> listBooks(@PathVariable Long userId) {
        return bookService.listSavedBooks(userId);
    }


    @GetMapping("/exibirDados/{bookId}/{userId}")
    public ResponseEntity<BookDTO> getDetailsBooks(@PathVariable Long bookId, @PathVariable Long userId){

        Book book = bookRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new ValidationException("Livro não encontrado"));

        BookDTO bookDTO = new BookDTO(book);

        return ResponseEntity.ok(bookDTO);
    }

    @Transactional
    @DeleteMapping("/excluirLivro/{bookId}/{userId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId, @PathVariable Long userId) {

        bookService.deleteBook(bookId, userId);

        return ResponseEntity.ok().build();
    }


    @Transactional
    @PutMapping("/editarLivro/{bookId}/{userId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody BookUpdate updateBook, @PathVariable Long userId) {

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
                bookToUpdate.getUserId()
        );
        return ResponseEntity.ok(bookDTO);
    }

    //endpoint usado em ms-session
    @PutMapping("/{bookId}/finalizar/{userId}")
    public ResponseEntity<Void> markBookAsFinished(@PathVariable Long bookId, @PathVariable Long userId){
        bookService.markAsFinished(bookId, userId);
        return ResponseEntity.ok().build();
    }



    //exibir nome de usuario no front
    /*@GetMapping("/usuario-logado")
    public ResponseEntity<UsuarioRetornoDTO> obterUsuarioLogado(Authentication authentication) {
        try {
            String emailLogado = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(emailLogado);

            return ResponseEntity.ok(new UsuarioRetornoDTO(usuario));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/

    /*Método para obter o JWT do cookie
    private String extractJwtFromCookie(HttpServletRequest request){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("jwt".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        throw new ValidationException("JWT não encontrado no cookie");
    }*/

    //Método para obter Id de user
    private Long getUserFromAuth(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();


        if (userId == null){
            throw new ValidationException("Usuário não encontrado");
        }
        return userId;
    }
}