package com.example.ApiRestSecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/*onfiguration
public class DisableWebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Deshabilitar CSRF (opcional)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Permitir todas las solicitudes
                )
                .formLogin(form -> form.disable())  // Deshabilitar formulario de login
                .httpBasic(basic -> basic.disable());  // Deshabilitar autenticación básica

        return http.build();
    }
}*/
