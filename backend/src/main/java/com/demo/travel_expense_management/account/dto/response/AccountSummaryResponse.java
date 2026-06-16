package com.demo.travel_expense_management.account.dto.response;

import com.demo.travel_expense_management.account.entity.AccountType;

public record AccountSummaryResponse(
    Long id,
    AccountType accountType,
    String bankName,
    String accountNumber,
    boolean active
) {}
