package com.demo.travel_expense_management.user.dto.response;

import com.demo.travel_expense_management.user.entity.Role;

public record UserSummaryResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Role role,
    Boolean active,
    String departmentName
) {}
