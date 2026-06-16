package com.demo.travel_expense_management.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
    @NotBlank @Size(max = 100) String bankName,
    @NotBlank @Size(max = 150) String accountHolderName,
    boolean active
) {}
