package com.demo.travel_expense_management.user.dto.request;

import com.demo.travel_expense_management.user.entity.DocumentType;
import com.demo.travel_expense_management.user.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotBlank @Email @Size(max = 150) String email,
    @NotNull DocumentType documentType,
    @NotBlank @Size(max = 30) String documentNumber,
    @Size(max = 20) String phone,
    @Size(max = 100) String position,
    @NotNull Role role,
    @NotNull Long departmentId
) {}
