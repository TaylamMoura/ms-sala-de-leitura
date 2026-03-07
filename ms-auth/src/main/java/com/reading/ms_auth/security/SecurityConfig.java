package com.reading.ms_auth.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;


@Configuration
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JWTAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                // 1. Desabilita o CORS no MS (O Gateway já cuidou disso!)
                .cors(cors -> cors.disable())

                .authorizeHttpRequests(authz -> authz
                        // 2. Garante que qualquer requisição OPTIONS passe reto
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(EndpointRequest.to("health")).permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/usuarios/validate-token").permitAll()
                        .requestMatchers(
                                "/usuarios",
                                "/usuarios/login",
                                "/inicio.html",
                                "/cadastro.html",
                                "/index.html",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/logout"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}