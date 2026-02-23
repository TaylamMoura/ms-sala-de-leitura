package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.EndSessionDTO;
import com.reading.ms_sessions.dto.SessionDTO;
import com.reading.ms_sessions.dto.StartSessionDTO;
import com.reading.ms_sessions.entity.ReadingSession;
import com.reading.ms_sessions.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessao-leitura")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService){
        this.sessionService = sessionService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<SessionDTO> startSession(@Valid @RequestBody StartSessionDTO dto) {

        ReadingSession readingSession = sessionService.startSession(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SessionDTO(readingSession));
    }

    @PostMapping("/finalizar")
    public ResponseEntity<SessionDTO> finishedSession(@Valid @RequestBody EndSessionDTO dto) {
        ReadingSession readingSession = sessionService.endSession(dto);

        return ResponseEntity.status(HttpStatus.OK).body(new SessionDTO(readingSession));
    }

    @GetMapping("/ultima-pagina/{bookId}")
    public ResponseEntity<Integer> getLastPage(@PathVariable Long bookId){
        try {
            int lastPage = sessionService.getLastReadPage(bookId);
            return ResponseEntity.ok(lastPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
