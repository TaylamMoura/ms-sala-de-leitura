package com.reading.ms_auth.controller;

import com.reading.ms_auth.dto.UserRegisterDTO;
import com.reading.ms_auth.dto.UserLoginDTO;
import com.reading.ms_auth.dto.NameResponseDTO;
import com.reading.ms_auth.entity.User;
import com.reading.ms_auth.security.JwtService;
import com.reading.ms_auth.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;


    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegisterDTO cadastroDTO){
        User novoUser = userService.registerUser(cadastroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NameResponseDTO(novoUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO,
                                          HttpServletResponse response) {
        try {
            boolean validCredentials = userService.validateCredentials(loginDTO.email(), loginDTO.password());

            if (validCredentials) {
                String token = jwtService.generateToken(loginDTO.email());

                // Criação do cookie com o token
                ResponseCookie cookie = ResponseCookie.from("jwt", token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(3600)
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                // Busca o usuário pelo email
                User user = userService.findUserByEmail(loginDTO.email());

                // Retorna token + userId no corpo da resposta
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("userId", user.getId());

                return ResponseEntity.ok(responseBody);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("mensagem", "Credenciais inválidas"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensagem", "Erro interno no servidor"));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            Claims claims = jwtService.validateToken(token.replace("Bearer ", ""));
            String email = claims.getSubject();
            return ResponseEntity.ok("Token válido para o email: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
        }
    }
}

//para colocar mensagens no status, usa-se <?> ao inves de <Usuario>