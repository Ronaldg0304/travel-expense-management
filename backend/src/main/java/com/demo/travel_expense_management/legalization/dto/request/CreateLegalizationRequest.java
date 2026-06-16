package com.demo.travel_expense_management.legalization.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateLegalizationRequest(
    @NotNull Long travelRequestId,
    @NotNull Long costCenterId,
    @NotEmpty @Valid List<CreateExpenseRequest> expenses
) {}
