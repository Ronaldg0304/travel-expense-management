package com.demo.travel_expense_management.approval.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApproveTravelRequestRequest(
    @NotNull @Positive Integer approvedAmount
) {}
