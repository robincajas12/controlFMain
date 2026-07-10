package com.controlf.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret:controlf-2026-jwt-signing-key-0123456789abcdef0123456789abcdef}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-hours:24}")
    private long expirationHours;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        String normalizedSecret = jwtSecret == null ? "" : jwtSecret.trim();
        if (normalizedSecret.isBlank()) {
            normalizedSecret = "controlf-2026-jwt-signing-key-0123456789abcdef0123456789abcdef";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(normalizedSecret.getBytes(StandardCharsets.UTF_8));
            this.secretKey = Keys.hmacShaKeyFor(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to initialize JWT signing key", e);
        }
    }

    public String generateToken(String email, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationHours * 3600);
        return Jwts.builder()
                .claims(Map.of("email", email, "role", role))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
