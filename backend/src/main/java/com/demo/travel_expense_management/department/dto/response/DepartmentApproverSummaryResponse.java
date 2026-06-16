package com.demo.travel_expense_management.department.dto.response;

public record DepartmentApproverSummaryResponse(
    Long id,
    String departmentName,
    String userFullName,
    boolean active
) {}
