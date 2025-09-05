package com.rookies4.every_moment.security;


import com.rookies4.myspringboot3project.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUserDetailsService userDetailsService;

    private SecretKey key;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp-minutes:60}")
    private long accessExpMinutes;

    @Value("${jwt.refresh-exp-days:14}")
    private long refreshExpDays;

    private SecretKey getKey() {
        if (key == null) {
            // Expect >= 32 bytes
            key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessExpMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshExpDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getKey())
                .compact();
    }

    public boolean validate(String token) {
        Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
        return true;
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parse(token);
        String email = claims.getSubject();
        var userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}