package com.reading.ms_catalog.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reading.ms_catalog.entity.Book;
import com.reading.ms_catalog.repository.BookRepository;
import com.reading.ms_catalog.dto.BookDTO;
import com.reading.sala_de_leitura.entity.Usuario;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BookService {

    private final APIConnection apiConnection;
    private final BookRepository repository;

    @Autowired
    public BookService(APIConnection apiConnection, BookRepository repository) {
        this.apiConnection = apiConnection;
        this.repository = repository;
    }

    public BookDTO searchByTitle(String title) {
        try {
            String titleEncoded = URLEncoder.encode("intitle:" + title, StandardCharsets.UTF_8.toString());
            String jsonResponse = apiConnection.booksJson(titleEncoded);

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null && !items.isEmpty()) {
                JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.get("title").getAsString();
                String author = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
                int pageCount = volumeInfo.get("pageCount").getAsInt();
                String thumbnail = volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
                String publishedDate = volumeInfo.get("publishedDate").getAsString();
                int ano = publishedDate != null ? Integer.parseInt(publishedDate.split("-")[0]) : 0;

                return new BookDTO(null, title, author, pageCount, thumbnail, ano, 0, false);

            } else {
                System.out.println("Livro não encontrado. Tente outro título!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Transactional
    public void saveBook(BookDTO bookDTO, Usuario usuarioLogado){

        String urlCapa = bookDTO.coverUrl();

        if (urlCapa == null || urlCapa.trim().isEmpty()){
            urlCapa = "https://via.placeholder.com/150";
        }

        Book book =new Book(
                null,
                bookDTO.title(),
                bookDTO.author(),
                bookDTO.pages(),
                urlCapa,
                bookDTO.publicationYear(),
                usuarioLogado,
                false
        );

        repository.save(book);
    }


    public List<BookDTO> listSavedBooks(Usuario usuario){
        List<Book> books = repository.findByUsuario(usuario);
        return books.stream().map(BookDTO::new).toList();
    }


    @Transactional
    public void deleteBook(Long id, Usuario usuario){
        Book book = repository.findById(id)
                .orElseThrow(() -> new ValidationException("Livro não escontrado"));

        if (!book.getUserId().equals(usuario)){
            throw new ValidationException("Você não tem permissão para excluir este book.");
        }
        repository.delete(book);
    }


    @Transactional
    public Book updateBook(Long id, BookUpdate bookUpdate, Usuario usuario) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new ValidationException("Livro não escontrado"));
        if (!book.getUserId().equals(book.getUserId())){
            throw new ValidationException("Você não tem permissão para excluir este book.");
        }
        book.update(bookUpdate);
        repository.save(book);
        return book;
    }


    public BookDTO getBookDetails(Long id, Usuario usuario) {
        Book book = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(()-> new ValidationException("Livro não encontrado"));
        return  new BookDTO(book);
    }
}