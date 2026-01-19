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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BookService {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private final APIConnection apiConnection;
    private final BookRepository repository;

    @Autowired
    public BookService(APIConnection apiConnection, BookRepository repository) {
        this.apiConnection = apiConnection;
        this.repository = repository;
    }

    public BookDTO searchByTitle(String bookTitle) {
        try {
            String titleEncoded = URLEncoder.encode("intitle:" + bookTitle, StandardCharsets.UTF_8.toString());
            String jsonResponse = apiConnection.booksJson(titleEncoded);

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null && !items.isEmpty()) {
                JsonObject item = items.get(0).getAsJsonObject();

                JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");
                JsonObject saleInfo = item.getAsJsonObject("saleInfo");

                String title = volumeInfo.get("title").getAsString();
                String author = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
                int pageCount = volumeInfo.get("pageCount").getAsInt();
                String thumbnail = volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
                String publishedDate = volumeInfo.get("publishedDate").getAsString();
                int year = publishedDate != null ? Integer.parseInt(publishedDate.split("-")[0]) : 0;
                String country = saleInfo != null && saleInfo.has("country") ? saleInfo.get("country").getAsString() : "N/A";

                return new BookDTO(null, title, author, pageCount, thumbnail, year, false, country);

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
    public void saveBook(BookDTO bookDTO, Long userId){

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
                userId,
                false,
                bookDTO.country()
        );

        repository.save(book);
    }


    public List<BookDTO> listSavedBooks(Long userId){
        List<Book> books = repository.findByUserId(userId);
        return books.stream().map(BookDTO::new).toList();
    }


    @Transactional
    public void deleteBook(Long id, Long userId){
        Book book = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ValidationException("Livro não escontrado"));

        repository.delete(book);
    }


    @Transactional
    public Book updateBook(Long id, BookUpdate bookUpdate, Long userId) {
        Book book = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ValidationException("Livro não escontrado"));

        book.update(bookUpdate);
        repository.save(book);
        return book;
    }

    @Transactional
    public void markAsFinished(Long id, Long userId){
        Book book = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ValidationException("Livro não encontrado"));

        book.setFinished(true);
        repository.save(book);
    }


    public BookDTO getBookDetails(Long id, Long userId) {
        Book book = repository.findByIdAndUserId(id, userId)
                .orElseThrow(()-> new ValidationException("Livro não encontrado"));
        return  new BookDTO(book);
    }
}