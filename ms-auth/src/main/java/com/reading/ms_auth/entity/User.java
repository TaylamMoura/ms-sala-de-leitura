package com.reading.ms_auth.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity(name = "User")
@Table(name = "users")
@EqualsAndHashCode(of = "id")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Defina um name de usuário!")
    private String username;

    @NotEmpty(message = "O email é obrigatório!")
    @Email(message = "Escreva um email válido!")
    private String email;

    @NotEmpty(message = "Defina uma password.")
    @Size(min = 8, message = "A password deve ter no mínimo 8 números")
    @Column(name = "password_hash")
    private String password;

    @Column(name = "creation_date", updatable = false)
    @CreationTimestamp
    private Timestamp creationDate;

    @NotEmpty(message = "Digite seu nome!")
    private String name;

    @Column(name = "birth_date")
    @NotNull(message = "Digite sua data de aniversário.")
    private LocalDate birthDate;


    //Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public User(Long id, String username, String email, String password, Timestamp dataCriacao, String name, LocalDate birthDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creationDate = dataCriacao;
        this.name = name;
        this.birthDate = birthDate;
    }

    public User(){

    }
}