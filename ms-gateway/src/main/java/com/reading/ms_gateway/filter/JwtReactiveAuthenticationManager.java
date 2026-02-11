package com.reading.ms_gateway.filter;

import com.reading.ms_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtReactiveAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof  BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .flatMap(bearer -> {
                    String token = bearer.getToken();

                    if (jwtService.isTokenValid(token)) {
                        Claims claims = jwtService.getClaims(token);
                        String email = claims.get("email", String.class);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                email, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                        return Mono.just(auth);
                    }
                    return Mono.error(new BadCredentialsException("Token JWT inválido"));
                });
    }
}

/* Função desta classe: validar token e criar usuário autenticado
usa o JwtService para verificar a validade do token
 */