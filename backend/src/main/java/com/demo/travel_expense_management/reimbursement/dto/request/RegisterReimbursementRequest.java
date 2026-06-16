package com.demo.travel_expense_management.reimbursement.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterReimbursementRequest(
    @NotNull Long accountId,
    @NotBlank String paymentReference,
    @NotNull LocalDate paymentDate,
    String comments
) {}
