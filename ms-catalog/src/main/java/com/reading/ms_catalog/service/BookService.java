package com.reading.ms_catalog.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reading.ms_catalog.dto.BookUpdate;
import com.reading.ms_catalog.entity.Book;
import com.reading.ms_catalog.repository.BookRepository;
import com.reading.ms_catalog.dto.BookDTO;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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


    public List<BookDTO> searchByTitle(String bookTitle) {
        List<BookDTO> listBooks = new ArrayList<>();
        try {
            String titleEncoded = URLEncoder.encode("intitle:" + bookTitle, StandardCharsets.UTF_8.toString());
            String jsonResponse = apiConnection.booksJson(titleEncoded);

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null && !items.isEmpty()) {
                for(int i = 0; i < Math.min(items.size(), 12); i++){
                    try {
                        JsonObject item = items.get(i).getAsJsonObject();
                        JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");

                        String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Título Desconhecido";

                        String author = "Autor desconhecido";
                        if (volumeInfo.has("authors")) {
                            author = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
                        }

                        int pageCount = volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").getAsInt() : 0;

                        String thumbnail = "https://via.placeholder.com/150x225?text=Sem+Capa";
                        if (volumeInfo.has("imageLinks")) {
                            thumbnail = volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
                        }

                        String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "0000";
                        int year = Integer.parseInt(publishedDate.split("-")[0]);

                        listBooks.add(new BookDTO(null, title, author, pageCount, thumbnail, year, false, "N/A", null));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBooks;
    }


    @Transactional
    public void saveBook(BookDTO bookDTO, Long userId){
        String urlCapa = bookDTO.coverUrl();
        if (urlCapa == null || urlCapa.trim().isEmpty()){
            urlCapa = "https://via.placeholder.com/150";
        }

        Book book = new Book(
                null,
                bookDTO.title(),
                bookDTO.author(),
                bookDTO.pages(),
                urlCapa,
                bookDTO.publicationYear(),
                userId,
                false,
                bookDTO.country()
        );
        repository.save(book);
    }


    @Transactional
    public List<BookDTO> listSavedBooks(Long userId){
        List<Book> books = repository.findByUserId(userId);
        return books.stream().map(BookDTO::new).toList();
    }


    @Transactional
    public void deleteBook(Long bookId, Long userId){
        Book book = repository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        repository.delete(book);
    }


    @Transactional
    public Book updateBook(Long bookId, BookUpdate bookUpdate, Long userId) {
        Book book = repository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        book.update(bookUpdate);
        repository.save(book);
        return book;
    }

    @Transactional
    public void markAsFinished(Long bookId, Long userId){
        Book book = repository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        book.setFinished(true);
        repository.save(book);
    }


    public BookDTO getBookDetails(Long bookId, Long userId) {
        Book book = repository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(()-> new RuntimeException("Livro não encontrado"));
        return new BookDTO(book);
    }
}