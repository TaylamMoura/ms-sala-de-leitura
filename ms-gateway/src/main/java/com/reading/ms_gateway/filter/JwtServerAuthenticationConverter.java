package com.reading.ms_gateway.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange){
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            return Mono.just(new UsernamePasswordAuthenticationToken(null, token));
        }

        return Mono.empty();
    }
}
/* Função desta classe: Extrair o token da requisição
ele não valida, apenas converter a requisição para algo que o Manager possa processar
 */