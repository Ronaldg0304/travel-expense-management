package com.demo.travel_expense_management.refund.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRefundRequest(
    @NotBlank String companyAccount,
    @NotBlank String refundReference,
    @NotNull LocalDate refundDate,
    String comments
) {}
