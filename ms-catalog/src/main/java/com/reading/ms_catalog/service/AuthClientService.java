package com.reading.ms_catalog.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

//Serviço para se comunicar com o Microsserviço de autenticação

@Service
public class AuthClientService {

    private final String authServiceName = "ms-auth";
    private final RestTemplate restTemplate;

    public AuthClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    //Valida Token no ms-auth e retorna o userID
    public Long validateToken(String token) {
        String endpoint = "http://ms-auth/usuarios/validate-token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Long> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    entity,
                    Long.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Falha ao validar token no MS-Auth: " + e.getMessage());
        }
    }
}

