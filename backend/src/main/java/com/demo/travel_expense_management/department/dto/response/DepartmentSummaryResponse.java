package com.demo.travel_expense_management.department.dto.response;

public record DepartmentSummaryResponse(
    Long id,
    String code,
    String name,
    boolean active
) {}
