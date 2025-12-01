package com.reading.ms_auth.dto;

import com.reading.ms_auth.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserRegisterDTO(
        @NotEmpty
        String name,

        @NotNull
        LocalDate birthDate,

        @NotEmpty
        String username,

        @NotEmpty
        @Email
        String email,

        @NotEmpty
        @Size(min = 8, message = "A password deve ter no mínimo 8 números")
        String password
) {
    public UserRegisterDTO(User user){
        this(user.getName(), user.getBirthDate(), user.getUsername(), user.getEmail(), user.getPassword());
    }
}