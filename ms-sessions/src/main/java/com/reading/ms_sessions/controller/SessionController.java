package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.SessionDTO;
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
    public ResponseEntity<SessionDTO> startSession(@Valid @RequestBody SessionDTO sessionDTO) {

        ReadingSession readingSession = sessionService.startSession(
                sessionDTO.userId(),
                sessionDTO.bookId()
        );

        SessionDTO dto = new SessionDTO(readingSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/finalizar")
    public ResponseEntity<SessionDTO> finishedSession(@Valid @RequestBody SessionDTO sessionDTO) {
        ReadingSession readingSession = sessionService.endSession(
                sessionDTO.userId(),
                sessionDTO.bookId(),
                sessionDTO.lastPage(),
                sessionDTO.readingTime()
        );

        SessionDTO dto = new SessionDTO(readingSession);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
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
