package com.demo.travel_expense_management.expense.dto.response;

import java.time.LocalDateTime;

public record ExpenseTypeResponse(
    Long id,
    String code,
    String name,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
