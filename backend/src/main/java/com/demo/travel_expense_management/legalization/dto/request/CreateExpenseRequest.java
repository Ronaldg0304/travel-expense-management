package com.demo.travel_expense_management.legalization.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest(
    @NotNull Long expenseTypeId,
    @NotNull LocalDate expenseDate,
    @NotBlank @Size(max = 500) String description,
    @NotNull @Positive Integer amount
) {}
