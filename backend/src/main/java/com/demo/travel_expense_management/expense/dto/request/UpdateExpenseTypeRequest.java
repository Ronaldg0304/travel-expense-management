package com.demo.travel_expense_management.expense.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateExpenseTypeRequest(
    @NotBlank @Size(max = 100) String name,
    boolean active
) {}
