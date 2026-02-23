package com.reading.ms_sessions.dto;


import com.reading.ms_sessions.entity.ReadingSession;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionDTO(
        Long userId,
        Long bookId,
        int pagesRead,
        int readingTime,
        int lastPage,
        String coverUrl) {

    public SessionDTO(ReadingSession readingSession){

        this(
                readingSession.getUserId(),
                readingSession.getBookId(),
                readingSession.getEndPage() - readingSession.getStartPage(),
                readingSession.getReadingTime(),
                readingSession.getEndPage(),
                readingSession.getCoverUrl());
    }
}

