package com.reading.ms_sessions.service;

import com.reading.ms_sessions.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogClient {

    private final RestTemplate restTemplate;

    @Autowired
    public CatalogClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public BookDTO getBook(Long bookId, String token){
        String endpoint = "http://ms-catalog/livros/exibirDados/" + bookId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<BookDTO> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    entity,
                    BookDTO.class
            );
            return response.getBody();

        } catch (Exception e){
            throw new RuntimeException("Livro não encontrado: " + e.getMessage());

        }
    }

    public void markBookAsFinished(Long bookId, String token) {
        String endpoint = "http://ms-catalog/livros/" + bookId + "/finalizar";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            restTemplate.exchange(
                    endpoint,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );
        } catch (Exception e ){
            throw new RuntimeException("Erro ao marcar livro como Finalizado");
        }
    }
}


