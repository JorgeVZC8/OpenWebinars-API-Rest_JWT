package com.example.ApiRestSecurity.security;

import com.example.ApiRestSecurity.security.errorHandling.JwtAccessDeniedHandler;
import com.example.ApiRestSecurity.security.errorHandling.JwtAuthenticationEntryPoint;
import com.example.ApiRestSecurity.security.jwt.JwtAuthenticationFilter;
import com.example.ApiRestSecurity.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint) // Manejo de autenticación fallida
                        .accessDeniedHandler(accessDeniedHandler) // Manejo de accesos denegados
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Aplicación sin estado
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/note/**").hasRole("USER") // Restringir "/note/**" a USER
                        .requestMatchers("/auth/register/admin/").hasRole("ADMIN") // Restringir "/auth/register/admin/" a ADMIN
                        .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Deshabilitar opciones de frame
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return authenticationProvider;
    }
}
