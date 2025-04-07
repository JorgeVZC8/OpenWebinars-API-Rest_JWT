package com.example.ApiRestSecurity.security.jwt;

import com.example.ApiRestSecurity.user.model.UserEntity;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Log
public class JwtProvider {

    public static final String TOKEN_TYPE= "JWT";
    public static final String TOKEN_HEADER= "Authorization";
    public static final String TOKEN_PREFIX= "Bearer";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.duration}")
    private Long jwtLifeInDays;

    private JwtParser jwtParser;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // Convertimos el secret en una clave válida para HS256
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Creamos el parser para luego validar tokens
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    public String generateToken(Authentication authentication){
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Date tokenExpirationDateTime=
                Date.from(
                        LocalDateTime
                                .now()
                                .plusDays(jwtLifeInDays)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                );

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(tokenExpirationDateTime)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            jwtParser.parseClaimsJwt(token);
            return true;
        }catch(SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException exception){
            log.info("Error con el token" + exception.getMessage());
        }
        return false;
    }

}

