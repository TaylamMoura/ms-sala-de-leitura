package com.reading.ms_catalog.controller;


import com.reading.ms_catalog.dto.BookDTO;
import com.reading.ms_catalog.entity.Book;
import com.reading.ms_catalog.repository.BookRepository;
import com.reading.ms_catalog.service.AuthClientService;
import com.reading.ms_catalog.service.BookUpdate;
import com.reading.ms_catalog.service.BookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AuthClientService authClientService;
    //private final SessionsClientService sessionsClientService;

    @Autowired
    public BookController(BookService service, BookRepository bookRepository, AuthClientService authClientService /*SessionsClientService sessionsClientService*/) {
        this.bookService = service;
        this.bookRepository = bookRepository;
        this.authClientService = authClientService;
        //this.sessionsClientService = sessionsClientService;
    }


    @GetMapping("/pesquisarLivro")
    public BookDTO searchBook(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }


    @Transactional
    @PostMapping("/salvarLivro")
    public ResponseEntity<BookDTO> saveBook(@RequestBody BookDTO bookDTO, Authentication authentication) {

       Long userId = getUserFromAuth(authentication);

        if (bookDTO.title() == null || bookDTO.title().trim().isEmpty()) {
            throw new ValidationException("Título do livro não pode estar vazio.");
        }

        bookService.saveBook(bookDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookDTO);
    }


    //Exibir na Página Inicial
    @GetMapping("/livrosSalvos")
    public List<BookDTO> listBooks(Authentication authentication) {
        Long userId = getUserFromAuth(authentication);
        return bookService.listSavedBooks(userId);
    }


    @GetMapping("/exibirDados/{id}")
    public ResponseEntity<BookDTO> getDetailsBooks(@PathVariable Long id, Authentication authentication){
        Long userId = getUserFromAuth(authentication);

        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ValidationException("Livro não encontrado"));

        BookDTO bookDTO = new BookDTO(book);

        return ResponseEntity.ok(bookDTO);
    }

    @Transactional
    @DeleteMapping("/excluirLivro/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication authentication) {

        Long userId = getUserFromAuth(authentication);

        bookService.deleteBook(id, userId);

        return ResponseEntity.ok().build();
    }


    @Transactional
    @PutMapping("/editarLivro/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookUpdate updateBook, Authentication authentication) {
        Long userId = getUserFromAuth(authentication);

        Book bookToUpdate = bookService.updateBook(id, updateBook, userId);
        BookDTO bookDTO = new BookDTO(
                bookToUpdate.getId(),
                bookToUpdate.getTitle(),
                bookToUpdate.getAuthor(),
                bookToUpdate.getPages(),
                bookToUpdate.getCoverUrl(),
                bookToUpdate.getPublicationYear(),
                bookToUpdate.getFinished(),
                bookToUpdate.getCountry()
        );
        return ResponseEntity.ok(bookDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .domain("localhost")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logout feito");
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

    //Método para obter o JWT do cookie
    private String extractJwtFromCookie(HttpServletRequest request){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("jwt".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        throw new ValidationException("JWT não encontrado no cookie");
    }

    //Método para obter Id de user
    private Long getUserFromAuth(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();


        if (userId == null){
            throw new ValidationException("Usuário não encontrado");
        }
        return userId;
    }
}