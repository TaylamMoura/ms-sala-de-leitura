
package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.BookStatisticsDTO;
import com.reading.ms_sessions.dto.OverallStatisticsDTO;
import com.reading.ms_sessions.service.StatisticsService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/estatisticas")
public class StatisticsController {

    private final StatisticsService statisticsService;


    @Autowired
    public StatisticsController(StatisticsService statisticsService){
        this.statisticsService = statisticsService;
    }


    //ESTATÍSTICA DO LIVRO DO USUÁRIO
    @GetMapping("/livro/{bookId}")
    public ResponseEntity<?> getBookStatistcs(@RequestHeader("Authorization") String token, @PathVariable Long bookId){
        try{
            BookStatisticsDTO dto = statisticsService.bookStatistics(token, bookId);
            return ResponseEntity.ok(dto);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido ou expirado");
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar as estatísticas do livro");
        }

    }


    //ESTATÍSTICA GERAL DO USUÁRIO
    @GetMapping("/geral")
    public ResponseEntity<?> getOverallStatistics(@RequestHeader("Authorization") String token) {
        try {
            OverallStatisticsDTO dto = statisticsService.overallStatistics(token);
            return ResponseEntity.ok(dto);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido ou expirado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar estatísticas gerais.");
        }
    }
}
