package com.reading.ms_auth.service;


import com.reading.ms_auth.dto.UserRegisterDTO;
import com.reading.ms_auth.entity.User;
import com.reading.ms_auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateToken(String email) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora de validade
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Transactional
    public User registerUser(UserRegisterDTO registerDTO) {
        User user = new User();
        user.setName(registerDTO.name());
        user.setBirthDate(registerDTO.birthDate());
        user.setEmail(registerDTO.email());
        user.setUsername(registerDTO.username());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));

        return userRepository.save(user);
    }

    public boolean validateCredentials(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }


    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com o e-mail " + email + " não foi encontrado."));
        return user;
    }
}