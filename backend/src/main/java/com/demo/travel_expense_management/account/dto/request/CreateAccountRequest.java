package com.demo.travel_expense_management.account.dto.request;

import com.demo.travel_expense_management.account.entity.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotNull Long userId,
    @NotNull AccountType accountType,
    @NotBlank @Size(max = 100) String bankName,
    @NotBlank @Size(max = 50) String accountNumber,
    @NotBlank @Size(max = 150) String accountHolderName,
    boolean active
) {}
