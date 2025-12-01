package com.reading.ms_catalog.controller;
package com.reading.sala_de_leitura.controller;


import com.reading.ms_catalog.dto.BookDTO;
import com.reading.ms_catalog.dto.UsuarioRetornoDTO;
import com.reading.ms_catalog.entity.Book;
import com.reading.ms_catalog.entity.SessoesDeLeitura;
import com.reading.ms_catalog.entity.Usuario;
import com.reading.ms_catalog.repository.BookRepository;
import com.reading.ms_catalog.repository.SessoesRepository;
import com.reading.ms_catalog.service.BookUpdate;
import com.reading.ms_catalog.service.BookService;
import com.reading.ms_catalog.service.UsuarioService;
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
    public ResponseEntity<BookDTO> saveBook(@RequestBody BookDTO bookDTO, Authentication authentication) {

        String emailLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.buscarPorEmail(emailLogado);

        if (bookDTO.title() == null || bookDTO.title().trim().isEmpty()) {
            throw new ValidationException("Título do livro não pode estar vazio.");
        }

        bookService.saveBook(bookDTO, usuarioLogado);
        return ResponseEntity.ok(bookDTO);
    }


    //Exibir na Página Inicial
    @GetMapping("/livrosSalvos")
    public List<BookDTO> listBooks(Authentication authentication) {
        String email = authentication.getName();
        Usuario usarioLogado = usuarioService.buscarPorEmail(email);
        return bookService.listSavedBooks(usarioLogado);
    }


    @GetMapping("/exibirDados/{id}")
    public ResponseEntity<BookDTO> getDetailsBooks(@PathVariable Long id, Authentication authentication){
        Usuario usuarioLogado = usuarioService.buscarPorEmail(authentication.getName());

        Book book = bookRepository.findByIdAndUsuario(id, usuarioLogado)
                .orElseThrow(() -> new ValidationException("Livro não encontrado"));

        int paginaAtual = sessoesRepository.findTopByUsuarioIdAndLivroIdOrderByFimSessaoDesc(usuarioLogado.getId(), book.getId())
                .map(SessoesDeLeitura::getPaginaFinal)
                .orElse(0);

        BookDTO bookDTO = new BookDTO(book, paginaAtual);

        return ResponseEntity.ok(bookDTO);
    }

    @Transactional
    @DeleteMapping("/excluirLivro/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(authentication.getName());
        bookService.deleteBook(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }


    @Transactional
    @PutMapping("/editarLivro/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookUpdate atualizarDados, Authentication authentication) {
        Usuario usuarioLogado = usuarioService.buscarPorEmail(authentication.getName());
        Book bookToUpdate = bookService.updateBook(id, atualizarDados, usuarioLogado);
        BookDTO bookDTO = new BookDTO(
                bookToUpdate.getId(),
                bookToUpdate.getTitle(),
                bookToUpdate.getAuthor(),
                bookToUpdate.getPages(),
                bookToUpdate.getCoverUrl(),
                bookToUpdate.getPublicationYear(),
                0,
                bookToUpdate.getFinished()
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
    @GetMapping("/usuario-logado")
    public ResponseEntity<UsuarioRetornoDTO> obterUsuarioLogado(Authentication authentication) {
        try {
            String emailLogado = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(emailLogado);

            return ResponseEntity.ok(new UsuarioRetornoDTO(usuario));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}