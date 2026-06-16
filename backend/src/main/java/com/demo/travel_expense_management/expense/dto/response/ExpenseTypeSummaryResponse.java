package com.demo.travel_expense_management.expense.dto.response;

public record ExpenseTypeSummaryResponse(
    Long id,
    String code,
    String name,
    boolean active
) {}
