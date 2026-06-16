package com.demo.travel_expense_management.department.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDepartmentRequest(
    @NotBlank @Size(max = 100) String name,
    boolean active
) {}
