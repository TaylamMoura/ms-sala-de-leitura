package com.reading.ms_sessions.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "sessionId")
@Entity(name = "reading_sessions")
@Table(name = "sessions")

public class ReadingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private  LocalDateTime endTime;

    @Column(name = "start_page")
    private int startPage;

    @Column(name = "end_page")
    private int endPage;

    @Column(name = "reading_time")
    private int readingTime;

    @Column(name = "cover_url")
    private String coverUrl;

    //Antes de salvar uma nova SessoesDeLeitura, este método define inicioSessao com a data e hora atuais
    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

}
