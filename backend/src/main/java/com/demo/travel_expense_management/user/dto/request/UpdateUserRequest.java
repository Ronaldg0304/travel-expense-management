package com.demo.travel_expense_management.user.dto.request;

import com.demo.travel_expense_management.user.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @Size(max = 20) String phone,
    @Size(max = 100) String position,
    @NotNull Role role,
    @NotNull Long departmentId,
    @NotNull Boolean active
) {}
