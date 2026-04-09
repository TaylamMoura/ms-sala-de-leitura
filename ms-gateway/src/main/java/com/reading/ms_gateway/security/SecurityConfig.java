package com.reading.ms_gateway.security;

import com.reading.ms_gateway.filter.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtReactiveAuthenticationManager authManager) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authManager);
        jwtFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());

        return http
                // 1. Desabilita o CSRF (essencial para POST de outras origens)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. Ativa o suporte a CORS usando o bean abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        // 3. LIBERA O OPTIONS TOTALMENTE
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .pathMatchers("/usuarios/login", "/usuarios/login/**").permitAll()
                        .pathMatchers("/actuator/**", "/auth/**").permitAll()
                        .pathMatchers("/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**").permitAll()
                        .pathMatchers(
                                "/ms-auth/v3/api-docs/**",
                                "/ms-catalog/v3/api-docs/**",
                                "/ms-sessions/v3/api-docs/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                // 4. Garante que o filtro de JWT só rode após a autenticação básica e CORS
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8085"); // URL do seu Front
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

/* Define quais sao as rotas publicas e as autenticadas
Registra o AuthenticationWebFilter, que vai chaar o converter e o manager para validar o jwt
 */
