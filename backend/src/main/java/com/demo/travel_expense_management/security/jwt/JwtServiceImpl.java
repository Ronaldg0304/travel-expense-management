package com.demo.travel_expense_management.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.demo.travel_expense_management.security.config.JwtProperties;
import com.demo.travel_expense_management.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.expirationMinutes() * 60 * 1000);

        return Jwts.builder()
            .subject(user.getEmail())
            .claim("role", user.getRole().name())
            .claim("userId", user.getId())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(getSigningKey())
            .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            return username.equals(user.getEmail()) && !isTokenExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }
}
