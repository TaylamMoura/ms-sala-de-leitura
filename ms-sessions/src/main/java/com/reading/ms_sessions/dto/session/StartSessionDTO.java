package com.reading.ms_sessions.dto.session;

import jakarta.validation.constraints.NotNull;

public record StartSessionDTO(
        @NotNull
        Long bookId
) {
}
