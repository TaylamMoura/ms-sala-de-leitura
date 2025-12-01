package com.reading.ms_auth.dto;

import com.reading.ms_auth.entity.User;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

//Para entrada de dados
public record UserRegisterDTO(
        @NotBlank(message = "Digite seu nome!")
        String name,

        @NotNull(message = "Digite sua data de aniversário.")
        LocalDate birthDate,

        @NotBlank(message = "O email é obrigatório!")
        @Email(message = "Escreva um email válido!")
        String email,

        @NotBlank(message = "Defina uma senha.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String password
) { }