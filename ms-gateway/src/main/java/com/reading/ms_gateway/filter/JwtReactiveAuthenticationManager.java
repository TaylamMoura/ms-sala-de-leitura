package com.reading.ms_gateway.filter;

import com.reading.ms_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        String token = authentication.getCredentials().toString();

        if (jwtService.isTokenValid(token)) {
            Claims claims = jwtService.getClaims(token);
            String email = claims.get("email", String.class);
            String userId = claims.getSubject();

            return Mono.just(new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            ));
        }

        return Mono.empty();
    }
}
/* Função desta classe: validar token e criar usuário autenticado
usa o JwtService para verificar a validade do token
 */