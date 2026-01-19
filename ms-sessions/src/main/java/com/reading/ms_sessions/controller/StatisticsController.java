package com.reading.ms_sessions.controller;

import com.reading.ms_sessions.dto.BookStatisticsDTO;
import com.reading.ms_sessions.dto.OverallStatisticsDTO;
import com.reading.sala_de_leitura.dto.EstatisticaGeralDTO;
import com.reading.sala_de_leitura.dto.EstatisticaLivroDTO;
import com.reading.sala_de_leitura.entity.Usuario;
import com.reading.sala_de_leitura.service.EstatisticaService;
import com.reading.sala_de_leitura.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

@RestController
public class StatisticsController {

    private final EstatisticaService estatisticaService;


    @Autowired
    public StatisticsController(EstatisticaService estatisticaService){
        this.estatisticaService = estatisticaService;
    }


    //ESTATÍSTICA DO LIVRO DO USUÁRIO
    @GetMapping("/estatistica-livro")
    public ResponseEntity<BookStatisticsDTO> getBookStatistcs(@RequestParam("bookId") Long bookId, Authentication authentication){
        return ResponseEntity.ok(estatisticaService.estatisticasLivro(bookId, usuarioLogado(authentication)));
    }


    //ESTATÍSTICA GERAL DO USUÁRIO
    @GetMapping("/estatistica-geral")
    public ResponseEntity<OverallStatisticsDTO> getOverallStatistics(Authentication authentication) {
        try {
            OverallStatisticsDTO statistics = estatisticaService.estatisticaGeral(usuarioLogado(authentication));
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OverallStatisticsDTO(Collections.emptyList(), 0L, 0, 0));
        }
    }
}