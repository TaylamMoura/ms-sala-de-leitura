package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.session.EndSessionDTO;
import com.reading.ms_sessions.dto.session.SessionDTO;
import com.reading.ms_sessions.dto.session.StartSessionDTO;
import com.reading.ms_sessions.entity.ReadingSession;
import com.reading.ms_sessions.repository.SessionsRepository;
import com.reading.ms_sessions.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessao-leitura")
public class SessionController {

    private final SessionService sessionService;
    private final SessionsRepository sessionsRepository;

    public SessionController(SessionService sessionService, SessionsRepository sessionsRepository){
        this.sessionService = sessionService;
        this.sessionsRepository = sessionsRepository;
    }


    @PostMapping("/iniciar")
    public ResponseEntity<SessionDTO> startSession(@Valid @RequestBody StartSessionDTO dto,
                                                   @RequestHeader("X-User-Id") Long userId) {

        ReadingSession readingSession = sessionService.startSession(dto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SessionDTO(readingSession));
    }


    @PostMapping("/finalizar")
    public ResponseEntity<SessionDTO> finishedSession(@Valid @RequestBody EndSessionDTO dto,
                                                      @RequestHeader("X-User-Id") Long userId) {
        ReadingSession readingSession = sessionService.endSession(dto, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new SessionDTO(readingSession));
    }


    @GetMapping("/ultima-pagina/{bookId}")
    public ResponseEntity<Integer> getLastPage(@RequestHeader("X-User-Id") Long userId,
                                               @PathVariable Long bookId){
        int lastPage = sessionsRepository.findTopByUserIdAndBookIdOrderByEndTimeDesc(userId, bookId)
                .map(ReadingSession::getEndPage)
                .orElse(0);
        return ResponseEntity.ok(lastPage);
    }
}
