package com.demo.travel_expense_management.disbursement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterDisbursementRequest(
    @NotNull @Positive Integer disbursedAmount,
    @Size(max = 1000) String adjustmentJustification
) {}
