package com.demo.travel_expense_management.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
    String secretKey,
    long expirationMinutes
) {}
