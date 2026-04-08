
package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.BookStatisticsDTO;
import com.reading.ms_sessions.dto.OverallStatisticsDTO;
import com.reading.ms_sessions.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> getBookStatistcs(@RequestHeader("X-User-Id") Long userId,
                                              @PathVariable Long bookId) {

        BookStatisticsDTO dto = statisticsService.bookStatistics(userId, bookId);
        return ResponseEntity.ok(dto);
    }


    //ESTATÍSTICA GERAL DO USUÁRIO
    @GetMapping("/geral")
    public ResponseEntity<?> getOverallStatistics(@RequestHeader("X-User-Id") Long userId) {

        OverallStatisticsDTO dto = statisticsService.overallStatistics(userId);
        return ResponseEntity.ok(dto);
    }
}
