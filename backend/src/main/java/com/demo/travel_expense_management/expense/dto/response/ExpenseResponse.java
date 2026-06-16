package com.demo.travel_expense_management.expense.dto.response;

import java.time.LocalDate;

public record ExpenseResponse(
    Long id,
    Long expenseTypeId,
    String expenseTypeName,
    LocalDate expenseDate,
    String description,
    Integer amount
) {}
