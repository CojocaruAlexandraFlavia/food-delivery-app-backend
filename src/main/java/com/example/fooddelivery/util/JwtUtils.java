package com.example.fooddelivery.util;

import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${application.jwt.tokenAvailability}")
    private int jwtAvailability;

    @Value("${application.jwt.tokenPrefix}")
    private String tokenPrefix;

    private final SecretKey secretKey;

    @Autowired
    public JwtUtils(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateJwtToken(@NotNull Authentication authentication){
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("authorities", userPrincipal.getAuthorities())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtAvailability)))
                .signWith(secretKey)
                .compact();
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }
}
