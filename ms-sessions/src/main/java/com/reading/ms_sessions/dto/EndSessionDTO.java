package com.reading.ms_sessions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EndSessionDTO(
        @NotNull
        Long userId,

        @NotNull
        Long bookId,

        @Min(1)
        int lastPage,

        @Min(1)
        int readingTime
) {
}
