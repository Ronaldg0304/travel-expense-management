package com.demo.travel_expense_management.security.dto.response;

import com.demo.travel_expense_management.user.entity.Role;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserInfo user
) {

    public LoginResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }

    public record UserInfo(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
    ) {}
}
