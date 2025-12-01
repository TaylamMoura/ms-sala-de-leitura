package com.reading.ms_auth.dto;

import com.reading.ms_auth.entity.User;

public record UsernameResponseDTO(
        String username) {

    public UsernameResponseDTO(User user){
        this(user.getUsername());
    }
}