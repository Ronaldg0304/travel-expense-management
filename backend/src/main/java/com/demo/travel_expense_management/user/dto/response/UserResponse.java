package com.demo.travel_expense_management.user.dto.response;

import java.time.LocalDateTime;

import com.demo.travel_expense_management.user.entity.DocumentType;
import com.demo.travel_expense_management.user.entity.Role;

public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    DocumentType documentType,
    String documentNumber,
    String phone,
    String position,
    Role role,
    Boolean active,
    Long departmentId,
    String departmentName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
