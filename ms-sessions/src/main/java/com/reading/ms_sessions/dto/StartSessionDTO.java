package com.reading.ms_sessions.dto;

import jakarta.validation.constraints.NotNull;

public record StartSessionDTO(
        @NotNull
        Long userId,

        @NotNull
        Long bookId
) {
}
