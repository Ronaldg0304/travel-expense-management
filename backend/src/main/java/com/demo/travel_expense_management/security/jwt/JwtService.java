package com.demo.travel_expense_management.security.jwt;

import java.util.Date;

import com.demo.travel_expense_management.user.entity.User;

public interface JwtService {

    String generateToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);

    Date extractExpiration(String token);
}
