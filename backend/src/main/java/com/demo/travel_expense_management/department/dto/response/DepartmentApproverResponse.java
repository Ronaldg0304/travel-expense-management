package com.demo.travel_expense_management.department.dto.response;

import java.time.LocalDateTime;

public record DepartmentApproverResponse(
    Long id,
    Long departmentId,
    String departmentCode,
    String departmentName,
    Long userId,
    String userFullName,
    String userEmail,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
