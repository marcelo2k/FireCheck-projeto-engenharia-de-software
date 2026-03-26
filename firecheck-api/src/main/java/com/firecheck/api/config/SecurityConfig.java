package com.firecheck.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita proteção CSRF para facilitar o desenvolvimento
            
            // --- CORREÇÃO: LIBERAR FRAMES PARA O H2 CONSOLE ---
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) 
            // --------------------------------------------------

            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite acesso a tudo (o controle de login é feito no frontend)
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}