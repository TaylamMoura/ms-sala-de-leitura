package com.reading.ms_auth.dto;

import com.reading.ms_auth.entity.User;

public record NameResponseDTO(
        String name) {

    public NameResponseDTO(User user){
        this(user.getName());
    }
}