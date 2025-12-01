package com.reading.ms_auth.dto;

import com.reading.ms_auth.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(
        @NotBlank(message = "O email é obrigatório!")
        @Email(message = "Escreva um email válido!")
        String email,

        @NotBlank(message = "Digite sua password.")
        @Size(min = 8, message = "A password deve ter no mínimo 8 números")
        String password) {


}