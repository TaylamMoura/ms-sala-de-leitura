package com.reading.ms_catalog.service;

import com.reading.ms_catalog.dto.UserIdResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

//Serviço para se comunicar com outro Microsserviço de autenticação

@Service
public class AuthClientService {

    private final String authServiceName;
    private final String authServicePath;
    private final RestTemplate restTemplate;

    public AuthClientService(RestTemplate restTemplate,
                             @Value("${spring.application.name}") String authServiceName,
                             @Value("${auth.service.path}") String authServicePath) {
        this.restTemplate = restTemplate;
        this.authServiceName = authServiceName;
        this.authServicePath = authServicePath;
    }

    // Método que recebe um token JWT e consulta o MS-Auth para descobrir o ID do usuário

    public Long buscarUsuarioId(String tokenJWT) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenJWT);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String endpoint = "http://" + authServiceName + authServicePath + "/user-id";

        try {
            ResponseEntity<UserIdResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    entity,
                    UserIdResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return  response.getBody().userId();
            }


        } catch (Exception e) {
            System.err.println("Falha ao se comunicar com MS-Auth: " + e.getMessage());
            throw new RuntimeException("Erro de comunicação com serviço de autenticação.");
        }
        throw new RuntimeException("ID de usuário não encontrado ou autenticação inválida.");
    }
}
