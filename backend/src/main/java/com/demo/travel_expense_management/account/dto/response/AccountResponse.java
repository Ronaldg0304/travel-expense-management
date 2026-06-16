package com.demo.travel_expense_management.account.dto.response;

import java.time.LocalDateTime;

import com.demo.travel_expense_management.account.entity.AccountType;

public record AccountResponse(
    Long id,
    Long userId,
    String userFullName,
    AccountType accountType,
    String bankName,
    String accountNumber,
    String accountHolderName,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
