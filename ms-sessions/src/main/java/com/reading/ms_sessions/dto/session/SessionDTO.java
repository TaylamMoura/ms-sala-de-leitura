package com.reading.ms_sessions.dto.session;

import com.reading.ms_sessions.entity.ReadingSession;

public record SessionDTO(
        Long bookId,
        int pagesRead,
        int readingTime,
        int lastPage,
        String coverUrl) {

    public SessionDTO(ReadingSession readingSession){

        this(
                readingSession.getBookId(),
                readingSession.getEndPage() - readingSession.getStartPage(),
                readingSession.getReadingTime(),
                readingSession.getEndPage(),
                readingSession.getCoverUrl());
    }
}

