package com.demo.travel_expense_management.department.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateDepartmentApproverRequest(
    @NotNull Long departmentId,
    @NotNull Long userId,
    boolean active
) {}
